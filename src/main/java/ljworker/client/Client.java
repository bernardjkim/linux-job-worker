package ljworker.client;

import java.util.logging.Logger;
import javax.net.ssl.SSLException;

public class Client {
    private static final Logger logger = Logger.getLogger(Client.class.getName());
    // Command string constants
    private static final String START = "start";
    private static final String STREAM = "stream";
    private static final String STOP = "stop";
    private static final String STATUS = "status";
    private static final String LIST = "list";

    /** Print client commands to console. */
    public static void printUsage() {
        System.out.println("Usage:");
        System.out.printf("\t%s\t<linux cmd>\n", START);
        System.out.printf("\t%s\t<linux cmd>\n", STREAM);
        System.out.printf("\t%s\t<process id>\n", STOP);
        System.out.printf("\t%s\t<process id>\n", STATUS);
        System.out.printf("\t%s\n", LIST);
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 1) {
            printUsage();
            return;
        }

        GrpcClient connection = new GrpcClient("sslcert/client.crt", "sslcert/client.pem", "sslcert/ca.crt");
        try {
            connection.init();
        } catch (SSLException e) {
            logger.info("Invalid SSL context");
            return;
        }
        if (START.equals(args[0])) {
            connection.start(args);
        } else if (STREAM.equals(args[0])) {
            connection.stream(args);
        } else if (STOP.equals(args[0])) {
            connection.stop(args);
        } else if (STATUS.equals(args[0])) {
            connection.status(args);
        } else if (LIST.equals(args[0])) {
            connection.list();
        } else {
            System.out.printf("invalid command: %s\n", args[0]);
            printUsage();
        }
        connection.close();
    }
}
