# Sony BRAVIA TV Remote Control App (Native Android)

A native Android remote control application for Sony BRAVIA TVs built with **Kotlin**, **Jetpack Compose (Material 3)**, and the official **Sony IRCC-IP** (InfraRed Compatible Control over IP) protocol.

---

## ✨ Features

- **Direct IRCC-IP Network Control**: Sends remote control commands over the local Wi-Fi / LAN network with low latency using SOAP XML POST requests to `http://<TV_IP>/sony/ircc`.
- **SSDP / UPnP Auto-Discovery**: Automatically searches for Sony BRAVIA TVs on your local Wi-Fi without needing manual IP lookup.
- **Wake-on-LAN (WoL)**: Sends UDP Magic Packets (ports 9 & 7) to wake TVs from suspended/standby states.
- **Full TV Remote Layout**:
  - **5-Way Tactile D-Pad**: Directional navigation (Up, Down, Left, Right) and Center OK / Enter button.
  - **Volume & Channel Rockers**: Dedicated Vol +/- & Ch +/- pills with central MUTE button.
  - **Quick Action Bar**: Power, Source Input, Subtitles, Options/Menu, Home, Back.
  - **Streaming Shortcuts**: Netflix, YouTube, Prime Video direct launch buttons.
  - **Media Controls**: Rewind, Play/Pause, Fast Forward.
  - **Numeric & Color Keys**: Expandable bottom sheet with 0-9 numpad, HDMI 1-4 source switchers, and Sony interactive color buttons (Red, Green, Yellow, Blue).
- **Haptic Touch Feedback**: Vibrates on button clicks for a responsive physical remote feel.
- **Pre-Shared Key (PSK) Security**: Supports Sony's `X-Auth-PSK` header authentication.

---

## 📺 One-Time Sony TV Configuration

To allow this app to control your Sony TV on your local network:

1. On your Sony TV remote, press **Settings** (or the Gear icon).
2. Go to **Network & Internet** → **Home network** → **IP control**.
3. Set **Authentication** to **Normal and Pre-Shared Key** (or **Pre-Shared Key**).
4. Select **Pre-Shared Key** and enter a code (e.g. `1234` or `0000`).
5. (Optional but recommended) Go to **Settings** → **Network & Internet** → **Remote device settings** → **Control remotely** and turn it **ON** (enables Wake-on-LAN and remote power control).

---

## 🏗️ Project Architecture

```
remote-app/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/sonyremote/app/
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/
│   │   │   │   ├── model/
│   │   │   │   │   ├── IrccCommand.kt        # Sony Base64 IRCC codes & enum
│   │   │   │   │   └── TvDevice.kt           # TV model definition
│   │   │   │   ├── network/
│   │   │   │   │   ├── SonyTvClient.kt       # OkHttp SOAP & REST client
│   │   │   │   │   ├── SsdpDiscoveryManager.kt # UDP Multicast UPnP scanner
│   │   │   │   │   └── WakeOnLanManager.kt   # UDP Magic Packet sender
│   │   │   │   └── repository/
│   │   │   │       └── SonyTvRepository.kt   # App persistence & state
│   │   │   └── ui/
│   │   │       ├── RemoteScreen.kt           # Main Remote UI
│   │   │       ├── components/
│   │   │       │   ├── ConnectionBar.kt      # Status & TV details header
│   │   │       │   ├── DPadControl.kt        # 5-way circular D-pad
│   │   │       │   ├── VolumeChannelRocker.kt# Rocker volume/channel
│   │   │       │   ├── NumpadSheet.kt        # Numbers & Color keys
│   │   │       │   └── SettingsDialog.kt     # Wi-Fi scanner & manual IP sheet
│   │   │       ├── theme/                    # Material 3 Dark Remote Theme
│   │   │       └── viewmodel/
│   │   │           ├── RemoteUiState.kt
│   │   │           └── RemoteViewModel.kt
│   │   └── res/
│   │       └── values/ (strings.xml, colors.xml, themes.xml)
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🚀 How to Run the App

1. **Open in Android Studio**:
   - Launch Android Studio.
   - Click **Open** and choose this `/Users/akarshjain/Programming/remote-app` directory.
   - Wait for Gradle sync to complete.

2. **Connect Device / Emulator**:
   - Connect an Android device (via USB or Wi-Fi debugging) that is connected to the **same Wi-Fi network** as your Sony TV.
   - Or start an Android Emulator with host networking enabled.

3. **Click Run (`Shift + F10`)**:
   - The app will automatically attempt to discover your Sony TV on the Wi-Fi.
   - If not found automatically, tap the **Settings** icon on the top right, enter your TV's IP address (e.g. `192.168.1.50`) and your PSK (e.g. `1234`), and tap **Save & Connect**.
