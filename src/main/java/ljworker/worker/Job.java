package ljworker.worker;

import java.util.ArrayList;
import java.util.List;

/**
 * A job is an instance of a linux job. It contains the command, status, and
 * logs of the job.
 */
public class Job implements Runnable {
    // Status constants
    static final String QUEUED = "QUEUED";
    static final String RUNNING = "RUNNING";
    static final String COMPLETED = "COMPLETED";
    static final String FAILED = "FAILED";

    private String[] args;
    private String status;
    private List<String> logs;

    public Job(String[] args) {
        this.args = args;
        this.status = QUEUED;
        this.logs = new ArrayList<>();
    }

    // Runnable interface requires run method. This method will be called when
    // the start() method is called on the thread.
    public void run() {
        // TODO: handle run
    }

    public String[] getArgs() {
        return this.args;
    }

    public String getStatus() {
        return this.status;
    }

    public List<String> getLogs() {
        return this.logs;
    }

    public void start() {
        // TODO: handle start
    }

    public void stop() {
        // TODO: handle stop
    }

}
