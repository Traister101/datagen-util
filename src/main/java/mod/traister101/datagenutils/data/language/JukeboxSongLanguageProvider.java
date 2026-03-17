package mod.traister101.datagenutils.data.language;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.JukeboxSong;

public abstract class JukeboxSongLanguageProvider extends DynamicRegistryLanguageSubProvider<JukeboxSong> {

	protected JukeboxSongLanguageProvider(final String modid) {
		super(Registries.JUKEBOX_SONG, songName -> Util.makeDescriptionId("jukebox_song", songName), modid);
	}
}
