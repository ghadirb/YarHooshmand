@echo off
chcp 65001 >nul
echo Building APK (debug)...
if exist gradlew.bat (
  call gradlew.bat assembleDebug
) else (
  echo gradlew.bat not found. Please run 'gradle wrapper' or use Android Studio.
  pause
  exit /b 1
)
if %ERRORLEVEL% NEQ 0 (
  echo Build failed.
  pause
  exit /b 1
)
echo Build succeeded. Opening output folder...
explorer "%cd%\app\build\outputs\apk\debug"
pause
