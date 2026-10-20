package shell;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {

    private CommandParser() {
    }
    public static List<String> parse(String line) throws CommandException {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        boolean hasToken = false;
        for (char symbol : line.toCharArray()) {
            if (symbol == '"') {
                inQuotes = !inQuotes;
                hasToken = true;
            } else if (Character.isWhitespace(symbol) && !inQuotes) {
                if (hasToken) {
                    tokens.add(current.toString());
                    current.setLength(0);
                    hasToken = false;
                }
            } else {
                current.append(symbol);
                hasToken = true;
            }
        }
        if (inQuotes) {
            throw new CommandException("parse error: unclosed quote");
        }
        if (hasToken) {
            tokens.add(current.toString());
        }
        return tokens;
    }
}