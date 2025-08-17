[app]
title = YarHooshmand AI v9
package.name = yarhooshmand_ai_v9
package.domain = org.hooman
source.dir = .
source.include_exts = py,kv,png,jpg,ttf,txt,json,db,xml
icon.filename = assets/icon.png
version = 9.0.0

# هماهنگ با requirements.txt
requirements = python3,kivy==2.3.0,kivymd==1.2.0,plyer,jdatetime,requests

orientation = portrait
fullscreen = 0

# سرویس‌ها
android.services = remindersvc:foreground, locationmonitor:foreground

# دسترسی‌ها
android.permissions = VIBRATE, WAKE_LOCK, FOREGROUND_SERVICE, POST_NOTIFICATIONS, RECEIVE_BOOT_COMPLETED, INTERNET, ACCESS_NETWORK_STATE, ACCESS_FINE_LOCATION, ACCESS_BACKGROUND_LOCATION

android.manifest = android/AndroidManifest.tmpl.xml
android.add_src = java_src

# نسخه API → پایدارتر روی 31
android.api = 31
android.minapi = 21

[buildozer]
log_level = 1
