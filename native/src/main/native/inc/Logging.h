#ifndef JAHSPOTIFY_LOGGING
#define JAHSPOTIFY_LOGGING

#include "JNIHelpers.h"
#include "jni_md.h"
#include "jni.h"

#include <stdlib.h>
#include <stdarg.h>
#include <stdio.h>

void log_trace(const char* component, const char *subComponent, const char* format, ...);
void log_debug(const char* component, const char *subComponent, const char* format, ...);
void log_info(const char *component, const char *subComponent, char *format, ...);
void log_warn(const char *component, const char *subComponent, char *format, ...);
void log_error(const char *component, const char *subComponent, char *format, ...);
void log_fatal(const char* component, const char *subComponent, char* format, ...);
void log_d(jobject o);

#endif
