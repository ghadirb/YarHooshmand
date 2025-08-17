
[app]
title = YarHooshmand AI v9
package.name = yarhooshmand_ai_v9
package.domain = org.hooman
source.dir = .
source.include_exts = py,kv,png,jpg,ttf,txt,json,db,sqlite,xml
icon.filename = assets/icon.png
version = 9.0.0
requirements = python3,kivy,kivymd,plyer,jdatetime,android,requests
orientation = portrait
fullscreen = 0

android.services = remindersvc:foreground, locationmonitor:foreground
android.permissions = VIBRATE, WAKE_LOCK, FOREGROUND_SERVICE, POST_NOTIFICATIONS, RECEIVE_BOOT_COMPLETED, INTERNET, ACCESS_NETWORK_STATE, ACCESS_FINE_LOCATION, ACCESS_BACKGROUND_LOCATION
android.manifest = android/AndroidManifest.tmpl.xml
android.add_src = java_src
android.api = 33
android.minapi = 21

[buildozer]
log_level = 1
