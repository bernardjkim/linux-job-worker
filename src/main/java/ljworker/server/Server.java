package ljworker.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) throws InterruptedException {

        GrpcServer server = new GrpcServer();
        try {
            server.start();
            server.blockUntilShutdown();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Unable to bind server. Port is already in use?");
        }
    }
}
