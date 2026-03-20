package mod.traister101.datagenutils.data.util;

import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.extensions.ILevelExtension;
import org.apache.commons.lang3.StringUtils;

import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;

import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A simple named Lang key and Lang translation pair
 */
@Value
@Getter
@ToString
@Accessors(fluent = true)
public class LanguageTranslation {

	/**
	 * The key
	 */
	String key;
	/**
	 * The translation
	 */
	String translation;

	/**
	 * The constructor
	 *
	 * @param key The key
	 * @param translation The translation
	 */
	@Contract(pure = true)
	public LanguageTranslation(final String key, final String translation) {
		this.key = key;
		this.translation = translation;
	}

	/**
	 * Takes a string like 'dark_oak' and converts it to 'Dark Oak'.
	 *
	 * @param serializedName A serialized name ({@link ResourceLocation#getPath()}) like `dark_oak`
	 *
	 * @return A "lang-ified" serialized name, such as 'dark_oak' -> 'Dark Oak'
	 *
	 * @throws IllegalArgumentException when {@code serializedName} contains a path seperator. This is an error, fix your code
	 */
	public static String langify(final String serializedName) throws IllegalArgumentException {
		if (serializedName.contains("/"))
			throw new IllegalArgumentException("Only 'flat' serialized names permitted (no path separators '/'). '" + serializedName + "'");
		return Arrays.stream(serializedName.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
	}

	/**
	 * {@return a LanguageTranslation}
	 *
	 * @param key The key
	 * @param translation The translation
	 */
	@Contract("_, _ -> new")
	public static LanguageTranslation of(final String key, final String translation) {
		return new LanguageTranslation(key, translation);
	}

	/**
	 * {@return a LanguageTranslation}
	 *
	 * @param block The block
	 * @param name The block name
	 */
	@Contract("_, _ -> new")
	public static LanguageTranslation block(final Block block, final String name) {
		return of(block.getDescriptionId(), name);
	}

	/**
	 * Uses {@link #langify(String)} to create a name from the registry name
	 *
	 * @param block The block
	 *
	 * @return a LanguageTranslation
	 */
	@Contract("_ -> new")
	public static LanguageTranslation simpleBlock(final Block block) {
		return block(block, langify(BuiltInRegistries.BLOCK.getKey(block).getPath()));
	}

	/**
	 * {@return a LanguageTranslation}
	 *
	 * @param block The block
	 * @param name The block name
	 */
	@Contract("_, _ -> new")
	@SuppressWarnings("unused")
	public static LanguageTranslation block(final Supplier<? extends Block> block, final String name) {
		return block(block.get(), name);
	}

	/**
	 * Uses {@link #langify(String)} to create a name from the registry name
	 *
	 * @param block The block
	 *
	 * @return a LanguageTranslation
	 */
	@Contract("_ -> new")
	@SuppressWarnings("unused")
	public static LanguageTranslation simpleBlock(final Supplier<? extends Block> block) {
		return simpleBlock(block.get());
	}

	/**
	 * {@return a LanguageTranslation}
	 *
	 * @param item The item
	 * @param name The item name
	 */
	@Contract("_, _ -> new")
	public static LanguageTranslation item(final Item item, final String name) {
		return of(item.getDescriptionId(), name);
	}

	/**
	 * Uses {@link #langify(String)} to create a name from the registry name
	 *
	 * @param item The item
	 *
	 * @return a LanguageTranslation
	 */
	@Contract("_ -> new")
	public static LanguageTranslation simpleItem(final Item item) {
		return item(item, langify(BuiltInRegistries.ITEM.getKey(item).getPath()));
	}

	/**
	 * {@return a LanguageTranslation}
	 *
	 * @param item The item
	 * @param name The item name
	 */
	@Contract("_, _ -> new")
	public static LanguageTranslation item(final ItemLike item, final String name) {
		return item(item.asItem(), name);
	}

	/**
	 * Uses {@link #langify(String)} to create a name from the registry name
	 *
	 * @param item The item
	 *
	 * @return a LanguageTranslation
	 */
	@Contract("_ -> new")
	@SuppressWarnings("unused")
	public static LanguageTranslation simpleItem(final ItemLike item) {
		return simpleItem(item.asItem());
	}

	/**
	 * Some items like {@link PotionItem} have different names depending on component data
	 *
	 * @param stack The Item Stack
	 * @param name The stack name
	 *
	 * @return a LanguageTranslation
	 */
	@Contract("_, _ -> new")
	@SuppressWarnings("unused")
	public static LanguageTranslation stack(final ItemStack stack, final String name) {
		return of(stack.getDescriptionId(), name);
	}

	/**
	 * {@return a LanguageTranslation}
	 *
	 * @param effect The effect
	 * @param name The effect name
	 */
	@Contract("_, _ -> new")
	public static LanguageTranslation effect(final MobEffect effect, final String name) {
		return of(effect.getDescriptionId(), name);
	}

	/**
	 * Uses {@link #langify(String)} to create a name from the registry name
	 *
	 * @param effect The effect
	 *
	 * @return a LanguageTranslation
	 */
	@SuppressWarnings("DataFlowIssue")
	@Contract("_ -> new")
	public static LanguageTranslation simpleEffect(final MobEffect effect) {
		return effect(effect, langify(BuiltInRegistries.MOB_EFFECT.getKey(effect).getPath()));
	}

	/**
	 * {@return a LanguageTranslation}
	 *
	 * @param effect The effect
	 * @param name The effect name
	 */
	@Contract("_, _ -> new")
	@SuppressWarnings("unused")
	public static LanguageTranslation effect(final Supplier<? extends MobEffect> effect, final String name) {
		return effect(effect.get(), name);
	}

	/**
	 * Uses {@link #langify(String)} to create a name from the registry name
	 *
	 * @param effect The effect
	 *
	 * @return a LanguageTranslation
	 */
	@Contract("_ -> new")
	@SuppressWarnings("unused")
	public static LanguageTranslation simpleEffect(final Supplier<? extends MobEffect> effect) {
		return simpleEffect(effect.get());
	}

	/**
	 * {@return a LanguageTranslation}
	 *
	 * @param entityType The entity type
	 * @param name The entity name
	 */
	@Contract("_, _ -> new")
	public static LanguageTranslation entity(final EntityType<?> entityType, final String name) {
		return of(entityType.getDescriptionId(), name);
	}

	/**
	 * Uses {@link #langify(String)} to create a name from the registry name
	 *
	 * @param entityType The entity type
	 *
	 * @return a LanguageTranslation
	 */
	@Contract("_ -> new")
	public static LanguageTranslation simpleEntity(final EntityType<?> entityType) {
		return entity(entityType, langify(BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath()));
	}

	/**
	 * {@return a LanguageTranslation}
	 *
	 * @param entityType The entity type
	 * @param name The entity name
	 */
	@Contract("_, _ -> new")
	@SuppressWarnings("unused")
	public static LanguageTranslation entity(final Supplier<? extends EntityType<?>> entityType, final String name) {
		return entity(entityType.get(), name);
	}

	/**
	 * Uses {@link #langify(String)} to create a name from the registry name
	 *
	 * @param entityType The entity type
	 *
	 * @return a LanguageTranslation
	 */
	@Contract("_ -> new")
	@SuppressWarnings("unused")
	public static LanguageTranslation simpleEntity(final Supplier<? extends EntityType<?>> entityType) {
		return simpleEntity(entityType.get());
	}

	/**
	 * {@return a LanguageTranslation}
	 *
	 * @param tag The tag
	 * @param name The tag name, used in recipe viewers
	 */
	@Contract("_, _ -> new")
	public static LanguageTranslation tag(final TagKey<?> tag, final String name) {
		return of(Tags.getTagTranslationKey(tag), name);
	}

	/**
	 * Uses {@link #langify(String)} to create a name from the registry name
	 *
	 * @param tag The tag
	 *
	 * @return a LanguageTranslation
	 */
	@Contract("_ -> new")
	@SuppressWarnings("unused")
	public static LanguageTranslation simpleTag(final TagKey<?> tag) {
		return tag(tag, langify(tag.location().getPath()));
	}

	/**
	 * {@return a LanguageTranslation}
	 *
	 * @param dimension Dimension key
	 * @param name The dimension name
	 */
	@Contract("_, _ -> new")
	@SuppressWarnings("unused")
	public static LanguageTranslation dimension(final ResourceKey<Level> dimension, final String name) {
		return of(dimension.location().toLanguageKey(ILevelExtension.TRANSLATION_PREFIX), name);
	}

	/**
	 * Uses {@link #langify(String)} to create a name from the registry name
	 *
	 * @param dimension Dimension key
	 *
	 * @return a LanguageTranslation
	 */
	@Contract("_ -> new")
	@SuppressWarnings("unused")
	public static LanguageTranslation simpleDimension(final ResourceKey<Level> dimension) {
		return of(dimension.location().toLanguageKey(ILevelExtension.TRANSLATION_PREFIX), langify(dimension.location().getPath()));
	}

	/**
	 * {@return a LanguageTranslation}
	 *
	 * @param jukeboxSong The jukebox song key
	 * @param name The jukebox song name
	 */
	@Contract("_, _ -> new")
	@SuppressWarnings("unused")
	public static LanguageTranslation jukeboxSong(final ResourceKey<JukeboxSong> jukeboxSong, final String name) {
		return of(Util.makeDescriptionId("jukebox_song", jukeboxSong.location()), name);
	}

	/**
	 * Uses {@link #langify(String)} to create a name from the registry name
	 *
	 * @param jukeboxSong The jukebox song key
	 *
	 * @return a LanguageTranslation
	 */
	@Contract("_ -> new")
	@SuppressWarnings("unused")
	public static LanguageTranslation simpleJukeboxSong(final ResourceKey<JukeboxSong> jukeboxSong) {
		final var registryName = jukeboxSong.location();
		return of(Util.makeDescriptionId("jukebox_song", registryName), langify(registryName.getPath()));
	}

	/**
	 * {@return a LanguageTranslation}
	 *
	 * @param advancementId The advancement id
	 * @param title The advancement title
	 */
	@Contract("_, _ -> new")
	public static LanguageTranslation advancementTitle(final ResourceLocation advancementId, final String title) {
		return of(advancementId.toLanguageKey("advancements", "title"), title);
	}

	/**
	 * {@return a LanguageTranslation}
	 *
	 * @param advancementId The advancement id
	 * @param description The advancement description
	 */
	@Contract("_, _ -> new")
	public static LanguageTranslation advancementDescription(final ResourceLocation advancementId, final String description) {
		return of(advancementId.toLanguageKey("advancements", "description"), description);
	}

	/**
	 * {@return A translatable component for the key}
	 */
	public Component component() {return Component.translatable(key);}
}