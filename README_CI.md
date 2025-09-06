# YarHooshmand – Final Modern Build

## Codemagic setup
1) In Codemagic > App settings > Code signing, add your keystore as a **Keystore reference** with alias/passwords.
2) Ensure the reference is named `keystore_reference` (or update `codemagic.yaml` accordingly).
3) Trigger the **Android Release** workflow. Artifacts (APK/AAB) will be available in build artifacts.

## Notes
- Requires JDK 17 and AGP that your project already uses.
- Make sure you have set SMS permission logic at runtime if you plan to use SMS reminders.
