package examples.expressions.parser;

import examples.expressions.parser.ast.Expression;
import examples.expressions.parser.ast.Literal;

import java.util.*;
import java.util.stream.Collectors;

import static examples.expressions.parser.TokenType.*;

public class ExpressionParser {


    public static final Set<TokenType> EXPRESSION_FIRST = Set.of(IDENTIFIER, NUMBER, LPAR, SUB);
    public static final Set<TokenType> EXPRESSION_FOLLOW = Set.of(RPAR, COMMA, EOF);

    public static final Set<TokenType> TERM_FIRST = EXPRESSION_FIRST;
    public static final Set<TokenType> TERM_FOLLOW = union(EXPRESSION_FOLLOW, ADD,  SUB);

    public static final Set<TokenType> FACTOR_FIRST = EXPRESSION_FIRST;
    public static final Set<TokenType> FACTOR_FOLLOW = union(TERM_FOLLOW, MUL, DIV);

    public static final Set<TokenType> ATOM_FIRST = Set.of(IDENTIFIER, NUMBER, LPAR);
    public static final Set<TokenType> ATOM_FOLLOW = FACTOR_FOLLOW;
    

    private static Set<TokenType> union(Set<TokenType> seed, TokenType... additional) {
        HashSet<TokenType> result = new HashSet<>();
        result.addAll(seed);
        result.addAll(Set.of(additional));
        return Set.copyOf(result);
    }

    
    ExpressionLexer lexer;
    Token lookahead;
    
    private void match(TokenType type) throws ParsingException {
        if(lookahead.type != type) {
            throw new ParsingException("Exoecting " + type + " got " + lookahead.type);
        }
        next();
    }
    private void next() throws ParsingException{
        lookahead = lexer.next();
    }
    
    private boolean lookaheadIn(Set<TokenType> types) {
        return types.contains(lookahead.type);
    }
    
    private boolean lookaheadIs(TokenType... types) {
        for(int i = 0; i < types.length; i++) {
            if(types[i] == lookahead.type)
                return true;
        }
        return false;
    }
    
    private ParsingException expected(Set<TokenType> expected, TokenType... additional) {

        HashSet<TokenType> toPrint = new HashSet<>(expected);
        toPrint.addAll(List.of(additional));

        return new ParsingException("Expecting any of " + toPrint.stream().map(TokenType::toString).collect(Collectors.joining(",")) + " got " + lookahead.type);
    }

    public Expression parse(String input) throws ParsingException {
        lexer = new ExpressionLexer(input);
        next();
        Expression expression = parseExpression();
        match(EOF);
        return expression;
    }

    private Expression parseExpression() throws ParsingException {
        if(lookaheadIn(EXPRESSION_FIRST)) {
            Expression expression = parseTerm();
            while(lookaheadIs(ADD, SUB)) {
                String operator = lookahead.lexeme;
                next();
                expression = new Expression(operator, expression, parseTerm());
            }
            if(lookaheadIn(EXPRESSION_FOLLOW)) {
                return expression;
            } else {
                throw expected(EXPRESSION_FOLLOW);
            }
        } else {
            throw expected(EXPRESSION_FIRST);
        }

    }

    private Expression parseTerm() throws ParsingException {
        if (lookaheadIn(TERM_FIRST)) {

            Expression term = parseFactor();

            while (lookaheadIs(MUL, DIV)) {
                String operator = lookahead.lexeme;
                next();
                Expression secondFactor = parseFactor();
                term = new Expression(operator, term, secondFactor);
            }

            if (lookaheadIn(TERM_FOLLOW)) {
                return term;
            } else {
                throw expected(TERM_FOLLOW);
            }
        } else {
            throw expected(TERM_FIRST);
        }

    }

    private Expression parseFactor() throws ParsingException {
        if (lookaheadIs(SUB)) {
            match(SUB);
            Expression atom = parseAtom();
            return new Expression("-", atom);
        } else if (lookaheadIn(ATOM_FIRST)) {
            return parseAtom();
        } else {
            throw expected(FACTOR_FIRST);
        }
    }

    private Expression parseAtom() throws ParsingException {
        if (lookaheadIs(NUMBER)) {
            Expression result = new Literal(lookahead.lexeme);
            next();
            return result;
        } else if (lookaheadIs(LPAR)) {
            next();
            Expression expression = parseExpression();
            match(RPAR);
            return expression;
        } else if (lookaheadIs(IDENTIFIER)) {

            String identifier = lookahead.lexeme;
            next();
            if(lookaheadIn(ATOM_FOLLOW)) {
                return new Expression(identifier);
            } else if (lookaheadIs(LPAR)) { //List of arguments
                next();
                ArrayList<Expression> arguments = new ArrayList<>();
                arguments.add(parseExpression());
                while (lookaheadIs(COMMA)) {
                    next();
                    arguments.add(parseExpression());
                }
                match(RPAR);
                return new Expression(identifier, arguments.toArray(Expression[]::new));
            }
            else
                throw expected(ATOM_FOLLOW, LPAR);
        }
        else{
            throw expected(ATOM_FIRST);
        }
    }

}
