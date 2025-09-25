package mod.traister101.datagenutils.data.util;

import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.extensions.ILevelExtension;
import org.apache.commons.lang3.StringUtils;

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
	 * Takes a string like 'dark_oak' and converts it to 'Dark Oak'.
	 *
	 * @param serializedName A serialized name ({@link ResourceLocation#getPath()}) like `dark_oak`
	 *
	 * @throws IllegalArgumentException when {@code serializedName} contains a path seperator. This is an error, fix your code
	 */
	public static String langify(final String serializedName) throws IllegalArgumentException {
		if (serializedName.contains("/")) throw new IllegalArgumentException("Only 'flat' serialized names permitted (no path separators '/'). ");
		return Arrays.stream(serializedName.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
	}

	/**
	 * @param key The key
	 * @param translation The translation
	 */
	@Contract("_, _ -> new")
	public static LanguageTranslation of(final String key, final String translation) {
		return new LanguageTranslation(key, translation);
	}

	/**
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
	 */
	@Contract("_ -> new")
	public static LanguageTranslation simpleBlock(final Block block) {
		return block(block, langify(BuiltInRegistries.BLOCK.getKey(block).getPath()));
	}

	/**
	 * @param block The block
	 * @param name The block name
	 */
	@Contract("_, _ -> new")
	public static LanguageTranslation block(final Supplier<Block> block, final String name) {
		return block(block.get(), name);
	}

	/**
	 * Uses {@link #langify(String)} to create a name from the registry name
	 *
	 * @param block The block
	 */
	@Contract("_ -> new")
	public static LanguageTranslation simpleBlock(final Supplier<Block> block) {
		return simpleBlock(block.get());
	}

	/**
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
	 */
	@Contract("_ -> new")
	public static LanguageTranslation simpleItem(final Item item) {
		return item(item, langify(BuiltInRegistries.ITEM.getKey(item).getPath()));
	}

	/**
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
	 */
	@Contract("_ -> new")
	public static LanguageTranslation simpleItem(final ItemLike item) {
		return simpleItem(item.asItem());
	}

	/**
	 * Some items like {@link PotionItem} have different names depending on component data
	 *
	 * @param stack The Item Stack
	 * @param name The stack name
	 */
	@Contract("_, _ -> new")
	public static LanguageTranslation stack(final ItemStack stack, final String name) {
		return of(stack.getDescriptionId(), name);
	}

	/**
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
	 */
	@SuppressWarnings("DataFlowIssue")
	@Contract("_ -> new")
	public static LanguageTranslation simpleEffect(final MobEffect effect) {
		return effect(effect, langify(BuiltInRegistries.MOB_EFFECT.getKey(effect).getPath()));
	}

	/**
	 * @param effect The effect
	 * @param name The effect name
	 */
	@Contract("_, _ -> new")
	public static LanguageTranslation effect(final Supplier<MobEffect> effect, final String name) {
		return effect(effect.get(), name);
	}

	/**
	 * Uses {@link #langify(String)} to create a name from the registry name
	 *
	 * @param effect The effect
	 */
	@Contract("_ -> new")
	public static LanguageTranslation simpleEffect(final Supplier<MobEffect> effect) {
		return simpleEffect(effect.get());
	}

	/**
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
	 */
	@Contract("_ -> new")
	public static LanguageTranslation simpleEntity(final EntityType<?> entityType) {
		return entity(entityType, langify(BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath()));
	}

	/**
	 * @param entityType The entity type
	 * @param name The entity name
	 */
	@Contract("_, _ -> new")
	public static LanguageTranslation entity(final Supplier<EntityType<?>> entityType, final String name) {
		return entity(entityType.get(), name);
	}

	/**
	 * Uses {@link #langify(String)} to create a name from the registry name
	 *
	 * @param entityType The entity type
	 */
	@Contract("_ -> new")
	public static LanguageTranslation simpleEntity(final Supplier<EntityType<?>> entityType) {
		return simpleEntity(entityType.get());
	}

	/**
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
	 */
	@Contract("_ -> new")
	public static LanguageTranslation simpleTag(final TagKey<?> tag) {
		return tag(tag, langify(tag.location().getPath()));
	}

	/**
	 * @param dimension Dimension key
	 * @param name The dimension name
	 */
	@Contract("_, _ -> new")
	public static LanguageTranslation dimension(final ResourceKey<Level> dimension, final String name) {
		return of(dimension.location().toLanguageKey(ILevelExtension.TRANSLATION_PREFIX), name);
	}

	/**
	 * Uses {@link #langify(String)} to create a name from the registry name
	 *
	 * @param dimension Dimension key
	 */
	@Contract("_ -> new")
	public static LanguageTranslation simpleDimension(final ResourceKey<Level> dimension) {
		return of(dimension.location().toLanguageKey(ILevelExtension.TRANSLATION_PREFIX), langify(dimension.location().getPath()));
	}

	/**
	 * @param advancementId The advancement id
	 * @param title The advancement title
	 */
	@Contract("_, _ -> new")
	public static LanguageTranslation advancementTitle(final ResourceLocation advancementId, final String title) {
		return of(advancementId.toLanguageKey("advancements", "title"), title);
	}

	/**
	 * @param advancementId The advancement id
	 * @param description The advancement description
	 */
	@Contract("_, _ -> new")
	public static LanguageTranslation advancementDescription(final ResourceLocation advancementId, final String description) {
		return of(advancementId.toLanguageKey("advancements", "description"), description);
	}

	/**
	 * @return A translatable component for the key
	 */
	public Component component() {return Component.translatable(key);}
}