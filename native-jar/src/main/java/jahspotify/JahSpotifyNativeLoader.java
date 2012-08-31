package jahspotify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Tries to load the required files contained in the jar.
 * @author Niels
 */
public class JahSpotifyNativeLoader {
	private static final Log log = LogFactory.getLog(JahSpotifyNativeLoader.class);

	/**
	 * When constructed, try to load the files.
	 */
	public JahSpotifyNativeLoader() {
		List<String> toLoad = new ArrayList<String>();
		
		// Windows users need extra files.
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			toLoad.add("libspotify.dll"); // Non-Windows users should have installed this as a library.
			toLoad.add("pthreadGC2.dll"); // Required for Windows only.
		}
		toLoad.add(System.mapLibraryName("jahspotify"));

		boolean allLoaded = true;
		
		// Use the default temp folder, overwrite files if needed.
		File temp = new File(System.getProperty("java.io.tmpdir"));
		for (String lib : toLoad) {
			log.debug("Attempting to load " + lib);

			File target = new File(temp, lib);
			if (target.exists()) {
				if (!target.delete()) {
					log.warn(target + " exists but can't be deleted.");
				}
			}
			
			InputStream in = null;
			FileOutputStream out = null;

			try {
				// Extract the library from the jar.
				in = this.getClass().getClassLoader().getResourceAsStream(lib);
				if (in == null) {
					log.warn("Unable to load stream for " + lib);
					allLoaded = false;
					continue;
				}
				out = new FileOutputStream(target);

				copy(in, out);
			} catch (Exception e) {
				log.warn("Unable to load " + lib);
				e.printStackTrace();
				allLoaded = false;
				continue;
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException e) {}

				try {
					if (out != null) {
						out.close();
					}
				} catch (IOException e) {}
				
				// Try to clean on exit.
				target.deleteOnExit();
			}
			
			// Finally, try to load the extracted file.
			try {
				System.load(target.getAbsolutePath());
			}
			catch (Exception e) {
				log.warn("Unable to load stream for " + lib);
				allLoaded = false;	
			}
		}
		if (!allLoaded)
			throw new RuntimeException("Unable to load all libraries.");
	}

	/**
	 * Copy the contents from one stream to the other.
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	private static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buff = new byte[1024 * 1024];
		int read;

		while ((read = in.read(buff)) != -1)
			out.write(buff, 0, read);
	}

}