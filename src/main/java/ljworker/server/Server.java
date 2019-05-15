package ljworker.server;

import java.io.IOException;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) throws InterruptedException {

        GrpcServer server = new GrpcServer("sslcert/server.crt", "sslcert/server.pem", "sslcert/ca.crt");
        try {
            server.start();
            server.blockUntilShutdown();
        } catch (IOException e) {
            logger.info("Unable to bind server. Port is already in use?");
        }
    }
}
