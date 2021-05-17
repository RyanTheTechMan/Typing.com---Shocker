//Settings
const boolean ledMode = true;
const boolean inverted = true;

//Don't Touch Below
boolean shockedBefore = false;
int selectedOut = -0;
int shockTime = 500;
boolean waitingForShockTime = false;

void setup() {
  if (ledMode) selectedOut = LED_BUILTIN;
  else selectedOut = 13;

  Serial.begin(9600);
  while (!Serial) {
    ; // wait for serial port to connect.
  }
}

void loop() {
  if (Serial.available() > 0) {
    byte incomingByte = 0;
    incomingByte = Serial.read(); // read the incoming byte:
    if (incomingByte != -1) { // -1 means no data is available
      if (waitingForShockTime){
        waitingForShockTime = false;
        shockTime = incomingByte;
      }
      else if (incomingByte == 45) {
        if (!shockedBefore) {
          pinMode(selectedOut, OUTPUT);
          shockedBefore = true;
        }
        digitalWrite(selectedOut, inverted ? HIGH : LOW);
      }
      else if (incomingByte == 46) {
        if (!shockedBefore) {
          pinMode(selectedOut, OUTPUT);
          shockedBefore = true;
        }
        digitalWrite(selectedOut, inverted ? LOW : HIGH);
      }
      else if (incomingByte == 47) {
        if (!shockedBefore) {
          pinMode(selectedOut, OUTPUT);
          shockedBefore = true;
        }

        digitalWrite(selectedOut, inverted ? LOW : HIGH);
        delay(shockTime);
        digitalWrite(selectedOut, inverted ? HIGH : LOW);
      }
      else if (incomingByte == 40) {
        waitingForShockTime = true;
      }
    }
  }
}