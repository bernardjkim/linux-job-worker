package ljworker.server;

import java.util.Observer;
import java.util.Observable;
import com.google.protobuf.ProtocolStringList;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import ljworker.LinuxJobServiceGrpc.LinuxJobServiceImplBase;
import ljworker.util.ObservableList;
import ljworker.HealthCheckRequest;
import ljworker.HealthCheckResponse;
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

        // Add observer to the job's logs. When a new entry is added to the logs
        // stream the output to the client.
        ObservableList logs = job.getLogs();
        logs.addObserver(new Observer() {

            @Override
            public void update(Observable o, Object args) {
                // check if log is still receiving new output
                if (!logs.isClosed()) {

                    // stream all available output
                    while (logs.hasNext()) {
                        String output = logs.next();
                        StartResponse response = builder.setOutput(output)
                                .build();

                        try {
                            responseObserver.onNext(response);
                        } catch (StatusRuntimeException e) {
                            // if RPC is cancelled, stop streaming logs
                            logs.deleteObserver(this);
                        }
                    }
                } else {
                    // remove observer if logs has no more output, this should
                    // be when the job has completed.
                    logs.deleteObserver(this);
                    responseObserver.onCompleted();
                }
            }
        });
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
    public void healthCheck(HealthCheckRequest req, StreamObserver<HealthCheckResponse> responseObserver) {
        HealthCheckResponse response = HealthCheckResponse.newBuilder()
                .setStatus("OK")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
