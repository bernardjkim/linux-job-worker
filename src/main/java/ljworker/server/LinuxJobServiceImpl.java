package ljworker.server;

import java.util.Observer;
import java.util.Observable;
import java.util.List;
import com.google.protobuf.ProtocolStringList;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import ljworker.LinuxJobServiceGrpc.LinuxJobServiceImplBase;
import ljworker.util.ObservableList;
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
        // create new job with the provided args and enqueue the job
        ProtocolStringList args = req.getArgsList();
        Job job = new Job(args.toArray(new String[0]));
        jobManager.startJob(job);

        // build response
        StartResponse.Builder builder = StartResponse.newBuilder();

        // Add observer to the jobs logs. When a new entry is added to the logs
        // stream the response to the client.
        ObservableList logs = job.getLogs();
        logs.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object index) {
                if (o != logs) {
                    return;
                }

                try {
                    // index of -1 indicates that the job has completed
                    if ((int) index == -1) {
                        logs.deleteObserver(this);
                        responseObserver.onCompleted();
                    } else {
                        List<String> list = logs.getList();
                        String output = list.get((int) index);
                        StartResponse response = builder.setOutput(output)
                                .build();
                        responseObserver.onNext(response);
                    }
                } catch (StatusRuntimeException e) {
                    // if client closes channel, stop the job
                    job.stop();
                }
            }
        });
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
