import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.slf4j.event.Level

plugins {
    idea
    java
    `maven-publish`
    alias(libs.plugins.modDevGradle)
}

// Mod stuff
val modId: String by project
val modName: String by project
val modLicense: String by project
val modVersion: String by project
val modGroupId: String by project
val modAuthors: String by project
val modDescription: String by project

val datagenOutput: String = "src/generated/resources"

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val modReplacementProperties = mapOf(
        "mod_id" to modId,
        "mod_name" to modName,
        "mod_version" to modVersion,
        "mod_license" to modLicense,
        "mod_authors" to modAuthors,
        "mod_description" to modDescription,
        "minecraft_version_range" to "[${libs.versions.minecraft.get()}]",
        "loader_version_range" to "[1,)",
        "neo_version_range" to "[${libs.versions.neforge.get()},)",
        "tfc_version_range" to "[${libs.versions.tfc.get()},)"
    )
    inputs.properties(modReplacementProperties)
    expand(modReplacementProperties)
    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}

base {
    archivesName.set("$modId-${libs.versions.minecraft.get()}")
    version = modVersion
    group = modGroupId
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withSourcesJar()
}

sourceSets {
    main {
        resources {
            srcDir(datagenOutput)
            srcDir(generateModMetadata)
        }
    }
    test {
        resources {
            // Pull down our generated data for tests
            srcDir(datagenOutput)
        }
    }
    create("datagen")
}

/**
 * Sets up a dependency configuration called 'localRuntime'.
 * This configuration should be used instead of 'runtimeOnly' to declare
 * a dependency that will be present for runtime testing but that is
 * "optional", meaning it will not be pulled by dependents of this mod.
 */
val localRuntime: Configuration by configurations.creating

configurations.runtimeClasspath.configure {
    extendsFrom(localRuntime)
}

configurations {
    get("datagenCompileClasspath").extendsFrom(compileClasspath.get())
    get("datagenRuntimeClasspath").extendsFrom(runtimeClasspath.get())
}

neoForge {
    version = libs.versions.neforge.get()
    addModdingDependenciesTo(sourceSets["datagen"])
    validateAccessTransformers = true

    parchment {
        minecraftVersion = libs.versions.parchmentMinecraft
        mappingsVersion = libs.versions.parchment
    }

    runs {
        configureEach {
            // Recommended logging data for a userdev environment
            // The markers can be added/remove as needed separated by commas.
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            systemProperty("neoforge.logging.markers", "REGISTRIES")
            logLevel = Level.DEBUG
            systemProperty("neoforge.enabledGameTestNamespaces", modId)

            // Only JBR allows enhanced class redefinition, so ignore the option for any other JDKs
            jvmArguments.addAll("-XX:+IgnoreUnrecognizedVMOptions", "-XX:+AllowEnhancedClassRedefinition", "-ea")
        }

        register("client") {
            client()
            gameDirectory = file("run/client")
        }

        register("server") {
            server()
            gameDirectory = file("run/server")
            programArgument("--nogui")
        }

        register("datagen") {
            data()
            gameDirectory = file("run/datagen")

            // Specify the modid for data generation, where to output the resulting resource, and where to look for existing resources.
            programArguments.addAll(
                "--mod",
                modId,
                "--all",
                "--output",
                file(datagenOutput).path,
                "--existing",
                file("src/main/resources/").path,
                "--existing-mod",
                "tfc"
            )
        }

        register("gameTest") {
            type = "gameTestServer"
            gameDirectory = file("run/game_test")
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.test.get())
            sourceSet(sourceSets["datagen"])
        }
    }

    unitTest {
        enable()
        testedMod = mods[modId]
    }

    ideSyncTask(generateModMetadata)
}

repositories {
    mavenCentral()
    mavenLocal()
    exclusiveContent {
        forRepository { maven { url = uri("https://maven.terraformersmc.com/") } }
        filter { includeGroup("dev.emi") }
    }
    exclusiveContent {
        forRepositories(
            maven { url = uri("https://maven.blamejared.com/") },
            // Mirror for JEI and patchouli
            maven { url = uri("https://modmaven.k-4u.nl") })
        forRepository { maven { url = uri("https://maven.blamejared.com/") } }
        filter {
            includeGroup("mezz.jei")
            includeGroup("vazkii.patchouli")
        }
    }
    exclusiveContent {
        forRepository { maven { url = uri("https://maven.k-4u.nl/") } }
        filter { includeGroup("mcjty.theoneprobe") }
    }
    exclusiveContent {
        forRepository { maven { url = uri("https://www.cursemaven.com") } }
        filter { includeGroup("curse.maven") }
    }
}

dependencies {
    // datagen can use mod code
    "datagenImplementation"(sourceSets["main"].output)

    // Lombok because yes
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // TFC!
    implementation(libs.tfc)
}

idea {
    module {
        // IDEA no longer automatically downloads sources/javadoc jars for dependencies,
        // so we need to explicitly enable the behavior.
        isDownloadSources = true
        isDownloadJavadoc = true

        val elements = arrayOf(
            "run", ".gradle", ".idea", "src/generated/resources/.cache"
        ).map { file(it) }
        excludeDirs.addAll(
            elements
        )
    }
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()

            artifact(tasks.jar)
            artifact(tasks.named("sourcesJar"))
        }
    }

    repositories {
        mavenLocal()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/traister101/datagen-util")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}



tasks {
    named("neoForgeIdeSync") {
        dependsOn(generateModMetadata)
    }

    javadoc {
        options.optionFiles(file("javadoc-options.txt"))
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
        testLogging {
            events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        }
    }
}