package jahspotify.impl;

import jahspotify.media.Album;
import jahspotify.media.Artist;
import jahspotify.media.ImageSize;
import jahspotify.media.Link;
import jahspotify.media.Playlist;

/**
 * @author Johan Lindquist
 */
public interface NativeMediaLoadedListener
{
    public void track(final int token, final Link link);
    public void playlist(Playlist playlist);

    public void album(final int token, final Album album);

    public void image(final int token, final Link link, final ImageSize imageSize, final byte[] imageBytes);

    public void artist(final int token, Artist artist);
}
