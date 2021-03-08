package examples.expressions.parser;

import java.util.regex.Matcher;

import static examples.expressions.parser.TokenType.*;


public class ExpressionLexer {


    private String input;
    private String original;
    private TokenType[] tokenTypes = {
            IDENTIFIER,
            NUMBER,
            COMMA,
            LPAR,
            RPAR,
            ADD,
            SUB,
            DIV,
            MUL
    };

    private static final Token END_OF_FILE = new Token(EOF, "");

    public ExpressionLexer(String input) {
        this.input = input;
        this.original = input;
    }

    public Token next() throws ParsingException {
        if(input.isBlank()) {
            return END_OF_FILE;
        }
        Matcher spaces = WS.match(input);
        if(spaces.find()) {
            input = spaces.replaceFirst("");
        }
        for(TokenType type : tokenTypes) {
            Matcher match = type.match(input);
            if(match.find()) {
                input = match.replaceFirst("");
                return new Token(type, match.group());
            }
        }
        throw new ParsingException("Unexpected char " + input.charAt(0) + " at position " + (original.length() - input.length()));
    }
}
