package ljworker.worker;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * JobManager will keep track of and manage the linux jobs. Provides methods to
 * start, stop, and query job.
 */
public class JobManager {
    // Maximum number of concurrently running jobs.
    // Ideal number of threads should be equal to number of processors.
    private static final int MAX_THREADS = 4;

    // TODO: instead of storing jobs and logs locally, we could store in remote
    // db or storage. This would allow the LinuxJobWorker to be more scablable
    // as we would be able to run multiple LinuxJobWorkers processing jobs while
    // all updating a central log.
    private Map<Integer, Job> jobs; // Mappings from process id to job.
    private AtomicInteger nextProcId;

    public JobManager() {
        this.jobs = new HashMap<Integer, Job>();
        this.nextProcId = new AtomicInteger();
    }

    /**
     * Get the job with the match id.
     * 
     * @param id Job id
     * @return Job with matching id, null if id is invalid
     */
    public Job getJob(int id) {
        return jobs.get(id);
    }

    /**
     * Get the keyset of job ids
     * 
     * @return
     */
    public Set<Integer> keySet() {
        return jobs.keySet();
    }

    /**
     * Add job to JobManager and start process.
     * 
     * @param job Job to execute
     */
    public void startJob(Job job) {
        // TODO: queue jobs to maintain max # of threads
        int id = nextProcId.incrementAndGet();
        jobs.put(id, job);
        job.start();
    }

    /**
     * Stop the job with the matching id
     * 
     * @param id Job id
     * @return true if valid job, false o.w.
     */
    public boolean stopJob(int id) {
        Job job = jobs.get(id);
        if (job != null) {
            job.stop();
            return true;
        } else {
            return false;
        }
    }
}
