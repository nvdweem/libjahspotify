package jahspotify.services;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *        or more contributor license agreements.  See the NOTICE file
 *        distributed with this work for additional information
 *        regarding copyright ownership.  The ASF licenses this file
 *        to you under the Apache License, Version 2.0 (the
 *        "License"); you may not use this file except in compliance
 *        with the License.  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Unless required by applicable law or agreed to in writing,
 *        software distributed under the License is distributed on an
 *        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *        KIND, either express or implied.  See the License for the
 *        specific language governing permissions and limitations
 *        under the License.
 */

import jahspotify.JahSpotify;
import jahspotify.media.Album;
import jahspotify.media.Link;
import jahspotify.media.Playlist;
import jahspotify.media.Track;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Manages all the queues within Jah'Spotify.  It allows for multiple queues to be maintained - only one is considered
 * the current queue however.
 *
 * @author Johan Lindquist
 */
public class QueueManager
{
    private Log _log = LogFactory.getLog(QueueManager.class);

    private Lock _queueLock = new ReentrantLock();

    public static final Link DEFAULT_QUEUE_LINK = Link.create("jahspotify:queue:default");

    private Set<QueueListener> _queueListeners = new HashSet<QueueListener>();

    private jahspotify.services.Queue _currentQueue;

    private JahSpotifyService _jahSpotifyService = JahSpotifyService.getInstance();
    private JahSpotify _jahSpotify;

    private static QueueManager instance;
    public static synchronized QueueManager getInstance() {
    	if (instance == null) {
    		instance = new QueueManager();
    		instance.initialize();
    	}
    	return instance;
    }

    private void initialize()
    {
        _jahSpotify = _jahSpotifyService.getJahSpotify();

        _currentQueue = new jahspotify.services.Queue();
        _currentQueue.setId(DEFAULT_QUEUE_LINK);
        _currentQueue.setQueueConfiguration(new QueueConfiguration());
        _currentQueue.setQueueStatistics(new QueueStatistics());
    }

    /**
     * Adds the specified links (playlists, folders, track, etc) to the specified queue.  If the queue does not yet
     * exists, it will be created.
     *
     * @param queue
     * @param uris
     * @return
     */
    public jahspotify.services.Queue addToQueue(final Link queue, final List<Link> uris)
    {
        if (!queue.equals(DEFAULT_QUEUE_LINK))
        {
            throw new IllegalArgumentException("URIs other than the default queue are not yet supported");
        }

        final List<Link> allURIs = new ArrayList<Link>();

        for (final Link uri : uris)
        {
            if (uri.isPlaylistLink())
            {
                _log.debug("Received playlist, processing for tracks");
                // Have playlist
                Playlist playlist = _jahSpotify.readPlaylist(uri, 0, 0);
                if (playlist != null)
                {
                    for (final Link track : playlist.getTracks())
                    {
                        allURIs.add(track);
                    }
                    _log.debug("Playlist processed ...");
                }
                else
                {
                    _log.warn("Playlist could not be retrieved");
                }
            }
            else if (uri.isAlbumLink())
            {
                _log.debug("Received album, processing for tracks");
                // Have album
                Album album = _jahSpotify.readAlbum(uri);
                if (album != null)
                {
                    for (Link track : album.getTracks())
                    {
                        allURIs.add(track);
                    }
                    _log.debug("Album processed ...");
                }
                else
                {
                    _log.warn("Album could not be retrieved");
                }
            }
            else if (uri.isTrackLink())
            {
                // Have track
                allURIs.add(uri);
            }
        }

        if (_log.isDebugEnabled())
        {
            _log.debug("Queueing tracks: " + allURIs);
        }

        final jahspotify.services.Queue currentQueue = addTracksToQueue(allURIs);

        if (_log.isDebugEnabled())
        {
            _log.debug("Tracks queued: " + allURIs);
        }

        return currentQueue;
    }

    /**
     * @param queue
     * @param uris
     * @return
     */
    public jahspotify.services.Queue addToQueue(Link queue, Link... uris)
    {
    	if (queue == null) queue = DEFAULT_QUEUE_LINK;
        return addToQueue(queue, Arrays.asList(uris));
    }

    public List<Link> retrieveQueues()
    {
        return Arrays.asList(DEFAULT_QUEUE_LINK);
    }

    public void deleteQueue(final Link queue)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * @param queue
     * @return
     */
    public jahspotify.services.Queue setCurrentQueue(final Link queue)
    {
        if (!queue.equals(DEFAULT_QUEUE_LINK))
        {
            throw new IllegalArgumentException("URIs other than the default queue are not yet supported");
        }
        return this.getCurrentQueue(0);
    }

    private jahspotify.services.Queue addTracksToQueue(final List<Link> trackURIs)
    {
        _queueLock.lock();
        try
        {
            final List<QueueTrack> queuedTracks = new ArrayList<QueueTrack>();
            // Add all elements to the array
            for (Link trackURI : trackURIs)
            {
                final Track track = _jahSpotifyService.getJahSpotify().readTrack(trackURI);
                if (track != null)
                {
                    QueueTrack queuedTrack = new QueueTrack("jahspotify:queue:default:" + UUID.randomUUID().toString(), trackURI, QueueManager.DEFAULT_QUEUE_LINK, "JahSpotify");
                    queuedTrack.setLength(track.getLength());
                    queuedTracks.add(queuedTrack);
                    _currentQueue.getQueuedTracks().add(queuedTrack);
                }
            }

            final QueueTrack[] queueTracks = new QueueTrack[queuedTracks.size()];
            queuedTracks.toArray(queueTracks);

            // Update the maximum queue size seen if the queue size is now the biggest ever seen
            _currentQueue.getQueueStatistics().updateMaxQueueSize(_currentQueue.getQueuedTracks().size());

            jahspotify.services.Queue queue = getCurrentQueue(0);

            for (final QueueListener queueListener : _queueListeners)
            {
                queueListener.tracksAdded(DEFAULT_QUEUE_LINK, queueTracks);
            }

            return queue;
        }
        finally
        {
            _queueLock.unlock();
        }
    }

    public int clearQueue(final Link queue)
    {
        if (!queue.equals(DEFAULT_QUEUE_LINK))
        {
            throw new IllegalArgumentException("URIs other than the default queue are not yet supported");
        }

        final int size = _currentQueue.getQueuedTracks().size();
        _currentQueue.getQueuedTracks().clear();
        return size;
    }

    public int deleteQueuedTrack(final Link queue, final Link uri)
    {
        if (!queue.equals(DEFAULT_QUEUE_LINK))
        {
            throw new IllegalArgumentException("URIs other than the default queue are not yet supported");
        }

        // uris are in the form of:
        // spotify:track:...
        // jahspotify:queue:<quename>:<uuid>

        final List<QueueTrack> removedTracks = new ArrayList<QueueTrack>();
        int count = 0;

        if (uri.isTrackLink())
        {
            for (QueueTrack queuedTrack : _currentQueue.getQueuedTracks())
            {
                if (queuedTrack.getTrackUri().equals(uri))
                {
                    removedTracks.add(queuedTrack);
                    _currentQueue.getQueuedTracks().remove(queuedTrack);
                    count++;
                }
            }
        }
        else if (uri.isQueueLink())
        {
            for (QueueTrack queuedTrack : _currentQueue.getQueuedTracks())
            {
                // Extract the ID from the URI
                String queueId = uri.getQueueId();
                if (queuedTrack.getId().equals(queueId))
                {
                    removedTracks.add(queuedTrack);
                    _currentQueue.getQueuedTracks().remove(queuedTrack);
                    count++;
                }
            }
        }

        if (count > 0)
        {
            for (QueueListener queueListener : _queueListeners)
            {
                queueListener.tracksRemoved(DEFAULT_QUEUE_LINK, removedTracks.toArray(new QueueTrack[removedTracks.size()]));
            }
        }

        return count;
    }

    public QueueConfiguration getQueueConfiguration(Link queue)
    {
        if (!queue.equals(DEFAULT_QUEUE_LINK))
        {
            throw new IllegalArgumentException("URIs other than the default queue are not yet supported");
        }

        return _currentQueue.getQueueConfiguration();
    }

    public void setQueueConfiguration(Link queue, QueueConfiguration queueConfiguration) throws MalformedURLException
    {
        if (!queue.equals(DEFAULT_QUEUE_LINK))
        {
            throw new IllegalArgumentException("URIs other than the default queue are not yet supported");
        }

        _currentQueue.getQueueConfiguration().setRepeatCurrentQueue(queueConfiguration.isRepeatCurrentQueue());
        _currentQueue.getQueueConfiguration().setRepeatCurrentTrack(queueConfiguration.isRepeatCurrentTrack());
        _currentQueue.getQueueConfiguration().setShuffle(queueConfiguration.isShuffle());
        _currentQueue.getQueueConfiguration().setAutoRefill(queueConfiguration.isAutoRefill());
        _currentQueue.getQueueConfiguration().setReportEmptyQueue(queueConfiguration.isReportEmptyQueue());
        _currentQueue.getQueueConfiguration().setReportTrackChanges(queueConfiguration.isReportTrackChanges());
        _currentQueue.getQueueConfiguration().setCallbackURL(queueConfiguration.getCallbackURL());

    }

    public jahspotify.services.Queue getQueue(final Link queue)
    {
        if (!queue.equals(DEFAULT_QUEUE_LINK))
        {
            throw new IllegalArgumentException("URIs other than the default queue are not yet supported");
        }
        return null;
    }

    public jahspotify.services.Queue getCurrentQueue(final int count)
    {
        _queueLock.lock();
        try
        {
            return _currentQueue;
        }
        finally
        {
            _queueLock.unlock();
        }
    }

    public jahspotify.services.Queue shuffle(final Link uri)
    {
        _queueLock.lock();
        try
        {
            final List<QueueTrack> queuedTracks = new ArrayList<QueueTrack>(_currentQueue.getQueuedTracks());
            _currentQueue.getQueuedTracks().clear();
            Collections.shuffle(queuedTracks);
            _currentQueue.getQueuedTracks().addAll(queuedTracks);
            return getCurrentQueue(0);
        }
        finally
        {
            _queueLock.unlock();
        }
    }

    public void addQueueListener(final QueueListener queueListener)
    {
        _queueListeners.add(queueListener);
    }

    /**
     * Retrieves the next track in queue.  This will return null if no tracks are queued
     *
     * @return
     */
    public QueueTrack getNextQueueTrack()
    {
        boolean empty = _currentQueue.getQueuedTracks().isEmpty();
        final QueueTrack nextTrack = _currentQueue.getQueuedTracks().poll();

        if (nextTrack != null)
        {
            _log.debug("Retrieved next track: " + nextTrack);
            for (QueueListener queueListener : _queueListeners)
            {
                queueListener.newTrackAtFront(DEFAULT_QUEUE_LINK, nextTrack);
            }
        }

        if (!empty && _currentQueue.getQueuedTracks().isEmpty())
        {
            _log.debug("Retrieved last track in queue, signalling empty queue");
            for (QueueListener queueListener : _queueListeners)
            {
                queueListener.queueEmpty(DEFAULT_QUEUE_LINK, nextTrack);
            }
        }

        return nextTrack;
    }

    public QueueTrack peekAtNextTrack() {
    	return _currentQueue.getQueuedTracks().isEmpty() ? null : _currentQueue.getQueuedTracks().getFirst();
    }

    public void addToQueue(final Link queueLink, final QueueTrack queueTrack)
    {
        _currentQueue.getQueuedTracks().add(queueTrack);

        // Update the maximum queue size seen if the queue size is now the biggest ever seen
        _currentQueue.getQueueStatistics().updateMaxQueueSize(_currentQueue.getQueuedTracks().size());

        for (final QueueListener queueListener : _queueListeners)
        {
            queueListener.tracksAdded(queueLink, queueTrack);
        }

    }
}
