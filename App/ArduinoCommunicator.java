package trainingSelector;
import com.fazecast.jSerialComm.*;

import java.io.PrintWriter;
import java.util.Scanner;

public class ArduinoCommunicator {

    private static final short baudRate = 9600;
    private SerialPort port;
    private boolean portFound;


    ArduinoCommunicator(){

        this.portFound = false;
        this.port = null;

    }

    private static String[] getPortNames(){
        SerialPort[] ports = SerialPort.getCommPorts();
        String[] result = new String[ports.length];
        for(int i = 0; i < ports.length; i++)
            result[i] = ports[i].getSystemPortName();
        /*
        for (String s : result)
            System.out.println(s);
        */

        return result;
    }

    public void initializeArduino(){

        String[] availablePortNames = getPortNames();
        SerialPort port;


        for(int i = 0; i < availablePortNames.length; i++){

            port = SerialPort.getCommPort(availablePortNames[i]);
            if(port.openPort()){
                System.out.println("Scanning port: " + availablePortNames[i]);
                int timeToSetUpArduino = 3000;
                int timeToWaitForAnswer = 100;
                String sendMessage = "PC";
                String expectedAnswer = "nano";

                //give the uC time to initialize
                try {
                    Thread.sleep(timeToWaitForAnswer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //set some properties I have no idea what they are for
                port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

                //again, time for uC to initialize
                try {
                    Thread.sleep(timeToSetUpArduino);
                }catch (java.lang.InterruptedException e){
                    e.printStackTrace();
                }

                //send an initializing message to the arduino
                PrintWriter output = new PrintWriter(port.getOutputStream());
                output.print(sendMessage);
                output.flush();

                //give the arduino time to receive the message and answer
                try {
                    Thread.sleep(timeToWaitForAnswer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //receive data from arduino
                String buffer = "";
                Scanner arduinoInput = new Scanner(port.getInputStream());

                try {
                    if (port.getInputStream().available() > 0) {
                        if (arduinoInput.hasNext()) {
                            buffer = arduinoInput.nextLine();
                        }

                        if (buffer.equals(expectedAnswer)) {
                            this.portFound = true;
                            this.port = port;
                            System.out.println("Detected Arduino Board on port: "+availablePortNames[i]);

                            return;
                        }
                    }
                }catch (java.io.IOException e){

                }

            }
        }

   }

    public boolean isArduinoInitialized(){

        return isPortFound() && port != null;

    }

    public boolean isPortFound() {
        return portFound;
    }

    public void setPortFound(boolean portFound) {
        this.portFound = portFound;
    }

    public SerialPort getPort() {
        return port;
    }

    public void setPort(SerialPort port) {
        this.port = port;
    }

}
