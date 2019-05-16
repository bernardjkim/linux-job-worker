package ljworker.util;

import java.util.List;
import java.util.Observable;

/**
 * ObservableList allows an Observer to subscribe to changes in this list. The
 * main use case is to help handle streaming a Job's logs while still running.
 */
public class ObservableList extends Observable {
    private List<String> list;

    public ObservableList(List<String> list) {
        super();
        this.list = list;
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
        notifyObservers(list.size() - 1);
    }

    /**
     * End will notify observers with an index of -1. This will be used to signal
     * that the list is done reading output/errors.
     */
    public void end() {
        setChanged();
        notifyObservers(-1);
    }

    public List<String> getList() {
        return list;
    }

}
