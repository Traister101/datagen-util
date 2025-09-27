package mod.traister101.datagenutils.data.util;

import net.minecraft.advancements.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import lombok.*;
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

	public DisplayInfo toInfo(final LanguageTranslation title, final LanguageTranslation description) {
		return new DisplayInfo(icon, title.component(), description.component(), Optional.ofNullable(background), type, showToast, announceChat,
				hidden);
	}

	public static final class SimpleDisplayInfoBuilder {}
}