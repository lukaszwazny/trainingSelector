package trainingSelector;

import com.fazecast.jSerialComm.*;

public class CommunicationExample {

    public static void main(String args[]) {

        //creates an objcet representing the PC <-> Arduino communication interface
        ArduinoCommunicator comm = new ArduinoCommunicator();

        //find the arduino-port and open it
        comm.initializeArduino();

        //check if the arduino board has been initialized succesfully
        if(comm.isArduinoInitialized())
            System.out.println("The Arduino board has been initialized and waits for commands.");
        else {
            System.out.println("ERROR. No Arduino board has been initialized.");
            return;
        }

        //retrieve arduino port
        SerialPort port = comm.getPort();

        //port listener definition
        port.addDataListener(new SerialPortDataListener(){
            @Override
            public int getListeningEvents(){
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;

                //read data from port
                byte[] newData = new byte[port.bytesAvailable()];
                int numRead = port.readBytes(newData, newData.length);

                if(numRead > 0) {

                    //HERE -> Add function, that will update the content of the training-table
                    //... function ...

                    //display the read data
                    String buff = new String(newData);
                    buff = buff.trim();//optional (buff may contain white spaces)
                    int number = Integer.parseInt(buff);
                    System.out.println(number);
                }
            }

        });
    }
}
