package app;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ConnectionStatus {
    protected PropertyChangeSupport propertyChangeSupport;
    private boolean portFound;

    public ConnectionStatus () {
        this.portFound = false;
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void setPortFound(boolean value) {
        boolean oldValue = this.portFound;
        this.portFound = value;
        propertyChangeSupport.firePropertyChange("portFoundProperty",oldValue, portFound);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
