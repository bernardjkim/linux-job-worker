package ljworker.client;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import ljworker.HealthCheckRequest;
import ljworker.HealthCheckResponse;
import ljworker.LinuxJobServiceGrpc;
import ljworker.ListRequest;
import ljworker.ListResponse;
import ljworker.StartRequest;
import ljworker.StartResponse;
import ljworker.ListResponse.JobData;
import java.util.logging.Level;


/**
 * gRPC client interface. This client interface allows a user to send
 * start/stop/status requests to a connected LinuxJobWorker.
 */
public class GrpcClient {
    private static final Logger logger = Logger.getLogger(GrpcClient.class.getName());
    private static final String HOST = "localhost";
    private static final int PORT = 8443;

    private final String certChainFilePath;
    private final String privateKeyFilePath;
    private final String trustCertCollectionFilePath;

    private ManagedChannel channel;
    private LinuxJobServiceGrpc.LinuxJobServiceBlockingStub blockingStub;

    public GrpcClient(String certChainFilePath, String privateKeyFilePath, String trustCertCollectionFilePath) {
        this.certChainFilePath = certChainFilePath;
        this.privateKeyFilePath = privateKeyFilePath;
        this.trustCertCollectionFilePath = trustCertCollectionFilePath;
    }

    /** Initialize gRPC connection. */
    public void init() throws SSLException {
        // Initialize connection to ther server
        this.channel = NettyChannelBuilder.forAddress(HOST, PORT)
                .negotiationType(NegotiationType.TLS)
                .sslContext(getSslContext()) // SSL context for encryption
                .build();

        // Initialize blocking stub
        blockingStub = LinuxJobServiceGrpc.newBlockingStub(channel);

        // registered shutdown hook will be run when the JVM begins shutdown
        Runtime.getRuntime()
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
        try {
            // NOTE: need to await termination o.w. might not properly close channel
            channel.shutdown()
                    .awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
        try {
            Iterator<StartResponse> response = blockingStub.startStream(request);
            while (response.hasNext()) {
                for (String output : response.next()
                        .getOutputList()) {
                    System.out.println(output);
                }
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

    /** Send list request */
    public void list() {
        // create new List Request
        ListRequest.Builder builder = ListRequest.newBuilder();
        ListRequest request = builder.build();

        // send request
        try {
            ListResponse response = blockingStub.list(request);
            System.out.printf("%s\t%-50s%s\n", "id", "args", "status");
            for (JobData jobData : response.getJobDataList()) {
                int id = jobData.getId();
                String args = jobData.getArgsList()
                        .toString();
                String status = jobData.getStatus();

                System.out.printf("%d\t%-50s%s\n", id, args, status);
            }
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus()
                    .getCode());
        }
    }

    /** Check if server available. Mainly just for testing purposes. */
    public HealthCheckResponse healthCheck() {
        HealthCheckRequest request = HealthCheckRequest.newBuilder()
                .build();
        HealthCheckResponse response;
        try {
            response = blockingStub.healthCheck(request);
            return response;
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus()
                    .getCode());
            return null;
        }
    }

    /**
     * Initialize the SSL context to use for encryption.
     * REF:https://github.com/grpc/grpc-java/tree/master/examples/example-tls
     * 
     * @return SSL context.
     * @throws SSLException
     */
    private SslContext getSslContext() throws SSLException {
        File keyCertChainFile = new File(certChainFilePath);
        File keyFile = new File(privateKeyFilePath);
        File trustCertCollectionFile = new File(trustCertCollectionFilePath);

        SslContextBuilder builder = GrpcSslContexts.forClient();
        builder.trustManager(trustCertCollectionFile);
        builder.keyManager(keyCertChainFile, keyFile);
        return builder.build();
    }
}
