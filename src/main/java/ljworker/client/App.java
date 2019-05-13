package ljworker.client;

import java.util.Scanner;
import java.io.Console;

public class App {
    // TODO: these constants could be variables received from user args at runtime
    private static final String HOST = "localhost";
    private static final int PORT = 8443;

    // Command string constants
    private static final String START = "start";
    private static final String STOP = "stop";
    private static final String STATUS = "status";
    private static final String EXIT = "exit";

    /**
     * Prompt user login.
     * 
     * @param connection GrpcClient connection.
     * @return Access token.
     */
    public static String loginPrompt(GrpcClient connection) {
        Console console = System.console();
        String token = "";
        String username;
        String password;

        while ("".equals(token)) {
            System.out.println("Login to continue.");
            username = console.readLine("username: ");
            password = String.valueOf(console.readPassword("password: "));
            token = connection.authenticate(username, password);
        }

        System.out.println("Login successful!");
        System.out.println("-----------------");
        return token;
    }

    /** Print client commands to console. */
    public static void printUsage() {
        System.out.println("Usage:");
        System.out.printf("\t%s\t<linux cmd>\n", START);
        System.out.printf("\t%s\t<process id>\n", STOP);
        System.out.printf("\t%s\t<process id>\n", STATUS);
        System.out.printf("\t%s\n", EXIT);
    }

    /**
     * Prompt user input.
     * 
     * @param connection GrpcClient connection
     * @param token Acess token.
     */
    public static void cmdPrompt(GrpcClient connection, String token) {
        Scanner scanner = new Scanner(System.in);
        boolean receivedExitCmd = false;

        while (!receivedExitCmd) {
            System.out.print("> ");
            String type = scanner.next();
            String input = scanner.nextLine()
                    .trim();

            if (START.equals(type)) {
                connection.start(input);
            } else if (STOP.equals(type)) {
                connection.stop(input);
            } else if (STATUS.equals(type)) {
                connection.status(input);
            } else if (EXIT.equals(type)) {
                receivedExitCmd = true;
                System.out.println("exiting linux job worker client...");
            } else {
                System.out.printf("invalid RPC type: %s\n", type);
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
        GrpcClient connection = new GrpcClient(HOST, PORT);
        String token;

        token = loginPrompt(connection);
        printUsage();
        cmdPrompt(connection, token);
        connection.close();
    }
}
