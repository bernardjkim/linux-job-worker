package ljworker.client;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.NettyChannelBuilder;
import ljworker.LinuxJobServiceGrpc;
import ljworker.StartRequest;
import ljworker.StartResponse;


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
        Runtime.getRuntime()
                // registered shutdown hook will be run when the JVM begins shutdown
                .addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        // Use stderr here since the logger may have been reset by its JVM shutdown
                        // hook.
                        System.err.println("*** shutting down gRPC client since JVM is shutting down");
                        GrpcClient.this.close();
                        System.err.println("*** client shut down");
                    }
                });
    }

    /** Once the channel is closed new incoming RPCs will be cancelled. */
    public void close() {
        channel.shutdown();

    }

    /**
     * Send start request with provided args.
     * 
     * @param args Start RPC arguments
     */
    public void start(String[] args) {
        // create new Start Request
        StartRequest.Builder builder = StartRequest.newBuilder();

        // start at index 1 to ignore 'start' token
        for (int index = 1; index < args.length; index++) {
            builder.addArgs(args[index]);
        }
        StartRequest request = builder.build();

        // send request
        try {
            blockingStub.start(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus()
                    .getCode());
        }
    }

    /**
     * Send start request with provided args. Stream output of process.
     * 
     * @param args Start RPC arguments
     */
    public void stream(String[] args) {
        // create new Start Request
        StartRequest.Builder builder = StartRequest.newBuilder();

        // start at index 1 to ignore 'start' token
        for (int index = 1; index < args.length; index++) {
            builder.addArgs(args[index]);
        }
        StartRequest request = builder.build();

        // send request
        Iterator<StartResponse> response;
        try {
            response = blockingStub.startStream(request);

            while (response.hasNext()) {
                System.out.println(response.next()
                        .getOutput());
            }
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus()
                    .getCode());
        }
    }

    /**
     * Send stop request with provided args.
     * 
     * @param args Stop RPC arguments
     */
    public void stop(String[] args) {
        // TODO: stop RPC
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
