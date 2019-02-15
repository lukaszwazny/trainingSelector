#include <Keypad.h>


const byte rows = 2;
const byte cols = 2;
String expectedMessage = "PC";
String answer = "nano";
const int pin = 8;

char keys[rows][cols] = {
  {'1', '2'},
  {'3', '4'}
};

byte rowPins[rows] = {2,3};
byte colPins[cols] = {4,5};

Keypad keypad = Keypad (makeKeymap(keys), rowPins, colPins, rows, cols);


void setup() {
  pinMode(pin, OUTPUT);
  digitalWrite(pin, LOW);
 Serial.begin(9600);
 Serial.setTimeout(50);
 digitalWrite(pin,HIGH);

}

void loop() {
  
  String text = Serial.readString();
  if(text == expectedMessage){
    digitalWrite(pin, LOW);
    Serial.println(answer);
  }

  
  char key = keypad.getKey();
  if(key != NO_KEY){
    Serial.print(key);
    digitalWrite(pin, HIGH);
    delay(150);
    digitalWrite(pin, LOW);
  }

}
