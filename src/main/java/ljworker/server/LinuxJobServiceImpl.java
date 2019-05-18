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
