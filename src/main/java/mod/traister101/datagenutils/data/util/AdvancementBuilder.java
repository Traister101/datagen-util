package mod.traister101.datagenutils.data.util;

import com.google.errorprone.annotations.*;

import net.minecraft.advancements.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

@CanIgnoreReturnValue
@RequiredArgsConstructor(staticName = "of")
public final class AdvancementBuilder {

	@Delegate(excludes = CommonAdvancementBuilder.Excludes.class)
	private final CommonAdvancementBuilder<AdvancementBuilder> commonBuilder = new CommonAdvancementBuilder<>(this);
	/**
	 * The advancement title
	 */
	private final String title;
	/**
	 * The advancement description
	 */
	private final String description;
	@Nullable
	private SimpleDisplayInfo display;
	@Nullable
	private ResourceLocation parent;

	public AdvancementBuilder display(final SimpleDisplayInfo.SimpleDisplayInfoBuilder displayInfoBuilder) {
		return display(displayInfoBuilder.build());
	}

	public AdvancementBuilder display(final ItemStack icon, final @Nullable ResourceLocation background, final AdvancementType type,
			final boolean showToast, final boolean announceChat, final boolean hidden) {
		return display(new SimpleDisplayInfo(icon, background, type, showToast, announceChat, hidden));
	}

	public AdvancementBuilder display(final SimpleDisplayInfo displayInfo) {
		this.display = displayInfo;
		return this;
	}

	/**
	 * @param parent The parent
	 */
	public AdvancementBuilder parent(final AdvancementHolder parent) {
		this.parent = parent.id();
		return this;
	}

	/**
	 * @param parentId The parent id
	 */
	public AdvancementBuilder parent(final ResourceLocation parentId) {
		this.parent = parentId;
		return this;
	}

	@CheckReturnValue
	private Advancement build(final ResourceLocation advancementId) {
		final var commonInfo = commonBuilder.build();
		final DisplayInfo displayInfo;
		if (display != null) {
			final var title = LanguageTranslation.advancementTitle(advancementId, this.title);
			final var description = LanguageTranslation.advancementDescription(advancementId, this.description);
			displayInfo = display.toInfo(title, description);
		} else {
			displayInfo = null;
		}
		return new Advancement(Optional.ofNullable(parent), Optional.ofNullable(displayInfo), commonInfo.rewards(), commonInfo.criteria(),
				commonInfo.requirements(), true);
	}

	/**
	 * @param output The advancement output
	 * @param advancementId The advancement id
	 */
	public AdvancementHolder save(final AdvancementOutput output, final ResourceLocation advancementId) {
		output.lang(LanguageTranslation.advancementTitle(advancementId, title));
		output.lang(LanguageTranslation.advancementDescription(advancementId, description));
		return output.accept(advancementId, build(advancementId));
	}

	/**
	 * @param output The advancement output
	 * @param id A resource string for the location, if no namespace is specified it'll default to {@value ResourceLocation#DEFAULT_NAMESPACE}
	 */
	public AdvancementHolder save(final AdvancementOutput output, final String id) {
		return save(output, ResourceLocation.parse(id));
	}
}