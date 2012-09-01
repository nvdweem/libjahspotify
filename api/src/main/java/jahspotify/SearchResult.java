package jahspotify;

import jahspotify.media.AbstractLoadable;
import jahspotify.media.Link;
import jahspotify.media.Playlist;

import java.util.List;

/**
 * @author Johan Lindquist
 */
public class SearchResult extends AbstractLoadable<SearchResult>
{
   private String query;
   private String didYouMean;
   private List<Link> tracksFound;
   private int totalNumTracks;
   private int trackOffset;
   private List<Link> albumsFound;
   private int totalNumAlbums;
   private int albumOffset;
   private List<Link> artistsFound;
   private int totalNumArtists;
   private int artistOffset;
   private List<Playlist> playlistsFound;
   private int totalNumPlaylists;
   private int playlistOffset;

    public String getDidYouMean()
    {
        return didYouMean;
    }

    public int getAlbumOffset()
    {
        return albumOffset;
    }

    public List<Link> getAlbumsFound()
    {
        return albumsFound;
    }

    public int getArtistOffset()
    {
        return artistOffset;
    }

    public List<Link> getArtistsFound()
    {
        return artistsFound;
    }

    public String getQuery()
    {
        return query;
    }

    public int getTotalNumAlbums()
    {
        return totalNumAlbums;
    }

    public int getTotalNumArtists()
    {
        return totalNumArtists;
    }

    public int getTotalNumTracks()
    {
        return totalNumTracks;
    }

    public int getTrackOffset()
    {
        return trackOffset;
    }

    public List<Link> getTracksFound()
    {
        return tracksFound;
    }

    public List<Playlist> getPlaylistsFound() {
		return playlistsFound;
	}

	public int getTotalNumPlaylists() {
		return totalNumPlaylists;
	}

	public int getPlaylistOffset() {
		return playlistOffset;
	}

	@Override
    public String toString()
    {
        return "SearchResult{" +
                "albumOffset=" + albumOffset +
                ", query='" + query + '\'' +
                ", didYouMean='" + didYouMean + '\'' +
                ", tracksFound=" + tracksFound +
                ", totalNumTracks=" + totalNumTracks +
                ", trackOffset=" + trackOffset +
                ", albumsFound=" + albumsFound +
                ", totalNumAlbums=" + totalNumAlbums +
                ", artistsFound=" + artistsFound +
                ", totalNumArtists=" + totalNumArtists +
                ", artistOffset=" + artistOffset +
                '}';
    }

}
