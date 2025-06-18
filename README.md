# **Wi-Fi Enabled Smart Fan Controller with Android Application**

## **Project Overview**

This project presents a comprehensive solution for intelligent fan control, leveraging the power of a **NodeMCU (ESP8266)** microcontroller as a compact web server, and an intuitive **Android application** for user interaction. The system enables users to remotely control an electric fan, including automatic shut-off scheduling, over a **local Wi-Fi network**, enhancing **convenience** and **energy efficiency**.

---

## **Key Components**

### **1. NodeMCU (ESP8266) Microcontroller**
- **Role:** Embedded web server and fan controller.
- **Functionality:**
  - Connects to the local Wi-Fi network (e.g., mobile hotspot).
  - Hosts a simple HTTP server that responds to GET requests.
  - Controls the fan using a relay module or direct connection.
  - Uses **Network Time Protocol (NTP)** for accurate timekeeping.
  - Stores and manages the fan state (ON/OFF) and auto-off schedule in volatile memory.
  - Continuously monitors current time and executes auto-off at the specified schedule.

### **2. Android Application**
- **Platform:** Built using Java for Android OS.
- **User Interface:** Clean, user-friendly with intuitive controls.
- **Core Features:**
  - **Fan ON/OFF:** Sends HTTP GET requests (`/on`, `/off`) to toggle fan power.
  - **Automatic OFF Scheduling:**
    - Uses `TimePickerDialog` to select shutdown time.
    - Sends GET request `/setofftime?hour=HH&minute=MM` to NodeMCU.
    - Displays confirmation of scheduled time.
  - **Cancel Schedule:** Sends GET request to `/cancel` endpoint.
  - **Dynamic IP Configuration:**
    - Users can input and save NodeMCU IP using `SharedPreferences`.
    - App adapts to changes in IP across launches.
  - **Status Feedback:** Shows current fan state (ON, OFF, Scheduled, Error).
  - **Robust Network Handling:**
    - Uses `ExecutorService` for background HTTP tasks to prevent UI freezing.
    - Implements `try-catch` for error handling and displays user-friendly `Toast` messages.
  - **Network Security:**
    - Configures `network_security_config.xml` to allow HTTP traffic to local IPs.

### **Communication Protocol**
- **Type:** HTTP GET
- **Port:** 80
- **Endpoints:**
  - `/on` – Turn fan ON
  - `/off` – Turn fan OFF
  - `/setofftime?hour=HH&minute=MM` – Set auto-OFF schedule
  - `/cancel` – Cancel existing schedule
- **Responses:** Simple text confirmations (e.g., "Fan ON", "Schedule Set").

---

## **User Experience & Benefits**

- 🧠 **Convenience:** Control fan from anywhere on the Wi-Fi network.
- ⚡ **Energy Efficiency:** Prevent unnecessary power usage via scheduling.
- ⏰ **Smart Wake-Up Feature:** Uses the fan turning off to gently wake users — a modern twist on a classic parenting trick!
- 🔄 **Flexible IP Handling:** Update IP without modifying code.
- 🧭 **Simple UI:** Clean design for easy interaction.

---

## **Technical Considerations**

- 📶 **Local Network Dependency:** Both Android device and NodeMCU must be on the same Wi-Fi network.
- 🌐 **Time Synchronization:** Accurate scheduling requires reliable internet for NTP.
- 🔌 **Power Continuity:** NodeMCU must stay powered and connected; it auto-reconnects and re-syncs NTP on disconnection.

---

> 💡 *This project integrates hardware control, real-time scheduling, and mobile development into a simple yet effective smart home utility.*
> *Built with AI tools for code suggestions/debugging.*  
