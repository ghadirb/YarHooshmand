[app]
# (str) Title of your application
title = MyApp

# (str) Package name
package.name = myapp

# (str) Package domain (needed for android)
package.domain = org.example

# (str) Source code where the main.py live
source.dir = .

# (str) Version of your application
version = 0.1

# (list) Application requirements
requirements = python3,kivy

# (str) Android API to use
android.api = 30

# (str) Android NDK version
android.ndk = 21b

# (str) Android SDK version
android.sdk = 30

# (str) Android build tools version
android.build_tools_version = 30.0.3

# (bool) Whether to accept licenses non-interactively
android.accept_license = True

# (list) Permissions
android.permissions = INTERNET

# (str) Path to the AIDL and sdkmanager
android.aidl_path = /usr/local/android/sdk/aidl
android.sdkmanager_path = /usr/local/android/sdk/tools/bin/sdkmanager
