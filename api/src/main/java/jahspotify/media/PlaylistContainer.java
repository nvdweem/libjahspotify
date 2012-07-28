package jahspotify.media;

import jahspotify.impl.JahSpotifyImpl;

import java.util.ArrayList;
import java.util.List;

public class PlaylistContainer {

	private static List<Playlist> playlists = new ArrayList<Playlist>();

	public static void addPlaylist(Playlist playlist) {
		synchronized (playlists) {
			playlists.add(playlist);
		}
	}

	public static void clear() {
		playlists.clear();
	}

	public static Playlist getPlaylist(int index) {
		if (index >= 0 && index < playlists.size())
			return playlists.get(index);
		return null;
	}

	public static Playlist getPlaylist(Link playlist) {
		return JahSpotifyImpl.getInstance().readPlaylist(playlist, 0, 0);
	}

	public static List<Playlist> getPlaylists() {
		return new ArrayList<Playlist>(playlists);
	}
}
