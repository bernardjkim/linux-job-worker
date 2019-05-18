package ljworker.util;

import java.util.List;
import java.util.Observable;

/**
 * ObservableList allows an Observer to subscribe to changes in this list. The
 * main use case is to help handle streaming a Job's logs while still running.
 * 
 * TODO: Observer/Observable pattern has been depricated in Java 9. Look into
 * PropertyChangeEvent and PropertyChangeListener
 */
public class ObservableList extends Observable {
    private List<String> list;
    private boolean closed; // if true, list is no longer expecting new strings
    private int index; // used to iterate through list

    public ObservableList(List<String> list) {
        super();
        this.list = list;
        this.closed = false;
        this.index = 0;
    }

    public List<String> getList() {
        return list;
    }

    /**
     * Checks to see if the list is still recieving new logs.
     * 
     * @return True if still recieving new logs, false otherwise.
     */
    public boolean hasNext() {
        return !closed;
    }

    /**
     * Get the next value.
     * 
     * @return The next log value.
     */
    public String getNext() {
        return list.get(index++);
    }

    /**
     * Add the provided String to the list. Notify observers with the max index of
     * the list.
     * 
     * @param str String to add to list
     */
    public void add(String str) {
        list.add(str);
        setChanged();
        notifyObservers();
    }

    /**
     * End will notify observers with an index of -1. This will be used to signal
     * that the list is done reading output/errors.
     */
    public void close() {
        setChanged();
        notifyObservers();
    }
}
