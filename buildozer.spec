[app]
title = YarHooshmand AI v9
package.name = yarhooshmand_ai_v9
package.domain = org.hooman
source.dir = .
source.include_exts = py,kv,png,jpg,ttf,txt,json,db,xml
icon.filename = assets/icon.png
version = 9.0.0

# کتابخانه‌هایی که برنامه نیاز داره
requirements = python3,kivy==2.3.0,kivymd==1.2.0,plyer,jdatetime,requests

# حالت نمایش
orientation = portrait
fullscreen = 0

# سرویس‌ها
android.services = remindersvc:foreground, locationmonitor:foreground

# دسترسی‌ها
android.permissions = VIBRATE, WAKE_LOCK, FOREGROUND_SERVICE, POST_NOTIFICATIONS, RECEIVE_BOOT_COMPLETED, INTERNET, ACCESS_NETWORK_STATE, ACCESS_FINE_LOCATION, ACCESS_BACKGROUND_LOCATION

# مسیر مانيفست سفارشی و کد جاوا
android.manifest = android/AndroidManifest.tmpl.xml
android.add_src = java_src

# نسخه‌های اندروید و NDK
android.api = 31
android.minapi = 21
android.ndk = 23b
android.archs = armeabi-v7a, arm64-v8a

[buildozer]
log_level = 2
warn_on_root = 1
