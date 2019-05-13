package ljworker.client;

public class App {
    // TODO: these constants could be variables received from user args at runtime
    private static final String HOST = "localhost";
    private static final int PORT = 8443;

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

    public static void main(String[] args) {
        GrpcClient connection = new GrpcClient(HOST, PORT);
        // TODO: not exactly sure how to handle auth yet.
        // String token = "";

        if (args.length < 2) {
            printUsage();
        } else {
            connection.init();
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
}
