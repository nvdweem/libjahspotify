package jahspotify.services;

import jahspotify.JahSpotify;
import jahspotify.PlaybackListener;
import jahspotify.impl.JahSpotifyImpl;
import jahspotify.media.Link;
import jahspotify.media.Track;

import java.util.ArrayList;
import java.util.List;

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
		history.add(0, currentTrack);
		trimHistory();
		currentTrack = null;
		next();
	}

	/**
	 * Try to play the next track.
	 *
	 * @return
	 */
	private boolean next() {
		QueueTrack qTrack = QueueManager.getInstance().getNextQueueTrack();
		if (qTrack == null) return false;
		Track track = spotify.readTrack(qTrack.getTrackUri());

		currentTrack = track;
		playNow(track);
		return true;
	}

	/**
	 * Immediately start playing a song.
	 *
	 * @param track
	 * @return
	 */
	private void playNow(Track track) {
		spotify.play(track.getId());
		currentTrack = track;
		changeSong();
		playing = true;
	}

	/**
	 * Toggle playing state.
	 */
	public void pause() {
		if (!playing && currentTrack == null) {
			next();
			return;
		}
		if (playing)
			spotify.pause();
		else
			spotify.resume();
		playing = !playing;
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
			spotify.seek(position * 1000);
			seekCallback(position * 1000);
		}
	}

	public void seekCallback(int position) {
		audio = null;
		positionOffset = position / 1000;
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
		return audio.write(buffer, 0, toWrite) / 4;
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

			audio = AudioSystem.getSourceDataLine(format);
			setVolumeToAudio(volume);
			audio.open(format);
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
		return currentTrack.getLength() / 1000;
	}

	public int getPosition() {
		if (audio == null)
			return 0;
		return positionOffset + audio.getFramePosition() / this.rate;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
		setVolumeToAudio(volume);
	}

	private void setVolumeToAudio(int volume) {
		if (audio.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
			FloatControl volumeControl = (FloatControl) audio
					.getControl(FloatControl.Type.MASTER_GAIN);
			float range = volumeControl.getMaximum()
					- volumeControl.getMinimum();
			volumeControl.setValue(volumeControl.getMinimum() + range
					* (volume / 100f));
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

	}

	@Override
	public void trackEnded(Link link, boolean forcedEnd) {
		if (!next())
			pause();
	}

	@Override
	public Link nextTrackToPreload() {
		QueueTrack track = QueueManager.getInstance().peekAtNextTrack();
		if (track == null) return null;
		return track.getTrackUri();
	}

}