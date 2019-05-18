package ljworker.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;
import io.grpc.Server;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;

/** gRPC server listening for client RPCs. */
public class GrpcServer {
    private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());
    private static final String HOST = "localhost";
    private static final int PORT = 8443;

    private final String certChainFilePath;
    private final String privateKeyFilePath;
    private final String trustCertCollectionFilePath;

    private Server server;

    public GrpcServer(String certChainFilePath, String privateKeyFilePath, String trustCertCollectionFilePath) {
        this.certChainFilePath = certChainFilePath;
        this.privateKeyFilePath = privateKeyFilePath;
        this.trustCertCollectionFilePath = trustCertCollectionFilePath;
    }

    /**
     * Initialize & start gRPC server
     * 
     * @throws IOException
     * @throws SSLException
     */
    public void start() throws SSLException, IOException {
        server = NettyServerBuilder.forAddress(new InetSocketAddress(HOST, PORT))
                .addService(new LinuxJobServiceImpl()) // add LinuxJobService
                .sslContext(getSslContextBuilder().build()) // set ssl context for encryption
                .build()
                .start();
        logger.info("Server started, listening on " + PORT);
        Runtime.getRuntime()
                // registered shutdown hook will be run when the JVM begins shutdown
                .addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        // Use stderr here since the logger may have been reset by its JVM shutdown
                        // hook.
                        System.err.println("*** shutting down gRPC server since JVM is shutting down");
                        GrpcServer.this.stop();
                        System.err.println("*** server shut down");
                    }
                });
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon
     * threads.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Initiates shutdown. Preexisting calls will continue, but new calls will be
     * rejected.
     */
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Initialize the SSL context to use for encryption.
     * REF:https://github.com/grpc/grpc-java/tree/master/examples/example-tls
     * 
     * @return SSL context builder.
     */
    private SslContextBuilder getSslContextBuilder() {
        File keyCertChainFile = new File(certChainFilePath);
        File keyFile = new File(privateKeyFilePath);
        File trustCertCollectionFile = new File(trustCertCollectionFilePath);

        SslContextBuilder sslClientContextBuilder = SslContextBuilder.forServer(keyCertChainFile, keyFile);
        sslClientContextBuilder.trustManager(trustCertCollectionFile);
        sslClientContextBuilder.clientAuth(ClientAuth.REQUIRE);
        return GrpcSslContexts.configure(sslClientContextBuilder, SslProvider.OPENSSL);
    }


}
