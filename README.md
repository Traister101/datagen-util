This library bundles common datagen utilities/providers and other such things so I can easily use and update them across
multiple mods. To see example usage see https://github.com/Traister101/TFC-Addon-Template or most mods I work on.
This will never be published to curse or other mod hosts. Instead, grab it from GithubPackages

### Kotlin build file example

This assumes a `datagenUtilsVersion` variable with the version of the library you want to depend on as well as a
`datagen` source set (where all the datagen lives) Additionally, it uses
the [gpr-for-gradle](https://plugins.gradle.org/plugin/io.github.0ffz.github-packages) plugin which makes depending on
GithubPackages more convenient. Consult
the [GithubPackage docs](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry)
for how to do it with Gradle alone

```kotlin
repositories {
    exclusiveContent {
        forRepository {
            githubPackage("traister101/DatagenUtil")
        }
        filter {
            includeGroup("mod.traister101")
        }
    }
}

dependencies {
    "datagenImplementation"("mod.traister101.datagenutils:datagen_utils-1.21.1:$datagenUtilsVersion")
}
```