@ECHO OFF
ECHO If wrapper jar is missing, Android Studio will regenerate it during Sync.
"%~dp0gradlew" %*
