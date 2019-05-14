package ljworker.client;

import java.io.File;
import javax.net.ssl.SSLException;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

/**
 * gRPC client interface. This client interface allows a user to send
 * start/stop/status requests to a connected LinuxJobWorker.
 */
class GrpcClient {
    // TODO: if service is open to public might consider using a trusted CA.
    // for now we are just using self signed certificates.
    private static File keyCertChainFile = new File("sslcert/client.crt");
    private static File keyFile = new File("sslcert/client.pem");
    private static File trustCertCollectionFile = new File("sslcert/ca.crt");

    private ManagedChannel channel;
    private String host;
    private int port;

    public GrpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /** Initialize gRPC connection. */
    public void init() throws SSLException {

        // Initialize the SSL context to use for encryption
        // REF: https://github.com/grpc/grpc-java/tree/master/examples/example-tls
        SslContextBuilder builder = GrpcSslContexts.forClient();
        builder.trustManager(trustCertCollectionFile);
        builder.keyManager(keyCertChainFile, keyFile);
        SslContext sslContext = builder.build();

        // Initialize connection to ther server
        this.channel = NettyChannelBuilder.forAddress(host, port)
                .negotiationType(NegotiationType.TLS)
                .sslContext(sslContext)
                .build();
    }

    /** Once the channel is closed new incoming RPCs will be cancelled. */
    public void close() {
        channel.shutdown();
    }

    /**
     * Send authenticate request with provided username & password.
     * 
     * @param username Client username
     * @param password Client password
     * @return An access token to authorize LinuxJobWorker RPCs.
     */
    public String authenticate(String username, String password) {
        // TODO: send authenticate rpc. return JWT token
        return "token";
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

}
