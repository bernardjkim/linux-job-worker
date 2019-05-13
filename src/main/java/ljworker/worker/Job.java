package ljworker.worker;

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

    private String command;
    private String status;
    private String logs;

    public Job(String command) {
        this.command = command;
        this.status = QUEUED;
        this.logs = "";
    }

    // Runnable interface requires run method. This method will be called when
    // the start() method is called on the thread.
    public void run() {
        // TODO: handle run
    }

    public String getCommand() {
        return this.command;
    }

    public String getStatus() {
        return this.status;
    }

    public String getLogs() {
        return this.logs;
    }

    public void start() {
        // TODO: handle start
    }

    public void stop() {
        // TODO: handle stop
    }

}
