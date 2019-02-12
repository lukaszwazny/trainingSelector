package test;
import arduino.*;

import java.util.Scanner;

import com.fazecast.jSerialComm.*;

public class Main {

	public static void main(String[] args) {
		
		
		SerialPort comPort;
		String portDescription = "COM3";
		int baudRate = 9600;
		
		//create port and set baud
		comPort = SerialPort.getCommPort(portDescription);
		comPort.setBaudRate(9600);
		
		//check if port has been properly opened
		if(comPort.openPort()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else {
			System.out.println("SHIT");
		}
		
		//set some properties I have no idea what they are for
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		
		//buffer
		String out = "";
		
		//scanner for the port content
		Scanner in = new Scanner(comPort.getInputStream());
		
		//endless reading-loop, need to end it somehow
		while(true) {
			if(in.hasNext()) {
				out = in.nextLine();
			}
			
			if(!out.equals("")) {
				System.out.println(out);
			}
		}
		
		//close scanner and port
		//in.close();
		//comPort.closePort();
		
	
	
	}

}
