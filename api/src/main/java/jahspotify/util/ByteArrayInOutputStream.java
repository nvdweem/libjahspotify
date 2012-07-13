package jahspotify.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Input stream to which data can be added while streaming.
 * @author Niels
 */
public class ByteArrayInOutputStream extends InputStream {
	private Queue<Byte> bytes = new ConcurrentLinkedQueue<Byte>();
	int size = 0;

	/**
	 * Returns the size of the stream.
	 * @return
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns how many bytes are available. Because the data is cleaned when it is read, this method returns
	 * the same value as size().
	 */
	@Override
	public int available() throws IOException {
		return size();
    }


	/**
	 * Writes the complete buffer to the output.
	 * @param buff
	 */
	public void write(byte[] buff) {
		write(buff, 0, buff.length);
	}
	/**
	 * Write part of the buffer to the stream.
	 * @param buff
	 * @param offset
	 * @param length
	 */
	public void write(byte[] buff, int offset, int length) {
		for (int i = offset; i < length; i++) {
			bytes.add(buff[i]);
			++size;
		}
	}
	/**
	 * Translate and write the full buffer.
	 * @param buff
	 */
	public void write(int[] buff) {
		write(buff, 0, buff.length);
	}
	/**
	 * Translate and write the buffer to the stream.
	 * @param buff
	 * @param offset
	 * @param length
	 */
	public void write(int[] buff, int offset, int length) {
		for (int i = offset; i <= length && i < buff.length; i++) {
			bytes.add((byte) buff[i]);
			++size;
		}
	}

	/**
	 * Show that marking is not supported.
	 */
	@Override
	public boolean markSupported() {
        return false;
    }

	/**
	 * Empty method, marking is not supported.
	 */
	@Override
	public void mark(int readLimit) {
	}

	/**
	 * Reset doesn't do anything.
	 */
	@Override
	public void reset() {
	}

	/**
	 * Read a single byte.
	 */
	@Override
	public int read() throws IOException {
		if (bytes.isEmpty()) return -1;
		--size;
		return bytes.poll();
	}

	/**
	 * Try to read len amount of bytes.
	 */
	@Override
	public int read(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        if (size() == 0) return -1;
        int i = off;
        int size = size();
        try {
	        for (; i < len && i < size; i++)
	        	b[i] = (byte)read();
        } catch (Exception e) {

        }

        return i;
    }

	/**
	 * Try to read len amount of bytes, but wait for up to 5 seconds to make sure they are available.
	 * @param b
	 * @param off
	 * @param len
	 * @param waitForFull
	 * @return
	 * @throws IOException
	 */
	public int read(byte[] b, int off, int len, boolean waitForFull) throws IOException {
		int i = 0;
		while (available() < len) {
			try {
				if (i++ > 500) return -1;
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return read(b, off, len);
	}

}
