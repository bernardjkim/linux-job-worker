package ljworker.server;

import java.util.List;
import com.google.protobuf.ProtocolStringList;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import ljworker.LinuxJobServiceGrpc.LinuxJobServiceImplBase;
import ljworker.ListResponse.JobData;
import ljworker.HealthCheckRequest;
import ljworker.HealthCheckResponse;
import ljworker.ListRequest;
import ljworker.ListResponse;
import ljworker.StartRequest;
import ljworker.StartResponse;
import ljworker.StatusRequest;
import ljworker.StatusResponse;
import ljworker.StopRequest;
import ljworker.StopResponse;
import ljworker.worker.Job;
import ljworker.worker.JobManager;

/** LinuxJobService handles LinuxJobWorker RPCs. */
public class LinuxJobServiceImpl extends LinuxJobServiceImplBase {
    private JobManager jobManager;

    public LinuxJobServiceImpl() {
        this.jobManager = new JobManager();
    }

    @Override
    public void start(StartRequest req, StreamObserver<StartResponse> responseObserver) {
        // create new job with the provided args and enqueue the job
        ProtocolStringList args = req.getArgsList();
        Job job = new Job(args.toArray(new String[0]));
        jobManager.startJob(job);

        // build response
        StartResponse response = StartResponse.newBuilder()
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void startStream(StartRequest req, StreamObserver<StartResponse> responseObserver) {
        // create new job with the provided args
        ProtocolStringList args = req.getArgsList();
        Job job = new Job(args.toArray(new String[0]));

        // build response
        StartResponse.Builder builder = StartResponse.newBuilder();

        // Read logs from the running job every 100 ms and stream output to client.
        new Thread() {
            @Override
            public void run() {

                List<String> logs = job.getLogs();
                int index = 0;
                boolean running = true;
                while (running) {

                    // TODO: Streaming a process with a large amount of output will result in
                    // sending too many response messages and cause an error. Current solution
                    // is to gather the output and send a response every interval...
                    // An error may also occur if the response message is too large...
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread()
                                .interrupt();
                    }

                    // add all available output to response
                    while (index < logs.size()) {
                        String output = logs.get(index++);
                        if (output.equals("END OF LOGS")) {
                            running = false;
                        } else {
                            builder.addOutput(output);
                        }
                    }

                    try {
                        // send response and reset response builder
                        StartResponse response = builder.build();
                        responseObserver.onNext(response);
                        builder.clearOutput();
                    } catch (StatusRuntimeException e) {
                        // if channel is closed, stop streaming logs
                        return;
                    }
                }
                responseObserver.onCompleted();
            }
        }.start();

        jobManager.startJob(job);
    }

    @Override
    public void stop(StopRequest req, StreamObserver<StopResponse> responseObserver) {
        // TODO: handle stop RPC
    }

    @Override
    public void status(StatusRequest req, StreamObserver<StatusResponse> responseObserver) {
        // TODO: handle status RPC
    }

    @Override
    public void list(ListRequest req, StreamObserver<ListResponse> responseObserver) {
        ListResponse.Builder builder = ListResponse.newBuilder();
        JobData.Builder jobDataBuilder = JobData.newBuilder();

        // add all currently stored jobs to list response
        for (int id : jobManager.keySet()) {
            Job job = jobManager.getJob(id);
            jobDataBuilder.setId(id)
                    .setStatus(job.getStatus());
            for (String arg : job.getArgs()) {
                jobDataBuilder.addArgs(arg);
            }
            builder.addJobData(jobDataBuilder.build());
            jobDataBuilder.clear();
        }

        ListResponse response = builder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void healthCheck(HealthCheckRequest req, StreamObserver<HealthCheckResponse> responseObserver) {
        HealthCheckResponse response = HealthCheckResponse.newBuilder()
                .setStatus("OK")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
