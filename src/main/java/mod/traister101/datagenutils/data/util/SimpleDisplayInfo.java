package mod.traister101.datagenutils.data.util;

import net.minecraft.advancements.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import lombok.*;
import lombok.experimental.Tolerate;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

@Value
@Builder
public class SimpleDisplayInfo {

	/**
	 * The icon
	 */
	ItemStack icon;
	/**
	 * The advancement title
	 */
	String title;
	/**
	 * The advancement description
	 */
	String description;
	/**
	 * The background texture
	 */
	@Nullable
	@Getter(AccessLevel.NONE)
	ResourceLocation background;
	/**
	 * The advancement type
	 */
	AdvancementType type;
	/**
	 * If a toast should be shown
	 */
	boolean showToast;
	/**
	 * If obtaining the advancement should send a chat message
	 */
	boolean announceChat;
	/**
	 * If this is a hidden advancement and should be hidden until obtained
	 */
	boolean hidden;

	public DisplayInfo toInfo(final ResourceLocation advancementId) {
		final var title = LanguageTranslation.advancementTitle(advancementId, this.title);
		final var description = LanguageTranslation.advancementDescription(advancementId, this.description);
		return new DisplayInfo(icon, title.component(), description.component(), Optional.ofNullable(background), type, showToast, announceChat,
				hidden);
	}

	void save(final AdvancementOutput output, final ResourceLocation advancementId) {
		output.lang(LanguageTranslation.advancementTitle(advancementId, title));
		output.lang(LanguageTranslation.advancementDescription(advancementId, description));
	}

	public static final class SimpleDisplayInfoBuilder {

		/**
		 * Set the icon
		 *
		 * @param item The item to use as an icon
		 *
		 * @return The builder
		 */
		@Tolerate
		public SimpleDisplayInfoBuilder icon(final ItemLike item) {
			return icon(new ItemStack(item));
		}
	}
}