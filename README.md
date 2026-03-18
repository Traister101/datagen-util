This library bundles common datagen utilities/providers and other such things so I can easily use and update them across
multiple mods. To see example usage see https://github.com/Traister101/TFC-Addon-Template or most mods I work on.
This will never be published to curse or other mod hosts. Instead, grab it from Up's Maven (shown in example) or
GithubPackages which you can read about how to
do [here](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry)

### Kotlin build file example

This assumes a `datagenUtilsVersion` variable with the version of the library you want to depend on as well as a
`datagen` source set (where all the datagen lives)

```kotlin
repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "up Maven"
                url = uri("https://maven.uuid.gg/releases")
            }
        }
        filter { includeGroup("mod.traister101.datagenutils") }
    }
}

dependencies {
    "datagenImplementation"("mod.traister101.datagenutils:Datagen-Utils-1.21.1:$datagenUtilsVersion")
}
```