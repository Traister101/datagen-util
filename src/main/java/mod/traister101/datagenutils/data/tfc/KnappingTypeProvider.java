package mod.traister101.datagenutils.data.tfc;

import net.dries007.tfc.util.data.KnappingType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import lombok.Setter;
import lombok.experimental.*;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * A provider for TFC's {@link KnappingType}
 */
public abstract class KnappingTypeProvider extends DataManagerProvider<KnappingType> {

	/**
	 * The constructor
	 *
	 * @param output The output
	 * @param modid The modid
	 * @param registries The registries
	 */
	protected KnappingTypeProvider(final PackOutput output, final String modid, final CompletableFuture<Provider> registries) {
		super(KnappingType.MANAGER, output, modid, registries);
	}

	/**
	 * {@return new KnappingTypeBuilder}
	 *
	 * @param ingredient The ingredient
	 * @param count The count
	 */
	protected KnappingTypeBuilder builder(final Ingredient ingredient, final int count) {
		return new KnappingTypeBuilder(new SizedIngredient(ingredient, count));
	}

	/**
	 * {@return new KnappingTypeBuilder}
	 *
	 * @param inputIngredient The input ingredient
	 */
	protected KnappingTypeBuilder builder(final SizedIngredient inputIngredient) {return new KnappingTypeBuilder(inputIngredient);}

	/**
	 * A builder for TFC's {@link KnappingType}
	 */
	protected final class KnappingTypeBuilder extends DataBuilder {

		private final SizedIngredient inputItem;
		/**
		 * Defaults to the input items count
		 */
		@Setter
		@Accessors(fluent = true)
		private int amountToConsume;
		/**
		 * The sound event to play on click
		 */
		@Setter
		@Accessors(fluent = true)
		private Holder<SoundEvent> clickSound;
		/**
		 * If {@link #amountToConsume(int)} should be consumed when complete or after first interaction
		 */
		@Setter
		@Accessors(fluent = true)
		private boolean consumeAfterComplete;
		/**
		 * If a disabled texture should be used. These are found at the same location as the usual texture with `_disabled` appended to the path
		 */
		@Setter
		@Accessors(fluent = true)
		private boolean useDisabledTexture;
		@Setter
		@Accessors(fluent = true)
		private boolean spawnsParticles;
		/**
		 * An {@link ItemStack} to use as the icon in recipe viewers
		 */
		@Setter
		@Accessors(fluent = true)
		private ItemStack jeiIcon;

		private KnappingTypeBuilder(final SizedIngredient inputItem) {
			this.inputItem = inputItem;
			this.amountToConsume = inputItem.count();
		}

		/**
		 * An {@link ItemLike} to use as the icon in recipe viewers
		 *
		 * @param item The item to use as an icon
		 *
		 * @return {@code this}
		 */
		@Tolerate
		public KnappingTypeBuilder jeiIcon(final ItemLike item) {
			return jeiIcon(new ItemStack(item));
		}

		protected KnappingType build() {
			return new KnappingType(inputItem, amountToConsume, Objects.requireNonNull(clickSound, "Must have a sound"), consumeAfterComplete,
					useDisabledTexture, spawnsParticles, Objects.requireNonNull(jeiIcon, "Must have a jei icon"));
		}
	}
}