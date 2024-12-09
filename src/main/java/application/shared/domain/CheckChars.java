package application.shared.domain;

import java.util.ArrayList;
import java.util.List;

public class CheckChars {

    public static boolean checkChars(String string) {
        List<String> illegalChars = new ArrayList<>();
        String[] illegalCharacters = {"\\", "<", ">", "*", "?", "/", "|", ":", "\""};
        illegalChars.addAll(List.of(illegalCharacters));
        boolean illegal = false;
        for (String s : illegalChars) {
            if (string.contains(s)) {
                illegal = true;
                break;
            }
        }
        return illegal;
    }
    public static int checkSpaceChars(String string) {
        int spaceCount = 0;
        for (int i = 0; i < string.length(); i++) {
            String c = String.valueOf(string.charAt(i));
            if (c.equalsIgnoreCase(" ")) {
                spaceCount = spaceCount+1;
            }
        }
        return spaceCount;
    }

    public static boolean checkPrexists(List<String> names, String name) {
        boolean preexists = false;
        for (String s : names) {
            if (s.equalsIgnoreCase(name)) {
                preexists = true;
                break;
            }
        }
        return preexists;
    }
}
