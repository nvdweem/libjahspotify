package jahspotify;

import jahspotify.media.Link;

/**
 * @author Johan Lindquist
 */
public interface PlaybackListener
{
    public void trackStarted(Link link);
    public void trackEnded(Link link, boolean forcedEnd);
    public Link nextTrackToPreload();

    public void setAudioFormat(int rate, int channels);
    public int addToBuffer(byte[] buffer);
}
