package jahspotify.media;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for loadable classes.
 * @author Niels
 */
public abstract class AbstractLoadable<T extends Loadable> implements Loadable {
	private boolean loaded;
	private List<LoadableListener<T>> listeners = new ArrayList<LoadableListener<T>>();
	
	/**
	 * Sets the loaded state. If the state is set to true the loadablelisteners will be
	 * triggered.
	 */
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
		if (loaded)
			triggerLoadedEvent();
	}
	
	/**
	 * Returns if this object is loaded.
	 */
	public boolean isLoaded() {
		return loaded;
	}
	
	/**
	 * Adds a listener which will receive an event if the object is loaded.
	 * If the object is already loaded, the listener will immediately receive
	 * the event.
	 * 
	 * @param listener
	 */
	@SuppressWarnings("unchecked")
	public void addLoadableListener(LoadableListener<T> listener) {
		listeners.add(listener);
		if (isLoaded())
			listener.loaded((T) this);
	}
	
	/**
	 * Sends the loaded event to all listeners.
	 */
	@SuppressWarnings("unchecked")
	private void triggerLoadedEvent() {
		for (LoadableListener<T> listener : listeners)
			listener.loaded((T) this);
	}
}
