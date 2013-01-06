package jahspotify.media;

import jahspotify.impl.JahSpotifyImpl;

import java.util.ArrayList;
import java.util.List;

public class PlaylistContainer {

	private static List<Playlist> playlists = new ArrayList<Playlist>();

	public static void addPlaylist(final Playlist playlist) {
		playlists.add(playlist);
	}

	public static void removePlaylist(final String playlist) {
		for (int i = playlists.size() - 1; i >= 0; i--) {
			if (playlists.get(i).getId().getId().equals(playlist))
				playlists.remove(i);
		}
	}

	public static void clear() {
		playlists.clear();
	}

	public static Playlist getPlaylist(final int index) {
		if (index >= 0 && index < playlists.size())
			return playlists.get(index);
		return null;
	}

	public static Playlist getPlaylist(final Link playlist) {
		return JahSpotifyImpl.getInstance().readPlaylist(playlist, 0, 0);
	}

	public static List<Playlist> getPlaylists() {
		return new ArrayList<Playlist>(playlists);
	}
}
