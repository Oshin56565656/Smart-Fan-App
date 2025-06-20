# Smart Fan/Light Control with NodeMCU

This project allows you to control a fan or light using a NodeMCU (ESP8266) via a web interface, including scheduling features.

## Hardware Setup

---

### Connecting the 5V Relay Module (Special Case: VCC, IN, GND Module)

**Problem:** If you are using a 5V relay module that **only** has `VCC`, `IN`, and `GND` pins (without a separate `JD-VCC` pin or jumper), and you're powering its `VCC` with 5V from the NodeMCU's `VIN` pin, you might find the relay is always ON or switches unreliably. This happens because the NodeMCU's output pins provide 3.3V, which is not a clear "HIGH" signal for a 5V-powered relay's input circuit, causing a voltage mismatch.

**Solution:** To fix this signal voltage mismatch and ensure reliable operation, you need a **Logic Level Shifter (LLS)**. This component will safely convert the NodeMCU's 3.3V control signal to a 5V signal that your relay module can correctly interpret.

#### Components Needed:
* NodeMCU (ESP8266 board)
* 5V Relay Module (with VCC, IN, GND pins only)
* **Bi-directional Logic Level Shifter Module** (e.g., commonly available modules based on BSS138 transistors)
* Fan or Light (Appliance to control)
* AC Mains Wires (use appropriate gauge and insulation for your appliance and local electrical standards)

#### Wiring Instructions:

**1. Safety First:** **ALWAYS cut power to the entire circuit, especially the mains voltage, before making ANY wiring changes. Verify no voltage is present with a multimeter or non-contact tester.**

**2. Powering the Logic Level Shifter:**
    * Connect NodeMCU's **`3V3`** pin (3.3V output) to the **`LV`** (Low Voltage) pin on the Logic Level Shifter.
    * Connect NodeMCU's **`VIN`** pin (5V output from USB/external supply) to the **`HV`** (High Voltage) pin on the Logic Level Shifter.
    * Connect NodeMCU's **`GND`** pin to one of the **`GND`** pins on the Logic Level Shifter (there's usually a shared GND on the LLS).

**3. Connecting NodeMCU to Logic Level Shifter:**
    * Connect NodeMCU's **`D1`** pin (your `FAN_RELAY_PIN`) to an **`LV1`** (or `LVx`, where 'x' is a channel number) pin on the Logic Level Shifter.

**4. Connecting Logic Level Shifter to Relay Module:**
    * Connect the corresponding **`HV1`** (or `HVx`) pin on the Logic Level Shifter to the Relay Module's **`IN`** pin.

**5. Powering the Relay Module:**
    * Connect NodeMCU's **`VIN`** pin (5V) to the Relay Module's **`VCC`** pin.
    * Connect NodeMCU's **`GND`** pin to the Relay Module's **`GND`** pin.

**6. Connecting Appliance (Fan/Light) to Relay:**
    * Connect one of the AC mains wires (typically the Live wire) to the Relay Module's **`COM`** (Common) terminal.
    * Connect the Relay Module's **`NO`** (Normally Open) terminal to one of the appliance's power wires (e.g., the Live wire of the fan/light).
    * Connect the other appliance power wire (e.g., Neutral) directly to the mains Neutral.

    *(**Important:** Make sure your relay's current rating (e.g., 5A, 10A) is sufficient for the appliance you are connecting.)*

#### Code Considerations:
* With the Logic Level Shifter correctly wired, your NodeMCU will provide a 3.3V signal to the LLS, which will convert it to a robust 5V signal for the relay.
* You should use the **standard active-LOW logic** for your relay in the code, which is the most common for these modules. This means:
    * To turn the fan/light **ON**: `digitalWrite(FAN_RELAY_PIN, LOW);`
    * To turn the fan/light **OFF**: `digitalWrite(FAN_RELAY_PIN, HIGH);`
* Ensure your `setFan` function is:
    ```cpp
    void setFan(bool state) {
      fanState = state;
      digitalWrite(FAN_RELAY_PIN, fanState ? LOW : HIGH); // If fanState is true (ON), set LOW (ON), else HIGH (OFF)
      Serial.print("Fan set to: ");
      Serial.println(fanState ? "ON" : "OFF");
    }
    ```
    And your `setup()` function includes:
    ```cpp
    void setup() {
      // ...
      pinMode(FAN_RELAY_PIN, OUTPUT);
      digitalWrite(FAN_RELAY_PIN, HIGH); // Ensure relay is OFF initially (active-LOW)
      // ...
    }
    ```

---

## Software Configuration

*(Add your existing sections for WiFi setup, web server endpoints, NTP client setup, etc., here.)*
