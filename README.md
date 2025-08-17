
# YarHooshmand AI (Kivy) — v9 (Location + Wake-word + Plugins + CI)

This v9 package includes everything needed to build a production-ready app skeleton:
- Location-based reminders (latitude, longitude, radius)
- Wake-word integration placeholder (Porcupine) and instructions to enable it
- Plugin system (plugins/) with an example plugin
- Robust DB schema and services (reminders, location checks)
- GitHub Actions workflows to build APK, sign (optional via secrets), and create Release
- Java placeholders for BootReceiver and NotificationActionReceiver

**Important:** Some functionality (Porcupine wake-word, Google Drive sync, full native actionable notifications)
requires additional native libraries / credentials and cannot be fully implemented purely in Python here.
README explains how to enable them step-by-step.

## Quick start (local)
1. Install dependencies listed in `requirements.txt` in your build environment.
2. Use Buildozer (or the provided GitHub Actions) to build: `buildozer android debug`
3. Place any native credentials (Porcupine models, keystore) as explained in README before using those features.

## GitHub Actions
- `.github/workflows/build_and_release.yml` builds the APK using `ArtemSBulgakov/buildozer-action`,
  uploads artifact, and creates a GitHub Release. Optional signing step is provided — set secrets:
  `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASS` (see README).

## Notes on permissions
- Location features require runtime permission requests on Android:
  `ACCESS_FINE_LOCATION` and `ACCESS_BACKGROUND_LOCATION`. The app template includes manifest entries,
  but you must also request runtime permission when using on Android 10+ devices.

