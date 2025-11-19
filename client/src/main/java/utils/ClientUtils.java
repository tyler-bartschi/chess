package utils;

import static ui.EscapeSequences.*;

public class ClientUtils {

    public static void resetTextEffects() {
        System.out.print(RESET_TEXT_BOLD_FAINT + RESET_TEXT_UNDERLINE + RESET_TEXT_ITALIC + RESET_TEXT_BLINKING + RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    public static void printSuccessMessage(String message) {
        System.out.println(SET_TEXT_COLOR_GREEN + message + "\n");
    }

    public static void printBlueAndWhite(String first, String second) {
        System.out.println(EMPTY + SET_TEXT_COLOR_BLUE + first + SET_TEXT_COLOR_WHITE + second);
    }

    public static String[] makeArrayLower(String[] tokens) {
        String[] newTokens = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            newTokens[i] = tokens[i].toLowerCase();
        }
        return newTokens;
    }

}
