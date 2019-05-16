package ljworker.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    static final String INTERRUPTED = "INTERRUPTED";

    // indicates thread status
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread worker;

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
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(args);

            // set status to RUNNING
            running.set(true);
            status = RUNNING;

            // any error message?
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR", logs);

            // any output?
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT", logs);

            // start reading output and error
            errorGobbler.start();
            outputGobbler.start();

            // wait for process to complete and set status to COMPLETED
            int exitVal = proc.waitFor();
            running.set(false);
            status = COMPLETED;
            logs.add("ExitValue: " + exitVal);
        } catch (IOException e) {
            status = FAILED;
        } catch (InterruptedException e) {
            Thread.currentThread()
                    .interrupt();
            status = INTERRUPTED;
        }
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
        worker = new Thread(this);
        worker.start();
    }

    public void stop() {
        running.set(false);
        worker.interrupt();
    }
}
