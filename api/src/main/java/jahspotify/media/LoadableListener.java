package jahspotify.media;

/**
 * Interface for callbacks of loaded items.
 * @author Niels
 */
public interface LoadableListener<T extends Loadable> {
	public void loaded(T media);
}
