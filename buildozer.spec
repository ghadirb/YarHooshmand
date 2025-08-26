[app]

title = MyApp
package.name = myapp
package.domain = org.myapp
source.dir = .
source.include_exts = py,png,jpg,kv,atlas
version = 0.1
requirements = python3,kivy,kivymd,requests
orientation = portrait
fullscreen = 1
android.permissions = INTERNET
android.api = 33
android.minapi = 21
android.ndk = 23b
android.sdk = 24
android.ndk_path =
android.sdk_path =
android.gradle_dependencies = com.android.support:appcompat-v7:28.0.0
android.arch = armeabi-v7a
android.allow_backup = True
android.logcat_filters = *:S python:D
android.entrypoint = main.py
android.manifest.intent_filters =

[buildozer]

log_level = 2
warn_on_root = 1
