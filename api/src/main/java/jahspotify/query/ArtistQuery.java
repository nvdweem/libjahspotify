package jahspotify.query;

/**
 * @author Johan Lindquist
 */
public class ArtistQuery extends TokenQuery
{
    public ArtistQuery(final String artist)
    {
        super("artist:\"" + artist +"\"");
    }

}
