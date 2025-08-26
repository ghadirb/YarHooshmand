[app]

title = YarHooshmand
package.name = yarhooshmand
package.domain = org.yarhooshmand
source.dir = .
source.include_exts = py,png,jpg,kv,atlas
version = 1.0

requirements = python3,kivy,kivymd,requests

orientation = portrait
fullscreen = 1

android.api = 33
android.minapi = 21
android.ndk = 23b
android.arch = armeabi-v7a

android.permissions = INTERNET,VIBRATE,RECEIVE_BOOT_COMPLETED

presplash.filename = %(source.dir)s/data/presplash.png
icon.filename = %(source.dir)s/data/icon.png

# جلوگیری از خطاهای مربوط به SSL
android.allow_backup = True
android.debug = 1
