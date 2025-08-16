[app]
# (str) Title of your application
title = MyApp

# (str) Package name
package.name = myapp

# (str) Package domain (needed for android)
package.domain = org.example

# (str) Source code where the main.py live
source.dir = .

# (list) Application requirements
requirements = python3,kivy

# (str) Supported orientation (one of landscape, sensorLandscape, portrait or all)
orientation = all

# (str) Android API version to use
android.api = 30

# (str) Android NDK version to use
android.ndk = 21b

# (str) Android SDK version to use
android.sdk = 30

# (str) Android build tools version
android.build_tools_version = 30.0.3

# (str) Icon of the application
icon.filename = assets/icon.png

# (list) Permissions
android.permissions = INTERNET
