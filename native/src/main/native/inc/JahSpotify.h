#ifndef JAHSPOTIFY

#define JAHSPOTIFY

typedef struct media {
	struct media* prev;
	struct media* next;
	jobject javainstance;
	sp_track* track;
	sp_album* album;
	sp_artist* artist;
	int browse;
} media;

void addLoading(jobject javainstance, sp_track* track, sp_album* album, sp_artist* artist, int browse);
void checkLoaded();

#endif
