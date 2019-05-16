package ljworker.server;

import com.google.protobuf.ProtocolStringList;
import io.grpc.stub.StreamObserver;
import ljworker.LinuxJobServiceGrpc.LinuxJobServiceImplBase;
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
}
