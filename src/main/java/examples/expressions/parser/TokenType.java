package examples.expressions.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TokenType {

    IDENTIFIER("[a-zA-Z][a-zA-Z0-9]*"),
    NUMBER("((\\d+(\\.\\d+)?)|(\\.\\d+))(e-?\\d+)?"),
    COMMA(","),
    LPAR("\\("),
    RPAR("\\)"),
    ADD("\\+"),
    SUB("-"),
    DIV("/"),
    MUL("\\*"),
    WS("\\s+"),
    EOF;

    private final Pattern pattern;

    TokenType() {
        this(null);
    }

    TokenType(String pattern) {
        this.pattern = (pattern != null) ? Pattern.compile("^(" + pattern + ")") : null;
    }

    public Matcher match(CharSequence input) {
        return (pattern != null)?pattern.matcher(input):null;
    }
}
