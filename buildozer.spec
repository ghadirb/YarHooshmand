[app]
title = YarHooshmand
package.name = yarhooshmand
package.domain = com.example
source.dir = .
source.include_exts = py,kv,png,jpg,ttf,txt,json
version = 0.1.0
requirements = python3,kivy,requests,plyer
orientation = portrait
fullscreen = 0
osx.kivy_version = 2.3.0
icon.filename = icon.png

[buildozer]
log_level = 2
warn_on_root = 0

[android]
android.api = 33
android.minapi = 28
android.sdk = 33
android.ndk = 25b
android.arch = armeabi-v7a,arm64-v8a
android.accept_sdk_license = True
# برای تستِ راحت، اینترنت مجاز:
android.permissions = INTERNET, VIBRATE, WAKE_LOCK, RECEIVE_BOOT_COMPLETED

[dependencies]
