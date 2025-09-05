# Merge Report: YarHooshmand Smart

## Summary

- Base source taken from **V3_patched** (assumed latest fixed source).

- Build tooling and CI imported from **V2** where useful (.github workflows, gradle wrapper if missing, scripts, etc.).

- Ensured a valid adaptive launcher icon to avoid generic blue icon.

- Checked AndroidManifest for a MAIN/LAUNCHER activity and added one if missing (naive fix).

- Added a `BUILD_ME.sh` for easy local builds.


## Copied From V2 (tooling)

- .github/workflows/android-build.yml
- build.gradle
- build.gradle.kts
- gradle.properties
- gradle/wrapper/gradle-wrapper.jar
- gradle/wrapper/gradle-wrapper.properties
- gradlew
- gradlew.bat
- settings.gradle
- settings.gradle.kts

## Icon Changes

- drawable/ic_launcher_foreground.xml: created

## Manifest Check

- YarHooshmandSmartV3/app/src/main/AndroidManifest.xml: has_launcher=True 

## Feature Signals (heuristic)

- google_drive_keys: FOUND
  - YarHooshmandSmartV3/README.md
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/YarApp.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/keys/KeysManager.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/models/ModelManager.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/net/ChatClient.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/ui/SettingsScreen.kt
  - build.gradle
  - settings.gradle
- model_priority: FOUND
  - YarHooshmandSmartV3/README.md
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/YarApp.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/models/ModelManager.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/net/ChatClient.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/reminders/ReminderWorker.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/reminders/VoiceConfirmService.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/ui/AppNav.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/ui/ModelsScreen.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/ui/SettingsScreen.kt
- reminders_table: FOUND
  - YarHooshmandSmartV3/README.md
  - YarHooshmandSmartV3/app/build.gradle.kts
  - YarHooshmandSmartV3/app/src/main/AndroidManifest.xml
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/YarApp.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/data/AppDatabase.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/data/ReminderDao.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/data/ReminderEntity.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/reminders/ReminderWorker.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/reminders/Scheduler.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/reminders/VoiceConfirmService.kt
  - (+3 more)
- voice_features: FOUND
  - YarHooshmandSmartV3/app/src/main/AndroidManifest.xml
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/YarApp.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/reminders/ReminderWorker.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/reminders/VoiceConfirmService.kt
- messaging_to_model: FOUND
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/models/ModelManager.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/net/ChatClient.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/ui/AppNav.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/ui/ChatScreen.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/ui/ModelsScreen.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/ui/RemindersScreen.kt
  - YarHooshmandSmartV3/app/src/main/java/org/yarhooshmand/smartv3/ui/SettingsScreen.kt