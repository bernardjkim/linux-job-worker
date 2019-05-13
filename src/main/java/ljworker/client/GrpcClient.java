package ljworker.client;

import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;

/**
 * gRPC client interface. This client interface allows a user to send
 * start/stop/status requests to a connected LinuxJobWorker.
 */
class GrpcClient {
    private ManagedChannel channel;
    private String host;
    private int port;

    public GrpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /** Initialize gRPC connection. */
    public void init() {
        this.channel = NettyChannelBuilder.forAddress(host, port)
                // TODO: setup TLS/SSL encryption. RPCs sent as plain text for now.
                .usePlaintext()
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
