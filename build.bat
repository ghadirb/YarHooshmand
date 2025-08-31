@echo off
title YarHooshmand - Build Script
echo ================================
echo  Building YarHooshmand APK ...
echo ================================
echo.

REM رفتن به پوشه پروژه
cd /d %~dp0

REM اجرای Gradle و ذخیره کل لاگ
call gradlew.bat clean assembleDebug --stacktrace > build_log.txt 2>&1

REM فیلتر کردن فقط خطاها به فایل جدا
findstr /i "error exception failed" build_log.txt > build_errors.txt

echo.
echo ================================
echo   📜 Build Logs saved:
echo       build_log.txt  (همه لاگ‌ها)
echo       build_errors.txt (فقط خطاها)
echo ================================
echo.

if exist app\build\outputs\apk\debug\app-debug.apk (
    echo ====================================
    echo ✅ Build Successful!
    echo APK File: app\build\outputs\apk\debug\app-debug.apk
    echo ====================================
) else (
    echo ====================================
    echo ❌ Build Failed! Check build_errors.txt
    echo ====================================
)

echo.
pause
