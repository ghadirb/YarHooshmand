# YarHooshmand (Kivy + Buildozer)

این پروژهٔ مینیمال برای ساخت APK بدون Android Studio طراحی شده:
- رابط کاربری ساده با Kivy
- اعلان محلی با plyer
- بیلد خودکار APK در GitHub Actions با کانتینر `kivy/buildozer`

## اجرای محلی (دسکتاپ)
```bash
python main.py
```

## ساخت APK با GitHub Actions
1) کل این پوشه را در یک ریپوی GitHub روی شاخه‌ی `main` آپلود/پوش کنید.
2) به تب **Actions** بروید و ورک‌فلو **Android APK (Buildozer)** را اجرا کنید.
3) فایل APK را از بخش **Artifacts** با نام `YarHooshmand-apk` دانلود کنید (داخلش `bin/*.apk`).

> نکته: بیلد اول زمان‌بر است چون SDK/NDK و وابستگی‌ها دانلود می‌شوند.
