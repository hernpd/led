# LED Project

This project uses a local AAR dependency (`core-release.aar`).
The AAR only contains native libraries for `arm64-v8a` and `armeabi-v7a`.
Devices or emulators running on x86 or x86_64 architectures will fail to load
the native libraries and the app will exit immediately.

To run the app successfully:

1. Use a physical device with an ARM-based CPU, **or**
2. Obtain a version of `core-release.aar` that includes `x86` and `x86_64` native
   libraries.

The `app` module is configured to limit supported ABIs to the ones included in
`core-release.aar`.
