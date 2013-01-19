===============================================================================
libJah'Spotify
===============================================================================

## Note

This project originated as a fork from the Jah'Spotify tool of Johan Lindquist. Johans repository contains a complete web application, this project aims to
be more of a library for other projects.

If you are searching for a web based Spotify tool which is also compatible with mobile devices you should check out Johans original work at:

    https://github.com/johanlindquist/jahspotify

## Introduction

libJah'Spotify is a Java wrapper built on top of the Spotify native APIs (libspotify)

Currently supports:

* retrieve a playlist
* retrieve an album
* retrieve a track
* retrieve an image
* add tracks to a queue
* play tracks
* pause/skip functions

## To build

libJah'Spotify supports the Linux, Windows and OSX versions of libspotify (see below for more details on building on Windows).

To build the sources first check them out from git

    git clone git://github.com/nvdweem/libjahspotify.git
    cd libjahspotify

Next, you need to download and install libspotify & request an API key from Spotify.  This can be done
on the http://developer.spotify.com website.

Generate the key and download the C code version of it.  Place this in a file called AppKey.h in the

    native/src/main/native/inc

directory. Finally, execute the Maven build

    mvn clean install

### Building on Windows

#### Before compiling

1. Download MinGW and put the bin folder in your PATH.
2. Create an environment variable 'LIB_SPOTIFY' and point it to where you unpacked libspotify folder.

### Running on Windows

For windows, you will need to download a few more dependencies:

- pthread (http://sources.redhat.com/pthreads-win32/). pthreadGC2.dll needs to be in your path.

## Modules

* api

  Provides the basic operations for interacting with libJah'Spotify (and in turn libspotify).
  The services package provides components which make the Api more easy to use.

* native

  Contains all native & JNI code interacting with libspotify.
  
* native-jar
  
  Creates a jar file with all required dependencies. If you supply this jar with your compiled
  program, a user won't have to setup its path to run the application.

## Example

This example shows how to initialize libJahSpotify and start playing a song.

	public class Main {
		public static void main(final String[] args) {
			// Determine the tempfolder and make sure it exists.
			File temp = new File(new File(Main.class.getResource("Main.class").getFile()).getParentFile(), "temp");
			temp.mkdirs();
	
			// Start JahSpotify
			JahSpotifyService.initialize(temp);
			JahSpotifyService.getInstance().getJahSpotify().addConnectionListener(new AbstractConnectionListener() {
				@Override
				public void initialized(final boolean initialized) {
					// Ask for the username and password.
					BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
					String username = null, password = null;
					try {
						System.out.print("Username: ");
						username = in.readLine();
						System.out.print("Password: ");
						password = in.readLine();
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(1);
					}
	
					// When JahSpotify is initialized, we can attempt to
					// login.
					if (initialized)
						JahSpotifyService.getInstance().getJahSpotify().login(username, password, null, false);
				}
	
				@Override
				public void loggedIn(final boolean success) {
					if (!success) {
						System.err.println("Unable to login.");
						System.exit(1);
					}
					// Get a track.
					Track t = JahSpotifyService.getInstance().getJahSpotify().readTrack(Link.create("spotify:track:6JEK0CvvjDjjMUBFoXShNZ"));
					// Wait for 10 seconds or until the track is loaded.
					MediaHelper.waitFor(t, 10);
					// If the track is loaded, play it.
					if (t.isLoaded())
						JahSpotifyService.getInstance().getJahSpotify().play(t.getId());
				}
			});
		}
	}

## Licensing

All libJah'Spotify code is released under the Apache 2.0 license