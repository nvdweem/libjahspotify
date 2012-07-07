package jahspotify.services;

import jahspotify.JahSpotify;
import jahspotify.impl.JahSpotifyImpl;

import java.io.File;

import javax.annotation.PreDestroy;

/**
 * @author Johan Lindquist
 */
public class JahSpotifyService
{
	private static String tempFolder;
    private JahSpotify _jahSpotify;

    private static JahSpotifyService instance;
    public static synchronized JahSpotifyService getInstance() {
    	if (tempFolder == null)
    		return null;

    	if (instance == null) {
    		try {
	    		instance = new JahSpotifyService();
	    		instance.initialize();
    		} catch (RuntimeException e) {
    			instance = null;
    			throw e;
    		}
    	}
    	return instance;
    }

    public static void initialize(File tempFolder) {
    	if (tempFolder == null || !tempFolder.isDirectory() || !tempFolder.exists())
    		throw new RuntimeException("The tempfolder should be a directory and it should already exist.");
    	JahSpotifyService.tempFolder = tempFolder.getAbsolutePath();
    }

    public JahSpotify createJahSpotify()
    {
        return _jahSpotify;
    }

    private void initialize()
    {
    	if (tempFolder == null || tempFolder.length() == 0)
    		tempFolder = System.getProperty("jahspotify.spotify.tempfolder");
    	if (tempFolder == null || tempFolder.length() == 0)
    		throw new RuntimeException("A temp folder should be set before initializing Jah'Spotify");

        if (_jahSpotify == null)
        {
            _jahSpotify = JahSpotifyImpl.getInstance();

            if (!_jahSpotify.isStarted())
            {
                _jahSpotify.login(tempFolder, System.getProperty("jahspotify.spotify.username"), System.getProperty("jahspotify.spotify.password"));
            }
            _jahSpotify.addPlaybackListener(MediaPlayer.getInstance());
        }
    }

    @PreDestroy
    public void shutdown()
    {
        if (_jahSpotify != null)
        {
            // Stop playback and then shutdown the instance
            _jahSpotify.stop();
            _jahSpotify.shutdown();
        }
    }

    public JahSpotify getJahSpotify()
    {
        return _jahSpotify;
    }

}
