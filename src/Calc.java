import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

class Calc {
    public static void main(String[] args) {
        getResult();
    }

    private static void getResult() {
        Scanner scn = new Scanner(System.in);
        Lexer l = new Lexer(scn.nextLine());
        Parser p = new Parser(l.lex(), scn);
        try {
            System.out.println(p.parse());
        } catch (RuntimeException e) {
            System.out.println("error");
        }
    }
}

class Lexer {
    private final String string;

    public Lexer(String string) {
        this.string = string;
    }

    public ArrayList<Token> lex() {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<Token> tokenArrayList = new ArrayList<>();
        boolean digitFlag = false, identFlag = false;
        for (int i = 0; i < string.length(); i++) {
            Character c = string.charAt(i);
            String s = Character.toString(c);
            if (Character.isDigit(c)) {
                digitFlag = !identFlag;
                stringBuilder.append(c);
            } else if (Character.isAlphabetic(c)) {
                identFlag = true;
                digitFlag = false;
                stringBuilder.append(c);
            } else {
                if (identFlag) {
                    tokenArrayList.add(new Token(Token.TokenType.IDENT, stringBuilder.toString()));
                    stringBuilder.delete(0, stringBuilder.length());
                    identFlag = false;
                } else if (digitFlag) {
                    tokenArrayList.add(new Token(Token.TokenType.NUMBER, stringBuilder.toString()));
                    stringBuilder.delete(0, stringBuilder.length());
                    digitFlag = false;
                }
                if (s.matches("[+-/*()]")) {
                    Token.TokenType type = null;
                    switch (s) {
                        case "+":
                            type = Token.TokenType.PLUS;
                            break;
                        case "-":
                            type = Token.TokenType.MINUS;
                            break;
                        case "*":
                            type = Token.TokenType.MUL;
                            break;
                        case "/":
                            type = Token.TokenType.DIV;
                            break;
                        case "(":
                            type = Token.TokenType.OPENBRACKET;
                            break;
                        case ")":
                            type = Token.TokenType.CLOSEBRACKET;
                            break;
                    }
                    tokenArrayList.add(new Token(type, s));
                } else if (!Character.isWhitespace(c)) {
                    throw new RuntimeException("Lexical Error");
                }
            }
        }

        if (stringBuilder.length() != 0) {
            tokenArrayList.add(new Token(digitFlag ? Token.TokenType.NUMBER : Token.TokenType.IDENT, stringBuilder.toString()));
        }

        return tokenArrayList;
    }
}

class Parser {
    private final ArrayList<Token> tokens;
    private final HashMap<String, Integer> identMap;
    private final Scanner scanner;
    private int index;

    public Parser(ArrayList<Token> tokens, Scanner scanner) {
        this.tokens = tokens;
        index = 0;
        identMap = new HashMap<>();
        this.scanner = scanner;
    }

    public int parse() {
        return E();
    }

    private int E() {
        return rightRecursionE(T());
    }

    private int rightRecursionE(int t) {
        if (index >= tokens.size()) {
            return t;
        }
        Token.TokenType type = tokens.get(index++).getTokenType();
        if (type == Token.TokenType.PLUS) {
            return rightRecursionE(t + T()); // <E> ::= <T> <E’>. <E’> ::= + <T> <E’>
        } else if (type == Token.TokenType.MINUS) {
            return rightRecursionE(t - T()); // <E>  ::= <T> <E’>. <E’> ::= ... | - <T> <E’>
        } else {
            if (type == Token.TokenType.NUMBER || type == Token.TokenType.IDENT) {
                throw new RuntimeException("Missing operation between numbers");
            }
            index--;
            return t; // <E>  ::= <T> <E’>. <E’> ::= ... | .
        }
    }

    private int T() {
        return rightRecursionT(F());
    }

    private int rightRecursionT(int f) {
        if (index >= tokens.size()) {
            return f;
        }
        Token.TokenType type = tokens.get(index++).getTokenType();
        if (type == Token.TokenType.MUL) {
            return rightRecursionT(f * F()); // <T> ::= <F> <T’>. <T’> ::= * <F> <T’> | ...
        } else if (type == Token.TokenType.DIV) {
            return rightRecursionT(f / F()); // <T> ::= <F> <T’>. <T’> ::= ... | / <F> <T’> | ...
        } else {
            index--;
            return f; // <T> ::= <F> <T’>. <T’> ::= ... | .
        }
    }

    private int F() {
        try {
            Token currentToken = tokens.get(index++);
            Token.TokenType tokenType = currentToken.getTokenType();
            String currentString = currentToken.getString();
            if (tokenType == Token.TokenType.NUMBER) {
                return Integer.parseInt(currentString); // <F> ::= number | ...
            } else if (tokenType == Token.TokenType.IDENT) {
                Integer value = identMap.get(currentString);
                if (value != null) {
                    return value; // <F> ::= value | ...
                } else {
                    try {
                        value = scanner.nextInt();
                    } catch (InputMismatchException e) {
                        throw new RuntimeException("'Integer' expected, but 'String' found");
                    }
                    identMap.put(currentString, value);
                    return value; // <F> ::= value | ...
                }
            } else if (tokenType == Token.TokenType.MINUS) {
                return -F(); // <F> ::= ... | -<F>.
            } else if (tokenType == Token.TokenType.OPENBRACKET) {
                int eReturn = E();
                try {
                    if (tokens.get(index++).getTokenType() != Token.TokenType.CLOSEBRACKET) {
                        throw new RuntimeException("Bracket error, using '(' after '('");
                    }
                } catch (IndexOutOfBoundsException e) {
                    throw new RuntimeException("Bracket error, expected ) after (");
                }
                return eReturn; // <F> ::= .. | ( <E> ) | ..
            } else {
                throw new RuntimeException("Operation error, missing arg of operation");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("Operation error, missing right arg of operation");
        }
    }
}

class Token {
    private final String string;
    private final TokenType tokenType;

    public Token(TokenType tokenType, String string) {
        this.tokenType = tokenType;
        this.string = string;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getString() {
        return string;
    }

    @Override
    public String toString() {
        return string + " " + tokenType;
    }

    public enum TokenType {
        IDENT, NUMBER, OPENBRACKET, CLOSEBRACKET, PLUS, MINUS, MUL, DIV
    }
}
