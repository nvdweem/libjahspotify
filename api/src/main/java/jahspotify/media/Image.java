package jahspotify.media;

import java.util.Arrays;

/**
 * @author Johan Lindquist
 */
public class Image implements Loadable
{
    private Link id;
    private ImageFormat imageFormat = ImageFormat.JPEG;
    private byte[] bytes;
    private boolean loaded = false;

    public Image()
    {
    }

    public Image(final Link uri)
    {
    	this(uri, null);
    }

    public Image(final Link uri, final byte[] bytes)
    {
        this.id = uri;
        this.bytes = bytes;
    }

    public byte[] getBytes()
    {
        return bytes;
    }

    public void setBytes(final byte[] bytes)
    {
        this.bytes = bytes;
    }

    public Link getId()
    {
        return id;
    }

    public void setId(final Link id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "Image{" +
                "bytes=" + Arrays.asList(bytes) +
                ", id=" + id +
                ", imageFormat=" + imageFormat +
                '}';
    }

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
}