This library bundles common datagen utilities/providers and other such things so I can easily use and update them across
multiple mods. For example usage see my [TFC Addon Template](https://github.com/Traister101/TFC-Addon-Template) or most
mods I work on.

This will never be published to curse or other mod hosts. Instead, grab it
from [Up's Maven](https://maven.uuid.gg/#/releases) (shown in [build file example](#kotlin-build-file-example)) or
GithubPackages which you can read about how to
do [here](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry)

## Features

There are 3 main features of this library.

1. We want to consolidate language translation definitions in such a way that providers which would like to define
   language translations as part of their generation can do so, and they'll be merged into the resulting lang file. This
   is accomplished with our custom language provider which can be read about
   in [Language generation](#language-generation).
2. Removing some pain points with vanilla builders/datagen. For example as part of point 1 our custom advancement
   provider and builder allow you to define the *actual* title and description strings inline with the rest of the
   advancement. More about that in [Advancement generation](#advancement-generation)
3. Provide ways to datagen things that mods don't for whatever reason. For example TFC doesn't ship any code to assist
   addons in custom types. (If you depend on a mod that doesn't ship any providers for their custom types and want it
   included I will happily accept PRs)

#### Language generation

Language generation has multiple QOL improvements. Our `EnhancedLanguageProvider` allows returning a set of known
objects permitting the provider to report missed translations. It also supports sub providers allowing easy
encapsulation of specific types. In the example below there are 3 sub providers, one for `Item`s, one for `Block`s and
one for `CreativeTab`s as well as a manual `translation_key: translation` definition for a tooltip. This provider also
supports external providers creating language translations as part of their running such as our
`EnhancedAdvancementProvider` provided for [advancement generation](#advancement-generation) which will automatically
generate the needed language translations when passed to the `EnhancedLanguageProvider`

The setup required looks something like this.

```java
// In datagen entry point
final EnhancedAdvancementProvider builtInAdvancements = generator.addProvider(event.includeServer(),
				BuiltInAdvancements.create(packOutput, lookupProvider, existingFileHelper));

final EnhancedLanguageProvider builtInLanguage = generator.addProvider(event.includeClient(), new BuiltInLanguage(packOutput, lookupProvider));
builtInLanguage.extraLanguage(builtInAdvancements); // Add the advancements as an extra language provider
```

<details>
<summary>Language generation example</summary>

```java
public class BuiltInLanguage extends EnhancedLanguageProvider {

	public BuiltInLanguage(final PackOutput output, final CompletableFuture<Provider> registries) {
		super(output, registries, ExampleMod.MOD_ID, "en_us", List.of(new Items(), new Blocks(), new CreativeTabs()));
	}

	@Override
	protected void addTranslations() {
		add(ExampleItem.EXAMPLE_TOOLTIP, "Example Tooltip");
	}

	private static final class Items extends ItemLanguageProvider {

		Items() {super(ExampleModItems.ITEMS);}

		@Override
		protected void addTranslations(final LanguageOutput<Item> output) {
			output.simple(ExampleModItems.EXAMPLE_ITEM); // Results in "Example Item"
			output.add(ExampleModItems.EXAMPLE_SIMPLE_ITEM, "Simple Example Item");
		}
	}

	private static final class Blocks extends BlockLanguageProvider {

		Blocks() {super(ExampleModBlocks.BLOCKS);}

		@Override
		protected void addTranslations(final LanguageOutput<Block> output) {
			output.simple(ExampleModBlocks.EXAMPLE_BLOCK); // Results in "Example Block"
		}
	}

	private static final class CreativeTabs extends CreativeTabLanguageProvider {

		CreativeTabs() {super(ExampleModCreativeTabs.CREATIVE_MODE_TABS);}

		@Override
		protected void addTranslations(final LanguageOutput<CreativeModeTab> output) {
			output.simple(ExampleModCreativeTabs.EXAMPLE_TAB); // "Example Tab"
		}
	}
}
```

</details>

### Advancement generation

Advancement generation has QOL improvements focused around defining language translations for you inline with the rest
of the advancement definition. If you find the following code appealing you'll like this provider

<details>
<summary>Advancement generation example</summary>

```java
// In some advancement provider
final var root = AdvancementBuilder.root()
		.display(SimpleDisplayInfo.builder()
				.icon(ExampleModItems.EXAMPLE_ITEM)
				.title("Example title") // The actual advancement title, lang is automatically generated
				.description("Example description") // The actual advancement description, lang is automatically generated
				.type(AdvancementType.TASK))
		.addCriterion("unlocked_example_recipe", RecipeUnlockedTrigger.unlocked(ExampleMod.location("example_recipe")))
		.save(output, ExampleMod.location("example/root"));
```

</details>

### Tag generation

Tags much like advancements can have their language translations defined inline.

### Recipe generation

Recipes are mostly vanilla, the main thing this lib brings to the table is `EnhancedRecipeProvider` (a child of vanillas
`RecipeProvider`) which supports external recipes from other providers via `AdditionalRecipeProvider`. The only provider
taking advantage of this is one of the custom TFC data providers.  
Despite the lack of a fancy recipe provider we do provide a custom `CraftingRecipeBuilder` supporting directories as
well as a number of TFC recipe builders which do the same.

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