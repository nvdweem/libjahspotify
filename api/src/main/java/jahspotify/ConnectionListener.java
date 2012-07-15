package jahspotify;

/**
 * @author Johan Lindquist
 */
public interface ConnectionListener
{
    public void connected();
    public void disconnected();
    public void loggedIn();
    public void loggedOut();
    public void blobUpdated(String blob);
}
