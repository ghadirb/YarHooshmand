[app]
title = Yar Hooshmand
package.name = yar_hooshmand
package.domain = ir.yarhooshmand
source.dir = .
source.include_exts = py,kv,png,jpg,ttf,txt,md
fullscreen = 0
orientation = portrait
presplash.filename = 
icon.filename = 

# Kivy نیاز است؛ plyer برای نوتیفیکیشن؛ requests برای شبکه
requirements = python3,kivy==2.3.0,requests,plyer,certifi,urllib3,idna,chardet

# برای فونت فارسی اگر خواستی بعداً اضافه کن:
# android.presplash_color = #ffffff

android.permissions = INTERNET, WAKE_LOCK, RECEIVE_BOOT_COMPLETED, POST_NOTIFICATIONS

# API و SDK پیش‌فرض مناسب
android.api = 31
android.minapi = 28
android.sdk =  0
android.ndk =  0

# gradle نسخه مناسب
android.gradle_dependencies = 
android.enable_androidx = True

# بهبود سرعت
android.enable_legacy_native_api = False

# سرویس‌ها (فعلاً خاموش)
services = 

[buildozer]
log_level = 2
warn_on_root = 1
