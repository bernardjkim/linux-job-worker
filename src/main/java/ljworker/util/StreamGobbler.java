package ljworker.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * StreamGobbler runs in a separate thread. It takes a InputStreamReader and
 * stores the input to a provided log.
 * 
 * REF:https://www.javaworld.com/article/2071275/when-runtime-exec---won-t.html
 */
public class StreamGobbler extends Thread {
    InputStream is;
    String type; // ERROR/OUTPUT
    ObservableList logs;

    public StreamGobbler(InputStream is, String type, ObservableList logs) {
        this.is = is;
        this.type = type;
        this.logs = logs;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                logs.add("[" + type + "]\t" + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
