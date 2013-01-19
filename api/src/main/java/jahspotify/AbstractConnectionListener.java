package jahspotify;

/**
 * Helper class for connection listeners.
 * @author Niels
 */
public abstract class AbstractConnectionListener implements ConnectionListener {

	@Override public void initialized(final boolean initialized) {}
	@Override public void connected() {}
	@Override public void disconnected() {}
	@Override public void loggedIn(final boolean success) {}
	@Override public void playlistsLoaded(final boolean contents) {}
	@Override public void loggedOut() {}
	@Override public void blobUpdated(final String blob) {}
	@Override public void playTokenLost() {}
}
