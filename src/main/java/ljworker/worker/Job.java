package ljworker.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import ljworker.util.ObservableList;
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

    // indicates thread status
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread worker;

    private String[] args;
    private String status;
    private ObservableList logs;

    public Job(String[] args) {
        this.args = args;
        this.status = QUEUED;
        // this.logs = new ArrayList<>();
        this.logs = new ObservableList(new ArrayList<>());
    }

    // Runnable interface requires run method. This method will be called when
    // the start() method is called on the thread.
    @Override
    public void run() {
        try {
            // creating the process
            ProcessBuilder pb = new ProcessBuilder(args);
            Process proc = pb.start();


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

            // wait for stream gobblers to complete
            errorGobbler.join();
            outputGobbler.join();

            // wait for process to complete and set status to COMPLETED
            int exitVal = proc.waitFor();
            status = COMPLETED;
            logs.add("ExitValue: " + exitVal);
        } catch (IOException e) {
            logs.add("[ERROR]\tIOException");
            logs.add("ExitValue: " + 1);
            status = FAILED;
        } catch (InterruptedException e) {
            Thread.currentThread()
                    .interrupt();
            status = INTERRUPTED;
        } finally {
            logs.end();
            running.set(false);
        }
    }

    public String[] getArgs() {
        return this.args;
    }

    public String getStatus() {
        return this.status;
    }

    public ObservableList getLogs() {
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
