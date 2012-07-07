===============================================================================
libJah'Spotify
===============================================================================

## Note

This project originated as a fork from the Jah'Spotify tool of Johan Lindquist. Johans repository contains a complete web application, this project aims to
be more of a library for other projects.

If you are searching for a web based Spotify tool which is also compatible on mobile devices you should check out Johans original work at:

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


## Licensing

All libJah'Spotify code is released under the Apache 2.0 license