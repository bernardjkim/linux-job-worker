package ljworker.client;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.NettyChannelBuilder;
import ljworker.LinuxJobServiceGrpc;
import ljworker.StopRequest;


/**
 * gRPC client interface. This client interface allows a user to send
 * start/stop/status requests to a connected LinuxJobWorker.
 */
public class GrpcClient {
    private static final Logger logger = Logger.getLogger(GrpcClient.class.getName());
    private static final String HOST = "localhost";
    private static final int PORT = 8443;

    private ManagedChannel channel;
    private LinuxJobServiceGrpc.LinuxJobServiceBlockingStub blockingStub;

    public GrpcClient() {}

    /** Initialize gRPC connection. */
    public void init() {
        // Initialize connection to ther server
        this.channel = NettyChannelBuilder.forAddress(HOST, PORT)
                // TODO: setup TLS/SSL encryption. RPCs sent as plain text for now.
                .usePlaintext()
                .build();
        blockingStub = LinuxJobServiceGrpc.newBlockingStub(channel);
    }

    /** Once the channel is closed new incoming RPCs will be cancelled. */
    public void close() throws InterruptedException {
        channel.shutdown()
                .awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * Send start request with provided args.
     * 
     * @param args Start RPC arguments
     */
    public void start(String[] args) {
        // TODO: start RPC
    }

    /**
     * Send stop request with provided args.
     * 
     * @param args Stop RPC arguments
     */
    public void stop(String[] args) {
        // create new Stop Request
        StopRequest.Builder builder = StopRequest.newBuilder();
        builder.setId(Integer.parseInt(args[1]));
        StopRequest request = builder.build();

        // send request
        try {
            blockingStub.stop(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus()
                    .getCode());
        }
    }

    /**
     * Send status request with provided args.
     * 
     * @param args Status RPC arguments
     */
    public void status(String[] args) {
        // TODO: status RPC
    }
}
