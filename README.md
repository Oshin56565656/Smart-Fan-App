# Wi-Fi Enabled Smart Fan Controller with Android Application

## Project Overview

This project presents a comprehensive solution for intelligent fan control, leveraging the power of a **NodeMCU (ESP8266)** microcontroller as a compact web server and an intuitive **Android application** for user interaction. The system enables users to remotely control an electric fan — including setting automatic shut-off schedules — over a **local Wi-Fi network**, enhancing convenience, flexibility, and energy efficiency.

---

## Components Required

| Component                     | Quantity | Description                                                                 |
|------------------------------|----------|-----------------------------------------------------------------------------|
| NodeMCU (ESP8266)            | 1        | Microcontroller with Wi-Fi, acts as the server and controller              |
| Relay Module (5V or 3.3V)    | 1        | Switches the AC fan ON/OFF                                                 |
| Logic Level Shifter          | 1        | Converts 3.3V (NodeMCU) to 5V (for relay modules requiring 5V logic)       |
| Android Smartphone           | 1        | Runs the control application                                               |
| USB Cable + Power Source     | 1        | Powers the NodeMCU                                                         |
| Jumper Wires                 | Several  | For wiring connections between components                                  |
| Breadboard or PCB            | 1        | Optional, for prototyping or final assembly                                |
| AC Fan                       | 1        | Any standard fan with ON/OFF wiring support via relay                      |

> **Note:** Some relay modules expect a 5V signal to trigger properly. Since the NodeMCU uses 3.3V logic, a **logic level shifter** ensures reliable operation and protects components from voltage mismatch.

---

## Key Components

### 1. NodeMCU (ESP8266) Microcontroller
- **Role:** Embedded web server and primary control unit.
- **Functions:**
  - Connects to the local Wi-Fi network.
  - Hosts an HTTP server that responds to specific GET requests.
  - Interfaces with a relay module (via logic level shifter if needed) to control the fan.
  - Uses **NTP (Network Time Protocol)** to fetch accurate time from the internet.
  - Stores and manages fan state and auto-off schedule in volatile memory.
  - Monitors current time and automatically turns the fan off as scheduled.

### 2. Android Application
- **Platform:** Java (Android Studio)
- **Features:**
  - **Fan ON/OFF Control:** Sends HTTP GET requests to `/on` and `/off` endpoints.
  - **Auto-Off Scheduler:**
    - Uses `TimePickerDialog` to let users set a shutdown time.
    - Sends a GET request like `/setofftime?hour=HH&minute=MM` to the NodeMCU.
    - Displays confirmation of the scheduled time in the UI.
  - **Cancel Schedule:** Sends a GET request to `/cancel` to clear active schedules.
  - **Dynamic IP Storage:** Lets users enter and save the NodeMCU's IP using `SharedPreferences`.
  - **Status Monitoring:** Displays real-time fan status (ON, OFF, Scheduled, or Error).
  - **Async Networking:** Uses `ExecutorService` to avoid UI freezing.
  - **Error Handling:** Catches networking issues and provides feedback via Toasts.
  - **Network Security:** Includes `network_security_config.xml` to allow cleartext HTTP to local IPs.

---

## Communication Protocol

The Android app and NodeMCU communicate over **HTTP GET** requests. The NodeMCU listens on port 80 and handles the following endpoints:

| Endpoint                         | Description                                  |
|----------------------------------|----------------------------------------------|
| `/on`                            | Turns the fan ON                             |
| `/off`                           | Turns the fan OFF                            |
| `/setofftime?hour=14&minute=30` | Sets auto-off time to 2:30 PM                |
| `/cancel`                        | Cancels any active auto-off schedule         |

Each request returns a plain text response like `"Fan ON"` or `"Schedule Set"`.

---

## User Experience & Benefits

- **Convenience:** Wireless control from anywhere on the same network.
- **Energy Efficiency:** Prevents unnecessary fan usage via scheduled shut-off.
- **Smart Wake-Up Feature:** Scheduling integrates with your morning alarm — the fan turning off can act as a subtle nudge to get out of bed.
- **Adaptability:** Easily change the NodeMCU IP address in the app without needing code changes.
- **Simple UI:** Designed for clarity and ease of use, even for beginners.

---

## Technical Considerations

- **Wi-Fi Requirement:** Both the Android device and NodeMCU must be connected to the same local Wi-Fi network (hotspot or router).
- **Internet Required for Time Sync:** NTP time sync requires a working internet connection.
- **Power Continuity:** The NodeMCU must remain powered and connected for schedules to work. Auto-reconnect logic is implemented for network or power interruptions.
- **Voltage Compatibility:** When using 5V relay modules with a 3.3V NodeMCU, a **logic level shifter** is essential for safe and consistent triggering.

---

> ✨ This project is ideal for hobbyists, students, and DIYers looking to add basic automation to their home with minimal components and maximum control.
