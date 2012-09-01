#ifndef JAHSPOTIFY

#define JAHSPOTIFY

typedef struct track {
	struct track* prev;
	struct track* next;
	jobject javatrack;
	sp_track* track;
} track;

void addLoading(jobject javatrack, sp_track* track);
void checkLoaded();

#endif
