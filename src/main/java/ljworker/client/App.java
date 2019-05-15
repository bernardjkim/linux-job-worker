package ljworker.client;

import java.util.logging.Logger;
import javax.net.ssl.SSLException;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());
    // Command string constants
    private static final String START = "start";
    private static final String STOP = "stop";
    private static final String STATUS = "status";

    /** Print client commands to console. */
    public static void printUsage() {
        System.out.println("Usage:");
        System.out.printf("\t%s\t<linux cmd>\n", START);
        System.out.printf("\t%s\t<process id>\n", STOP);
        System.out.printf("\t%s\t<process id>\n", STATUS);
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 2) {
            printUsage();
            return;
        }

        GrpcClient connection = new GrpcClient("sslcert/client.crt", "sslcert/client.pem", "sslcert/ca.crt");
        try {
            connection.init();
        } catch (SSLException e1) {
            logger.info("Invalid SSL context");
            return;
        }

        if (START.equals(args[0])) {
            connection.start(args);
        } else if (STOP.equals(args[0])) {
            connection.stop(args);
        } else if (STATUS.equals(args[0])) {
            connection.status(args);
        } else {
            System.out.printf("invalid command: %s\n", args[0]);
        }
        connection.close();
    }
}
