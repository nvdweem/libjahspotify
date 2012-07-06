package jahspotify.media;

import jahspotify.impl.JahSpotifyImpl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class PlaylistContainer {

	private Map<Link, String> playlists = new TreeMap<Link, String>();

	public void addPlaylist(Link link, String name) {
		synchronized (playlists) {
			if (playlists.containsKey(link)) return;
			playlists.put(link, name);
		}
	}

	public Playlist getPlaylist(int index) {
		synchronized (playlists) {
			int i = 0;
			for (Entry<Link, String> e : playlists.entrySet())
				if (i++ == index)
					return getPlaylist(e.getKey());
		}
		return null;
	}

	public Playlist getPlaylist(Link playlist) {
		return JahSpotifyImpl.getInstance().readPlaylist(playlist, 0, 0);
	}

	public Map<Link, String> getPlaylists() {
		return new TreeMap<Link, String>(playlists);
	}
}
