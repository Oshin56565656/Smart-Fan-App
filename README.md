# 🌬️ Wi-Fi Enabled Smart Fan Controller with Android App

## Overview

Ever wished you could turn off your fan without leaving your bed? Or have it shut off automatically after you fall asleep? This project makes that a reality.

We’ve built a **smart fan controller** using a **NodeMCU (ESP8266)** and a custom **Android app**. It lets you control your fan over Wi-Fi — turn it on/off, schedule an auto shut-off, and even get status updates — all from your phone.

It’s a simple, affordable smart home upgrade that blends comfort with energy savings.

---

## 🔧 How It Works

### 1. NodeMCU (ESP8266) – The Brain
The NodeMCU acts as a tiny web server and the main controller for your fan.

- Connects to your home Wi-Fi or phone hotspot.
- Hosts a basic HTTP server that listens for commands.
- Controls the fan via a relay module (or directly, depending on your setup).
- Syncs the current time from the internet using **NTP** (Network Time Protocol).
- Keeps track of fan state and the scheduled shut-off time.
- Automatically turns the fan off when the time comes.

### 2. Android App – The Remote
A simple and intuitive app built with Java that puts fan control in your pocket.

- **One-Tap Control:** Instantly turn the fan ON or OFF using buttons.
- **Set Auto-Off:** Pick a time with a friendly time picker — the app sends it to the NodeMCU.
- **Cancel Schedule:** Don’t need it anymore? Just cancel it with a tap.
- **See What’s Happening:** The app shows current fan status — ON, OFF, Scheduled, or Error.
- **Handles Wi-Fi Changes:** You can enter and save the NodeMCU’s IP address, so even if it changes, the app still works.
- **Stable & Smooth:** Runs HTTP requests in the background using `ExecutorService` so the UI never lags.
- **Clear Feedback:** Toast messages keep you informed of what’s happening — success or error.

> ⚠️ Note: Since the app talks to a local IP over HTTP, it includes a `network_security_config.xml` to allow this communication on newer Android versions.

---

## 🔁 How It All Communicates

It’s all done with good old HTTP GET requests. The NodeMCU listens on port 80 and handles simple URL paths:

- `/on` → Turns the fan ON  
- `/off` → Turns it OFF  
- `/setofftime?hour=14&minute=30` → Schedules the fan to turn off at 2:30 PM  
- `/cancel` → Cancels the scheduled shut-off

Each request gets a clear text reply like `"Fan ON"` or `"Schedule Set"`.

---

## ☀️ Why Use It?

- **Comfort:** Control your fan from anywhere in your house.
- **Energy Savings:** No more fans running all night long.
- **Wake-Up Hack:** Combine with your alarm — the sudden stop of cool air is surprisingly good at getting you out of bed (a trick many parents know well!).
- **Flexible & Adaptable:** Works even if your Wi-Fi IP changes — just update it in the app.
- **Simple Yet Smart:** No clutter, no confusion. Just tap and relax.

---

## ⚙️ Technical Notes

- **Local Wi-Fi Required:** Your phone and the NodeMCU need to be on the same network.
- **Needs Internet for Time Syncing:** The NodeMCU fetches accurate time via NTP, so a stable connection helps.
- **Stay Powered:** The NodeMCU must remain powered and connected for scheduled events to work. If it disconnects, it tries to reconnect and re-sync automatically.

---

> Made for students, tinkerers, or anyone who just wants their fan to be a little smarter — without spending a fortune.

