package ljworker.server;

import io.grpc.stub.StreamObserver;
import ljworker.LinuxJobServiceGrpc.LinuxJobServiceImplBase;
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
        // TODO: handle start RPC
    }

    @Override
    public void startStream(StartRequest req, StreamObserver<StartResponse> responseObserver) {
        // TODO: handle start stream RPC
    }

    @Override
    public void stop(StopRequest req, StreamObserver<StopResponse> responseObserver) {
        // build response
        StopResponse.Builder builder = StopResponse.newBuilder();

        // stop job with the matching id
        int id = req.getId();
        Job job = jobManager.getJob(id);
        if (job != null) {
            job.stop();
            builder.setSuccess(true);
        } else {
            builder.setSuccess(false);
        }

        StopResponse response = builder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
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
