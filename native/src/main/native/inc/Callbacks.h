#ifndef JAHSPOTIFY_CALLBACKS

#define JAHSPOTIFY_CALLBACKS

#include <libspotify/api.h>

void startPlaybackSignalled();
int signalLoggedIn(int loggedIn);
int signalConnected();
int signalDisconnected();
int signalLoggedOut();
void signalBlobUpdated(const char* blob);

int signalStartFolderSeen(char *folderName, uint64_t folderId);
int signalSynchStarting(int numPlaylists);
int signalSynchCompleted();
int signalMetadataUpdated(sp_playlist *playlist);
int signalEndFolderSeen();

int signalTrackEnded(char *uri, bool forcedTrackEnd);
int signalTrackStarted(char *uri);
void signalPlayTokenLost();
int signalPlaylistSeen(const char *playlistName, char *linkName);

int signalSearchComplete(sp_search *search, int32_t token);
int signalImageLoaded(sp_image *image, jobject imageInstance);
int signalTrackLoaded(sp_track *track, int32_t token);
int signalPlaylistLoaded(jobject playlist);
int signalAlbumBrowseLoaded(sp_albumbrowse *albumBrowse, jobject token);
int signalArtistBrowseLoaded(sp_artistbrowse *artistBrowse, jobject token);

jobject createSearchResult(JNIEnv* env);
void signalToplistComplete(sp_toplistbrowse *result, jobject nativeSearchResult);

#endif