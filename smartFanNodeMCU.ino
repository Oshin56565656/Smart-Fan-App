#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <NTPClient.h> 
#include <WiFiUdp.h>   


const char* ssid = "YOUR_WIFI_SSID";      
const char* password = "YOUR_WIFI_PASSWORD"; 


#define FAN_RELAY_PIN D1 

// --- Web Server Setup ---
ESP8266WebServer server(80); 

// --- Time Synchronization Setup ---
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 5.5 * 3600, 60000); 

// --- Fan State and Schedule Variables ---
bool fanState = false; 
unsigned long scheduledOffMillis = 0; 
int scheduledOffHour = -1; 
int scheduledOffMinute = -1; 

// --- Function to control the fan relay ---
void setFan(bool state) {
  fanState = state;
  digitalWrite(FAN_RELAY_PIN, fanState ? LOW : HIGH); 
  Serial.print("Fan set to: ");
  Serial.println(fanState ? "ON" : "OFF");
}

// --- Handler for "/" endpoint ---
void handleRoot() {
  server.send(200, "text/plain", "Smart Fan Control - NodeMCU Ready!");
}

// --- Handler for "/on" endpoint ---
void handleOn() {
  setFan(true); 
  scheduledOffMillis = 0; 
  scheduledOffHour = -1;
  scheduledOffMinute = -1;
  server.send(200, "text/plain", "Fan ON");
}

// --- Handler for "/off" endpoint ---
void handleOff() {
  setFan(false); 
  scheduledOffMillis = 0; 
  scheduledOffHour = -1;
  scheduledOffMinute = -1;
  server.send(200, "text/plain", "Fan OFF");
}

// --- Handler for "/setofftime" endpoint ---
void handleSetOffTime() {
  if (server.hasArg("hour") && server.hasArg("minute")) {
    int hour = server.arg("hour").toInt();
    int minute = server.arg("minute").toInt();

    if (hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59) {
      timeClient.update(); 
      unsigned long currentEpoch = timeClient.getEpochTime();
      int currentHour = timeClient.getHours();
      int currentMinute = timeClient.getMinutes();

      unsigned long targetEpoch;
      int dayOffset = 0;

      if (hour < currentHour || (hour == currentHour && minute <= currentMinute)) {
        dayOffset = 1; 
      }
      long currentTotalMinutes = currentHour * 60 + currentMinute;
      long targetTotalMinutes = hour * 60 + minute;
      long minutesUntilOff;

      if (targetTotalMinutes > currentTotalMinutes) {
          minutesUntilOff = targetTotalMinutes - currentTotalMinutes;
      } else { 
          minutesUntilOff = (24 * 60 - currentTotalMinutes) + targetTotalMinutes;
      }

      scheduledOffMillis = millis() + (minutesUntilOff * 60 * 1000); 
      scheduledOffHour = hour;
      scheduledOffMinute = minute;

      setFan(true); 
      server.send(200, "text/plain", "Schedule set for " + String(hour) + ":" + String(minute) + " (24hr)");
      Serial.print("Scheduled OFF at (24hr): ");
      Serial.print(hour);
      Serial.print(":");
      Serial.println(minute);
      Serial.print("Fan scheduled to turn OFF in (minutes): ");
      Serial.println(minutesUntilOff);

    } else {
      server.send(400, "text/plain", "Invalid hour or minute. Hour: 0-23, Minute: 0-59.");
    }
  } else {
    server.send(400, "text/plain", "Missing 'hour' or 'minute' parameters.");
  }
}


// --- Handler for "/cancel" endpoint ---
void handleCancel() {
  scheduledOffMillis = 0; 
  scheduledOffHour = -1;
  scheduledOffMinute = -1;
  setFan(false); 
  server.send(200, "text/plain", "Schedule cancelled. Fan OFF.");
  Serial.println("Schedule cancelled.");
}

// --- Handler for unknown endpoints (404 Not Found) ---
void handleNotFound() {
  server.send(404, "text/plain", "Not Found");
}


void setup() {
  Serial.begin(115200); 
  delay(10); 
  Serial.println("\nNodeMCU Wi-Fi Connection Test");

  pinMode(FAN_RELAY_PIN, OUTPUT); 
  setFan(false); 

  // Connect to Wi-Fi
  Serial.print("Connecting to '");
  Serial.print(ssid);
  Serial.println("'");
  WiFi.begin(ssid, password);

  int retries = 0;
  while (WiFi.status() != WL_CONNECTED && retries < 20) {
    delay(500);
    Serial.print(".");
    retries++;
  }

  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\nWiFi connected!");
    Serial.print("NodeMCU IP Address: ");
    Serial.println(WiFi.localIP()); 

    // Configure web server routes
    server.on("/", handleRoot);
    server.on("/on", handleOn);
    server.on("/off", handleOff);
    server.on("/setofftime", handleSetOffTime);
    server.on("/cancel", handleCancel);
    server.onNotFound(handleNotFound); 

    server.begin(); 
    Serial.println("HTTP server started");

    // Initialize NTP client to get time
    timeClient.begin();
    timeClient.update(); 
    Serial.println("NTP time synchronized.");
    Serial.print("Current time: ");
    Serial.println(timeClient.getFormattedTime());

  } else {
    Serial.println("\nWiFi connection failed!");
  }
}


void loop() {
  server.handleClient(); 


  timeClient.update();

 
  if (scheduledOffMillis > 0 && millis() >= scheduledOffMillis) {
    Serial.println("Scheduled OFF time reached!");
    setFan(false); 
    scheduledOffMillis = 0; 
    scheduledOffHour = -1;
    scheduledOffMinute = -1;
  }
}
