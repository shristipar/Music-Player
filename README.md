# Music-Player (Android Legacy Project)

A legacy Android music player app (`com.example.sony.mixx`) updated to build and run on a modern macOS environment.

## Tech Stack

- Android Gradle Plugin: `1.0.0`
- Gradle Wrapper: `2.2.1`
- Compile SDK: `21`
- Min SDK: `17`
- Target SDK: `21`
- Java: **JDK 8 required** for this project toolchain

## Prerequisites

- macOS or Linux
- JDK 8
- Android SDK with:
  - `platform-tools`
  - `platforms;android-21`
  - `build-tools;30.0.3`
- (Optional) Android Emulator + system image

## 1) Set Java and Android SDK env vars

```bash
export JAVA_HOME="$HOME/.jdks/amazon-corretto-8.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
export ANDROID_HOME="$HOME/Library/Android/sdk"
```

## 2) Configure `local.properties`

Create `local.properties` in project root:

```properties
sdk.dir=/Users/<your-user>/Library/Android/sdk
```


## 3) Build

```bash
./gradlew build
```

## 4) Run on Emulator / Device

### Install APK

```bash
adb install -r app/build/outputs/apk/app-debug.apk
```

### Launch app

```bash
adb shell am start -n com.example.sony.mixx/.splash
```

## Emulator Setup (example)

```bash
sdkmanager "emulator" "platform-tools" "system-images;android-30;google_apis;arm64-v8a"
avdmanager create avd -n musicplayer_api30_arm -k "system-images;android-30;google_apis;arm64-v8a" -d pixel
emulator -avd musicplayer_api30_arm -no-snapshot -no-boot-anim
```

## Logs and Debugging

### Live app/runtime logs

```bash
adb logcat | grep -E "AndroidRuntime|mixx"
```

### Dump recent logs only

```bash
adb logcat -d -t 200
```

### Lint reports

- `app/build/outputs/lint-results.html`
- `app/build/outputs/lint-results.xml`

## Notes

- This is a legacy Android project. Some lint checks are intentionally disabled in `app/build.gradle` to keep builds stable with legacy assets/tooling.
- `./gradlew` must be executable:

```bash
chmod +x gradlew
```

## Known Issues

- **Legacy toolchain lock-in**: The project uses very old Gradle/AGP (`gradle-2.2.1`, plugin `1.0.0`), so modern Android/Java tooling is not fully compatible without broader migration.
- **Java version sensitivity**: Builds require **Java 8** for this setup; newer JDKs can fail with version/tooling errors.
- **Legacy lint environment noise**: `LintError` (API database not found) can appear in some environments due to old lint tooling behavior; this check is disabled to keep CI/local builds reliable.
- **Icon/resource modernization pending**: Several icon-density/style checks are disabled until full asset redesign/re-export is done.
- **Runtime data assumptions**: App logic still depends on local media path state; malformed/missing media paths can still surface edge-case runtime errors.

## Planned Fixes / Roadmap

- **Toolchain migration**: Upgrade Gradle wrapper and Android Gradle Plugin to modern supported versions.
- **SDK target refresh**: Raise `compileSdkVersion` and `targetSdkVersion`, then validate behavior on newer Android versions.
- **Dependency modernization**: Replace deprecated support libraries with AndroidX equivalents.
- **Lint hardening**: Re-enable currently disabled lint checks after icon/resource cleanup and tooling migration.
- **Asset pipeline cleanup**: Normalize launcher/notification icons and provide consistent density variants.
- **Runtime stability pass**: Add stronger null checks and media-path validation around playback/resume flows.
