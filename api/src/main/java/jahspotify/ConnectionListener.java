package jahspotify;

/**
 * @author Johan Lindquist
 */
public interface ConnectionListener
{
	public void initialized(boolean initialized);
    public void connected();
    public void disconnected();
    public void loggedIn();
    public void loggedOut();
    public void blobUpdated(String blob);
}
