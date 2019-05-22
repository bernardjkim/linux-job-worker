package ljworker.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * StreamGobbler runs in a separate thread. It takes a InputStreamReader and
 * stores the input to a provided log.
 * 
 * REF:https://www.javaworld.com/article/2071275/when-runtime-exec---won-t.html
 */
public class StreamGobbler extends Thread {
    private static final Logger logger = Logger.getLogger(StreamGobbler.class.getName());

    private InputStream is;
    private String type; // ERROR/OUTPUT
    private List<String> logs;

    public StreamGobbler(InputStream is, String type, List<String> logs) {
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
                logs.add("[" + type + "] " + line);
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "IOException in StreamGobbler");
        }
    }
}
