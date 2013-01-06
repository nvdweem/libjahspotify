package jahspotify.impl;

/**
 * @author Johan Lindquist
 */
public interface NativeConnectionListener
{
	public void initialized(boolean initialized);
    public void connected();
    public void disconnected();
    public void loggedIn(boolean success);
    public void playlistsLoaded();
    public void loggedOut();
    public void blobUpdated(String blob);
}
