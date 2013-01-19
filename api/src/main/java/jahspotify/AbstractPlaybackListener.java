package jahspotify;

import jahspotify.media.Link;

public abstract class AbstractPlaybackListener implements PlaybackListener {
	@Override public void trackStarted(final Link link) {}
	@Override public void trackEnded(final Link link, final boolean forcedEnd) {}
	@Override public Link nextTrackToPreload() {return null;}
	@Override public void playTokenLost() {}
	@Override public void setAudioFormat(final int rate, final int channels) {}
	@Override public int addToBuffer(final byte[] buffer) {return 0;}
}
