package ljworker.client;

import java.io.File;
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
