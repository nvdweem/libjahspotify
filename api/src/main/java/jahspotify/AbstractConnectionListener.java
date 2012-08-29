package jahspotify;

/**
 * Helper class for connection listeners.
 * @author Niels
 */
public abstract class AbstractConnectionListener implements ConnectionListener {

	@Override public void initialized(boolean initialized) {}
	@Override public void connected() {}
	@Override public void disconnected() {}
	@Override public void loggedIn(boolean success) {}
	@Override public void loggedOut() {}
	@Override public void blobUpdated(String blob) {}

}
