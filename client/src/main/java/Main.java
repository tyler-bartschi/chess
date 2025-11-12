import chess.*;
import ui.UI;

public class Main {
    public static void main(String[] args) {
        int serverPort = 8080;
        if (args.length >= 1) {
            serverPort = Integer.parseInt(args[0]);
        }

        try {
            new UI(serverPort).run();
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}