[app]
title = YarHooshmand
package.name = yarhooshmand
package.domain = ir.example
source.dir = .
source.include_exts = py,kv,png,txt,md
version = 0.1.0
requirements = python3,kivy==2.2.1,plyer,requests,urllib3,chardet,idna,certifi
orientation = portrait
fullscreen = 0
android.permissions = INTERNET, POST_NOTIFICATIONS
android.api = 31
android.minapi = 28
android.sdk = 31
android.ndk = 25b
android.archs = armeabi-v7a, arm64-v8a
android.allow_backup = True
android.enable_androidx = True
icon.filename = assets/icon.png

[buildozer]
log_level = 2
warn_on_root = 1
