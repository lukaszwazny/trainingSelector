#include <Keypad.h>


const byte rows = 4;
const byte cols = 5;
String expectedMessage = "PC";
String answer = "nano";
const int pin = 13;


char keys[rows][cols] = {
  
  {1, 2, 3, 4, 5},
  {6, 7, 8, 9, 10},
  {11, 12, 13, 14, 15},
  {16, 17, 18, 19, 20}
  
};

byte rowPins[rows] = {2,3,4,5};
byte colPins[cols] = {6,7,8,9,10};

Keypad keypad = Keypad (makeKeymap(keys), rowPins, colPins, rows, cols);


void setup() {
 pinMode(pin, OUTPUT);
 digitalWrite(pin, HIGH);
 Serial.begin(9600);
 Serial.setTimeout(50);

}

void loop() {

  
  String text = Serial.readString();
  if(text == expectedMessage){
    Serial.println(answer);
    digitalWrite(pin, HIGH);
  }

  char key = keypad.getKey();
  if(key != NO_KEY){
    Serial.print(int(key)-1);
    digitalWrite(pin, LOW);
    delay(150);
    digitalWrite(pin, HIGH);
  }

}
