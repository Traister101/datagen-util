package mod.traister101.datagenutils.data.util;

import net.minecraft.advancements.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import lombok.*;
import lombok.experimental.Tolerate;
import org.jetbrains.annotations.*;
import java.util.Optional;

/**
 * A {@link DisplayInfo} "wrapper"
 */
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

	/**
	 * The constructor
	 *
	 * @param icon The icon
	 * @param title The title
	 * @param description The description
	 * @param background The background texture
	 * @param type The type
	 * @param showToast If a toast should be shown
	 * @param announceChat If getting the advancement should get a message in chat
	 * @param hidden If the advancement should be hidden
	 */
	@Contract(pure = true)
	public SimpleDisplayInfo(final ItemStack icon, final String title, final String description, @Nullable final ResourceLocation background,
			final AdvancementType type, final boolean showToast, final boolean announceChat, final boolean hidden) {
		this.icon = icon;
		this.title = title;
		this.description = description;
		this.background = background;
		this.type = type;
		this.showToast = showToast;
		this.announceChat = announceChat;
		this.hidden = hidden;
	}

	/**
	 * Convert to the vanilla {@link DisplayInfo}
	 *
	 * @param advancementId The advancement id
	 *
	 * @return The vanilla {@link DisplayInfo} representation
	 */
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

	@SuppressWarnings("doclint")
	public static final class SimpleDisplayInfoBuilder {

		/**
		 * Set the icon
		 *
		 * @param item The item to use as an icon
		 *
		 * @return The builder
		 */
		@Tolerate
		@SuppressWarnings("unused")
		public SimpleDisplayInfoBuilder icon(final ItemLike item) {
			return icon(new ItemStack(item));
		}
	}
}