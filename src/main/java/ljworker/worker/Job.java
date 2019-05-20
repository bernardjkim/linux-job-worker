package ljworker.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import ljworker.util.StreamGobbler;

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

    private Thread worker;

    private String[] args;
    private String status;

    // TODO: Do we need to store the process output for a client to query at a
    // later time? Or do we just need to worry about streaming the output while
    // the process is running?
    private List<String> logs;

    public Job(String[] args) {
        this.args = args;
        this.status = QUEUED;
        this.logs = new ArrayList<>();
    }

    // Runnable interface requires run method. This method will be called when
    // the start() method is called on the thread.
    @Override
    public void run() {
        // create the process
        ProcessBuilder pb = new ProcessBuilder(args);
        Process proc = null;
        StreamGobbler errorGobbler = null;
        StreamGobbler outputGobbler = null;
        try {
            proc = pb.start();

            // set status to RUNNING
            status = RUNNING;

            errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR", logs);
            outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT", logs);

            // start reading output and error
            errorGobbler.start();
            outputGobbler.start();

            // wait for stream gobblers to complete
            errorGobbler.join();
            outputGobbler.join();

            // wait for process to complete and set status to COMPLETED
            int exitVal = proc.waitFor();
            status = COMPLETED;
            logs.add("ExitValue: " + exitVal);
        } catch (IOException e) {
            logs.add("[ERROR]\tIOException");
            status = FAILED;
        } catch (InterruptedException e) {
            // stop the running process
            if (null != proc) {
                proc.destroy();
            }

            logs.add("[INFO]\tInterrupt");
            status = INTERRUPTED;
            Thread.currentThread()
                    .interrupt();
        } finally {
            logs.add("END OF LOGS");
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

    /**
     * Stop the current job. Calling stop on a job that is not running will have no
     * effect.
     */
    public void stop() {
        worker.interrupt();
    }
}
