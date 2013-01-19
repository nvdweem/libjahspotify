package jahspotify.media;

import jahspotify.impl.JahSpotifyImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlaylistContainer {

	private static List<Playlist> playlists = new ArrayList<Playlist>();
	private static Set<Long> pTrs = new HashSet<Long>();

	public static void addPlaylist(final Playlist playlist) {
		synchronized (playlists) {
			playlists.add(playlist);
		}
	}

	/**
	 * Should only be called by the C library.
	 * The long parameter will be the pointer to the playlist, this
	 * is because the playlist might not be loaded far enough to
	 * check if it is unique by getting the link.
	 *
	 * @param pTr The pointer value of the playlist. Used to check
	 * 			  if the playlist has been added before.
	 * @return An empty playlist object.
	 */
	public static Playlist addPlaylist(final long pTr) {
		if (pTrs.contains(pTr))
			return null;

		pTrs.add(pTr);
		synchronized (playlists) {
			Playlist playlist = new Playlist();
			playlists.add(playlist);
			return playlist;
		}
	}

	public static void removePlaylist(final String playlist) {
		synchronized (playlists) {
			for (int i = playlists.size() - 1; i >= 0; i--) {
				if (playlists.get(i).getId().getId().equals(playlist))
					playlists.remove(i);
			}
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
