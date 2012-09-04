package jahspotify.services;

import jahspotify.JahSpotify;
import jahspotify.PlaybackListener;
import jahspotify.impl.JahSpotifyImpl;
import jahspotify.media.Link;
import jahspotify.media.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

/**
 * Class which plays the music from libspotify.
 * @author Niels
 */
public class MediaPlayer implements PlaybackListener {
	private transient final JahSpotify spotify = JahSpotifyImpl.getInstance();
	private static final int MAX_HISTORY = 50;

	private List<MediaStreamer> streamers = new ArrayList<MediaStreamer>();
	private List<Queue<Link>> queues = new ArrayList<Queue<Link>>();
	private List<Track> history = new ArrayList<Track>();
	private int rate = 0, channels = 0;
	private int positionOffset = 0;
	private SourceDataLine audio;
	private Track currentTrack;
	private boolean playing = false;
	private int volume = 100;

	private static MediaPlayer instance;
	public static synchronized MediaPlayer getInstance() {
		if (instance == null)
			instance = new MediaPlayer();
		return instance;
	}

	/**
	 * Reset counters.
	 */
	public void changeSong() {
		if (audio != null && audio.isOpen())
			audio.close();
		audio = null;
		positionOffset = 0;
	}

	/**
	 * Queue a track.
	 *
	 * @param track
	 */
	public void play() {
		start();
	}

	/**
	 * Start playing if nothing is playing.
	 */
	private void start() {
		if (currentTrack == null)
			next();
	}

	/**
	 * Go to the next track.
	 */
	public void endOfTrack() {
		currentTrack = null;
		next();
	}

	/**
	 * Try to play the next track.
	 *
	 * @return
	 */
	private boolean next() {
		Track track = getNextTrack(true);
		if (track == null) return false;

		currentTrack = track;
		history.add(0, currentTrack);
		trimHistory();
		playNow(track);
		return true;
	}

	/**
	 * Immediately start playing a song.
	 *
	 * @param track
	 * @return
	 */
	public void playNow(Track track) {
		changeSong();
		spotify.play(track.getId());
		currentTrack = track;
	}

	/**
	 * Pause the player if the play token was lost.
	 */
	@Override
	public void playTokenLost() {
		if (isPlaying()) pause();
	}

	/**
	 * Toggle playing state.
	 */
	public void pause() {
		if (!playing && currentTrack == null) {
			next();
			return;
		}
		if (playing) {
			spotify.pause();
			audio.stop();
		} else {
			spotify.resume();
			audio.start();
		}
		playing = !playing;
	}
	
	public void pause(boolean play) {
		if (play == playing) return;
		pause();
	}

	public void skip() {
		endOfTrack();
	}

	public void prev() {
		// Prev replays the current song if it is pressed within the first 5
		// seconds.
		if (getPosition() > 5) {
			seek(0);
			return;
		}

		if (!history.isEmpty())
			playNow(history.remove(0));
	}

	public void seek(int position) {
		if (currentTrack != null) {
			audio.flush();
			spotify.seek(position);
			seekCallback(position);
		}
	}

	public void seekCallback(int position) {
		positionOffset = position - ((int) audio.getMicrosecondPosition() / 1000);
	}

	/**
	 * Called from libspotify.
	 * @param buffer
	 * @return
	 */
	@Override
	public int addToBuffer(byte[] buffer) {
		if (audio == null || buffer == null)
			return 0;
		int available = audio.available();
		if (available == 0)
			return 0;
		int bufferSize = buffer.length;
		int toWrite = Math.min(available, bufferSize);
		int written = audio.write(buffer, 0, toWrite);

		try {
			for (MediaStreamer streamer : new ArrayList<MediaStreamer>(streamers)) {
				try {
					streamer.addToBuffer(buffer, written);
				} catch (Throwable t) {
					// Remove failing streamer.
					streamers.remove(streamer);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return written / 4;
	}

	/**
	 * Called from libspotify.
	 * @param rate
	 * @param channels
	 */
	@Override
	public void setAudioFormat(int rate, int channels) {
		if (audio != null && rate == this.rate && channels == this.channels)
			return;
		this.rate = rate;
		this.channels = channels;

		try {
			AudioFormat format = new AudioFormat(rate, 8 * channels, channels,
					true, false);
			format = new AudioFormat(format.getEncoding(),
					format.getSampleRate(), format.getSampleSizeInBits(),
					format.getChannels(), format.getFrameSize(),
					format.getFrameRate(), false);

			synchronized(streamers) {
				for (MediaStreamer streamer : streamers) {
					try {
						streamer.setAudioFormat(format);
					} catch (Throwable t) {
						// Remove failing streamer.
						streamers.remove(streamer);
					}
				}
			}

			audio = AudioSystem.getSourceDataLine(format);
			audio.open(format, rate * 4);
			setVolumeToAudio(volume);
			audio.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isPlaying() {
		return playing;
	}

	public Track getCurrentTrack() {
		return currentTrack;
	}

	public int getDuration() {
		if (currentTrack == null)
			return 0;
		return currentTrack.getLength();
	}

	public int getPosition() {
		if (audio == null)
			return 0;
		return positionOffset + (int) audio.getMicrosecondPosition() / 1000;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
		setVolumeToAudio(volume);
	}

	private void setVolumeToAudio(int volume) {
		if (audio == null) return;
		if (audio.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
			FloatControl volumeControl = (FloatControl) audio.getControl(FloatControl.Type.MASTER_GAIN);
			
			// Copied from: http://www.javadocexamples.com/java_source/com/limegroup/gnutella/gui/mp3/BasicPlayer.java.html#line.615
			double minGainDB = volumeControl.getMinimum();
			double ampGainDB = Math.min(.5*volumeControl.getMaximum(), 0) - volumeControl.getMinimum();
			double cste = Math.log(10.0)/20;
			double valueDB = minGainDB + (1/cste)*Math.log(1+(Math.exp(cste*ampGainDB)-1)* (volume / 100f));
			volumeControl.setValue((float)valueDB);
		}
	}

	/**
	 * Trims the history to always keep at most 50 tracks.
	 */
	private void trimHistory() {
		while (history.size() > MAX_HISTORY)
			history.remove(MAX_HISTORY);
	}

	@Override
	public void trackStarted(Link link) {
		playing = true;
	}

	@Override
	public void trackEnded(Link link, boolean forcedEnd) {
		if (!forcedEnd) {
			if (audio != null && audio.isOpen()) {
				audio.drain();
			}
		}
			
		if (!next()) {
			pause();
			currentTrack = null;
			audio = null;
		}
	}

	@Override
	public Link nextTrackToPreload() {
		Track track = getNextTrack(false);
		if (track == null) return null;
		return track.getId();
	}

	/**
	 * Get the next track
	 * @return
	 */
	public Track getNextTrack(boolean popQueue) {
		for (Queue<Link> q : queues) {
			Link next;
			if (popQueue)
				next = q.poll();
			else
				next = q.peek();

			if (next != null)
				return JahSpotifyImpl.getInstance().readTrack(next);
		}
		return null;
	}

	public void addQueue(Queue<Link> queue) {
		queues.add(queue);
	}
	public void removeQueue(Queue<Link> queue) {
		queues.remove(queue);
	}

	public void addStreamer(MediaStreamer streamer) {
		synchronized(streamers) {
			streamers.add(streamer);
			if (audio != null)
				streamer.setAudioFormat(audio.getFormat());
		}
	}
	public void removeStreamer(MediaStreamer streamer) {
		synchronized(streamers) {
			streamers.remove(streamer);
		}
	}

	public List<Track> getHistory() {
		return history;
	}
	
}