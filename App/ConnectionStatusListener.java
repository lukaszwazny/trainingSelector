package app;

import com.fazecast.jSerialComm.SerialPort;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ConnectionStatusListener implements PropertyChangeListener {

    private SerialPort[] tmpPorts;

    public ConnectionStatusListener(SerialPort[] tmpPorts){
        this.tmpPorts = tmpPorts;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("portFoundProperty")) {
            if((Boolean)evt.getNewValue()){
                for(SerialPort port: tmpPorts){
                   
                    //WAZNE !!! nie wywolywac tej metody dla portu, na ktorym jest arduino. Koniecznie poprawic!!!
                    port.removeDataListener();
                    System.out.println("removed " + port.getSystemPortName());
                }
                System.out.println("hej");
            }
        }
    }
}
