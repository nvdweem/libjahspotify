package jahspotify.services;

import javax.sound.sampled.AudioFormat;

/**
 * Interface for classes which will stream the audio.
 * @author Niels
 */
public interface MediaStreamer {
	/**
	 * Adds audio to buffer. The complete buffer must be read, bytes will only be delivered once.
	 * @param buff
	 * @param len
	 */
	public void addToBuffer(byte[] buff, int len);

	/**
	 * Sets the audio format. Can be called more than once.
	 * @param format
	 */
	public void setAudioFormat(AudioFormat format);
}
