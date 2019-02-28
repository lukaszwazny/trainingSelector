package app;

import com.fazecast.jSerialComm.*;

import java.io.PrintWriter;

public class ArduinoCommunicator {

	private static final short baudRate = 9600;
    protected SerialPort port;
    private SerialPort tmpPort;
    private boolean portFound;
    private ArduinoDataReceiver buffer;


    ArduinoCommunicator(){

        this.portFound = false;
        this.port = null;
        this.buffer = null;

    }

    ArduinoCommunicator(ArduinoDataReceiver buffer){

        this.portFound = false;
        this.port = null;
        this.buffer = buffer;

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

        //SerialPort tmpPort = this.port;
        String[] availablePortNames = getPortNames();
        SerialPort[] tmpPorts = new SerialPort[availablePortNames.length];

        for(int i = 0; i < availablePortNames.length; i++){

            tmpPorts[i] = SerialPort.getCommPort(availablePortNames[i]);
        }

        ConnectionStatus status = new ConnectionStatus();
        ConnectionStatusListener statusListener = new ConnectionStatusListener(tmpPorts);
        status.addPropertyChangeListener(statusListener);

        for(int i = 0; i < availablePortNames.length; i++ ){
            SerialPort nextPort = tmpPorts[i];

            if(nextPort.openPort()) {
                nextPort.addDataListener(new SerialPortDataListener() {
                    @Override
                    public int getListeningEvents() {
                        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                    }

                    @Override
                    public void serialEvent(SerialPortEvent event) {

                        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                            return;

                        //read data from port
                        byte[] newData = new byte[nextPort.bytesAvailable()];
                        int numRead = (nextPort).readBytes(newData, newData.length);

                        if (numRead > 0) {

                            //parse the read data to int
                            String buff = new String(newData);
                            buff = buff.trim();//optional (buff may contain white spaces)

                            if (buff.equals("nano")) {
                                setPort(nextPort);
                                System.out.println("hurra:)");

                                int timeToSetUpArduino = 3000;
                                int timeToWaitForAnswer = 100;
                                String sendMessage = "PC";

                                //send an initializing message to the arduino
                                PrintWriter output = new PrintWriter(nextPort.getOutputStream());
                                output.print(sendMessage);
                                output.flush();
                                setPortFound(true);
                                status.setPortFound(true);
                                System.out.println("listeners removed");
                            } else {
                                System.out.println("No arduino on port:" + nextPort.getSystemPortName());
                            }

                            // System.out.println(number);
                        }
                    }

                });
            }

        }





        /*
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
                }catch (InterruptedException e){
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
        }*/

   }

   public void removeListeners(SerialPort[] ports){

        for(int i = 0; i < ports.length; i++){
            System.out.println("Shit");
            ports[i].removeDataListener();
        }
   }

    public void launchArduinoListener(){
        System.out.println(port.getSystemPortName());
        SerialPort tmpPort = this.port;
        tmpPort.removeDataListener();
        tmpPort.addDataListener(new SerialPortDataListener(){
            @Override
            public int getListeningEvents(){
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;

                //read data from port
                byte[] newData = new byte[tmpPort.bytesAvailable()];
                int numRead = (tmpPort).readBytes(newData, newData.length);

                if(numRead > 0) {

                    //parse the read data to int
                    String buff = new String(newData);
                    buff = buff.trim();//optional (buff may contain white spaces)
                    int number = Integer.parseInt(buff);

                    buffer.parseData(number);
                    System.out.println(number);
                }
            }

        });
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