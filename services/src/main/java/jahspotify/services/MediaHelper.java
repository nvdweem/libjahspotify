package jahspotify.services;

import jahspotify.media.Loadable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Helper class to wait for the loading of loadable types.
 * @author Niels
 */
public class MediaHelper {
	/**
	 * Block until the loadable has been loaded or the timeout has passed.
	 * @param m The loadable to wait for.
	 * @param maxSeconds The maximum number of seconds to wait.
	 * @return true if the loadable has been loaded.
	 */
	public static boolean waitFor(Loadable m, int maxSeconds) {
		for (int i = 0; i < maxSeconds * 100; i++) {
			if (m.isLoaded())
				return true;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Block until all the loadables are loaded or the timeout has passed.
	 * @param _ms The loadables to wait for.
	 * @param maxSeconds The maximum number of seconds to wait.
	 * @return true if the all loadables have been loaded.
	 */
	public static boolean waitFor(Collection<? extends Loadable> _ms, int maxSeconds) {
		List<Loadable> ms = new ArrayList<Loadable>(_ms);

		for (int i = 0; i < maxSeconds * 100; i++) {
			for (int j = ms.size() - 1; j >= 0; j--)
				if (ms.get(j).isLoaded())
					ms.remove(j);
			if (ms.size() == 0)
				return true;

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return false;
	}
}
