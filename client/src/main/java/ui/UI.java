package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class UI {

    private authState state;
    private final String port;

    private enum authState {
        UNAUTHENTICATED,
        AUTHENTICATED
    }

    public UI(String port) {
        state = authState.UNAUTHENTICATED;
        this.port = port;
    }

    public void run() {
        System.out.println(WHITE_KING + " Welcome to chess! Type Help to get started. " + BLACK_KING);

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (result != "quit") {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = evaluate(line);
                resetTextEffects();
            } catch (InputException ex) {
                printErrorMessage(ex.getMessage());
            } catch (Throwable ex) {
                printErrorMessage("An unidentified error occurred. Please try again.");
            }
        }
    }

    private void printPrompt() {
        resetTextEffects();
        if (state == authState.AUTHENTICATED) {
            System.out.print(SET_TEXT_BOLD + "[LOGGED_OUT] " + RESET_TEXT_BOLD_FAINT + ">>> ");
        } else {
            System.out.print(SET_TEXT_BOLD + "[LOGGED_IN] " + RESET_TEXT_BOLD_FAINT + ">>> ");
        }
    }

    private void resetTextEffects() {
        System.out.print(RESET_TEXT_BOLD_FAINT + RESET_TEXT_UNDERLINE + RESET_TEXT_ITALIC + RESET_TEXT_BLINKING + RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    private String evaluate(String line) throws InputException, Exception{
        resetTextEffects();
        return "";
    }

    private void printErrorMessage(String msg) {
        resetTextEffects();
        System.out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_RED + msg);
    }

}
