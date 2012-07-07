package jahspotify.services;

import jahspotify.JahSpotify;
import jahspotify.Search;
import jahspotify.SearchListener;
import jahspotify.SearchResult;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Johan Lindquist
 */
public class SearchEngine
{
    private JahSpotifyService _jahSpotifyService = JahSpotifyService.getInstance();

    private JahSpotify _jahSpotify;

    private static SearchEngine instance;
    public static synchronized SearchEngine getInstance() {
    	if (instance == null) {
    		instance = new SearchEngine();
    		instance.initialize();
    	}
    	return instance;
    }

    private void initialize()
    {
        _jahSpotify = _jahSpotifyService.getJahSpotify();
    }

    public SearchResult search(Search search)
    {
        try
        {
            final BlockingQueue<SearchResult> resultQueue = new ArrayBlockingQueue<SearchResult>(1);
            _jahSpotify.initiateSearch(search, new SearchListener()
            {
                @Override
                public void searchComplete(final SearchResult searchResult)
                {
                    try
                    {
                        resultQueue.put(searchResult);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            return resultQueue.poll(10, TimeUnit.SECONDS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

    }

}
