پروژهٔ سبک‌شده‌ی «یار هوشمند» - راهنمای سریع

1) اجرا:
   - فایل make_project.py را اجرا کن (python make_project.py) تا پوشه YarHooshmand ساخته شود.
   - سپس Android Studio را باز کن و از File -> Open پوشه YarHooshmand را انتخاب کن.

2) پیش‌نیازها:
   - اندروید استودیو (پرتابل هم اوکیه)
   - SDK Platform 28+ و Build-tools نصب باشد (Android Studio هنگام sync درخواست می‌کند)
   - اینترنت یا پروکسی برای دانلود وابستگی‌ها در اولین sync نیاز است.

3) تست سریع:
   - پس از sync از Build -> Build Bundle(s) / APK(s) -> Build APK(s) استفاده کن.
   - یا در CMD داخل پوشه پروژه دستور gradlew.bat assembleDebug را اجرا کن (Windows).

4) Google Drive keys:
   - در بخش هوشمند، لینک پیش‌فرض فایل تکست Google Drive قرار دارد.
   - فرمت فایل تکست: هر خط یک کلید یا "service:key" مانند:
       openai:sk-...
       openrouter:rk-...
   - اگر دانلود از Drive انجام نشد، می‌توانی کلید را دستی در SharedPreferences ذخیره کنی (تنظیمات داخل کد).

5) نکات امنیتی:
   - نگهداری کلیدها روی دستگاه ناامن است؛ برای انتشار واقعی از سرور واسط استفاده کن.

اگر خواستی من می‌تونم:
- mirrorهای لازم را به build.gradle اضافه کنم (برای کشورهایی که dl.google.com فیلتر است)
- نمونه gradle-wrapper.jar واقعی و gradle-7.5-bin.zip را راهنمایی کنم که دستی دانلود و قرار دهی
- پس از تست، بخش IAP را اضافه کنم.
