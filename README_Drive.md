# Google Drive Backup (YarHooshmand)

1) فایل OAuth `client_secret_*.json` خود را به مسیر زیر کپی و به `client_secret.json` تغییرنام دهید:
```
app/src/main/res/raw/client_secret.json
```
2) اپ را اجرا کنید و از مسیر **تنظیمات → ورود به گوگل درایو** وارد شوید (Scope: DRIVE_FILE).
3) Worker بکاپ به‌صورت خودکار فایل JSON می‌سازد و در صورت ورود موفق، همان فایل به Google Drive آپلود می‌شود.
4) Export دستی JSON/CSV هم از صفحه اصلی در دسترس است.
