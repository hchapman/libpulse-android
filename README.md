Libpulse-android is a JNI interface to functions of libpulse (which communicates with pulseaudio).

As of now, it is focused on providing access to introspection functions of the library, so as to
enable writing of a remote control volume application (see my project Reverb on github)

On Building libpulse.so for Android
==============================================
I got... kind of lucky when I built libpulse for Android and realized I could start this project. If anyone can streamline and improve on this, and build libpulse again, I'd like to know! Preferrably, the newest version (I think this is sub-1.0, even) of the library.


Building libpulse-android
==============================================
I'm working on moving lp-a to a gradle build structure.

CAREFUL: right now jniLibs is a bunch of symlinks to the .so files. I plan to update Android.mk or otherwise so
that ndk-build puts the prebuilts in the proper directories. It seems that
Gradle's NDK support is far too lacking for the scope involved in this project.