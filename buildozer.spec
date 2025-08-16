[app]
# (str) Title of your application
title = YourAppTitle

# (str) Package name
package.name = yourappname

# (str) Package domain (needed for android)
package.domain = org.yourdomain

# (str) Source code where the main.py live
source.dir = .

# (str) Version of your application
version = 0.1

# (list) Application requirements
requirements = python3,kivy

# (str) Android API to use
android.api = 30

# (str) Android NDK version to use
android.ndk = 21b

# (str) Android SDK version to use
android.sdk = 30

# (str) Android build tools version
android.build_tools_version = 30.0.3

# (bool) Whether to accept licenses non-interactively
android.accept_license = true

# (list) Permissions
android.permissions = INTERNET, WRITE_EXTERNAL_STORAGE

# (str) Path to the AIDL and sdkmanager
android.sdkmanager = /usr/bin/sdkmanager
android.aidl = /usr/bin/aidl
