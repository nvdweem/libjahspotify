package jahspotify.impl;

/**
 * @author Johan Lindquist
 */
public interface NativeConnectionListener
{
    public void connected();
    public void disconnected();
    public void loggedIn(boolean success);
    public void loggedOut();
    public void blobUpdated(String blob);
}
