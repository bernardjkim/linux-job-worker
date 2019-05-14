package ljworker.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import io.grpc.Server;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;

/**
 * gRPC server listening for client RPCs.
 */
public class GrpcServer {
    // TODO: these constants could be variables received from user args at runtime
    private static final String HOST = "localhost";
    private static final int PORT = 8443;

    // TODO: if service is open to public might consider using a trusted CA.
    // for now we are just using self signed certificates.
    private static File keyCertChainFile = new File("sslcert/server.crt");
    private static File keyFile = new File("sslcert/server.pem");
    private static File trustCertCollectionFile = new File("sslcert/ca.crt");

    public static void main(String[] args) throws IOException, InterruptedException {

        // Initialize the SSL context to use for encryption
        // REF: https://github.com/grpc/grpc-java/tree/master/examples/example-tls
        SslContextBuilder builder = SslContextBuilder.forServer(keyCertChainFile, keyFile);
        builder.trustManager(trustCertCollectionFile);
        builder.clientAuth(ClientAuth.REQUIRE);
        SslContext sslContext = GrpcSslContexts.configure(builder, SslProvider.OPENSSL)
                .build();

        // Initialize gRPC server listening on HOST:PORT
        Server server = NettyServerBuilder.forAddress(new InetSocketAddress(HOST, PORT))
                .sslContext(sslContext)
                .addService(new LinuxJobServiceImpl())
                .build();

        server.start();
        System.out.printf("Server started at %s:%d\n", HOST, PORT);
        server.awaitTermination();
    }
}
