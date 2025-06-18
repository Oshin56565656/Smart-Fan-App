
**Wi-Fi Enabled Smart Fan Controller with Android Application**

Project Overview: This project presents a comprehensive solution for intelligent fan control,
leveraging the power of a NodeMCU (ESP8266) microcontroller as a compact web server and an
intuitive Android application for user interaction. The system allows users to remotely control an
electric fan, including setting automatic shut-off schedules, over a local Wi-Fi network, enhancing
convenience and energy efficiency.

**Key Components**:

1. *NodeMCU (ESP8266) Microcontroller*:
   
     Role: Serves as the embedded web server and the primary control unit for the fan.
     Functionality:
     Establishes and maintains a connection to the local Wi-Fi network (e.g., a
      mobile hotspot).
     Hosts a simple HTTP web server that responds to specific GET requests from
      the Android application.
     Interfaces with a relay module (or directly controls a fan if suitable) to switch
      the fan's power supply ON or OFF.
     Utilizes the Network Time Protocol (NTP) to obtain accurate current time
      from the internet, crucial for precise scheduling.
     Manages the fan's state (ON/OFF) and stores the user-defined auto-off
    schedule in its volatile memory.
     Continuously monitors the current time against the set schedule and triggers
      the fan to turn off automatically at the specified moment.
   
2. *Android Application*:
   
     Platform: Developed using Java for the Android operating system.
     User Interface: Provides a clean and user-friendly interface with dedicated buttons
      for fan control and scheduling.
     Core Functionality:
     Fan ON/OFF: Sends immediate HTTP GET requests to the NodeMCU's /on
      and /off endpoints to directly toggle the fan's power.
     Automatic OFF Scheduling:
     Features an integrated TimePickerDialog for users to easily
      select a specific hour and minute for the fan to automatically turn off.
     Constructs and sends an HTTP GET request with hour and minute
      parameters (e.g., /setofftime?hour=14&minute=30) to the
      NodeMCU, which then stores and manages the schedule internally.
     Provides a visual confirmation of the set schedule on the app's UI.
     Cancel Schedule: Sends an HTTP GET request to a dedicated /cancel
      endpoint on the NodeMCU to clear any active auto-off schedules.
     Dynamic IP Address Configuration: Allows users to manually input and save
      the NodeMCU's current IP address within the app using
      SharedPreferences. This ensures the app can adapt if the NodeMCU's
      IP changes (e.g., after a router reset or power cycle). The saved IP persists
      across app launches.
     Status Feedback: Displays the current fan status (ON, OFF, Scheduled, Error)
      and the active schedule.
     Robust Network Handling: Utilizes Java's ExecutorService for
      background HTTP operations, preventing UI freezes and crashes.
     Error Handling: Implements try-catch blocks for network operations and
      provides user-friendly Toast messages for successful commands or
      connection errors.
      Network Security Configuration: Explicitly allows cleartext (HTTP) traffic to
      the NodeMCU's local IP address through
     network_security_config.xml, circumventing Android's default
      security restrictions for insecure connections.
   
    *Communication Protocol*: The communication between the Android app and the NodeMCU is
      based on standard HTTP GET requests. The NodeMCU's web server listens on port 80 and
      processes requests to specific URL paths (e.g., /on, /off, /setofftime, /cancel),
      responding with simple text confirmations.
   
    **User Experience & Benefits**:
   
     Convenience: Control the fan from anywhere within the Wi-Fi network, eliminating the need
      for physical interaction with the fan's switch.
     Energy Efficiency: Schedule the fan to turn off automatically, preventing unnecessary power
      consumption.
     Unique Wake-Up Feature: This scheduling function is designed to work in conjunction with
      a traditional phone alarm, providing a novel way to ease into the day. Inspired by a classic
      parenting technique, the sudden cessation of a fan's cooling breeze can be surprisingly
      effective at stirring you awake – much like a parent turning off the fan while gently (or not
      so gently!) urging you to rise. This clever method leverages a change in environmental
      comfort to aid your morning routine.
     Flexibility: Adapt to changes in NodeMCU's IP address without requiring app code
      modifications or redeployment.
     Simplicity: Intuitive user interface makes control and scheduling straightforward.
   
    **Technical Considerations**:
   
     Local Network Dependency: The system relies on both the Android device and the
      NodeMCU being connected to the same local Wi-Fi network (typically provided by a mobile
      hotspot or home router).
     Time Synchronization: Accurate scheduling heavily depends on the NodeMCU's ability to
      consistently synchronize its time via NTP, which requires a stable internet connection.
     Power Continuity: The NodeMCU must remain powered and connected to Wi-Fi for
      scheduled events to execute. Brief network outages are handled by automatic re-connection
      and NTP re-synchronization.
