import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Calc {
    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);
        Lexer l = new Lexer(scn.nextLine());
        Parser p = new Parser(l.lex(), scn);
        try {
            System.out.println(p.Parse());
        } catch (Exception e) {
            System.out.println("error");
        }
    }
}

class Lexer {
    private final String string;
    private final Pattern binaryOpPattern, identPattern, whiteSpacePattern, bracketPattern, digitPattern;

    public Lexer(String string) {
        binaryOpPattern = Pattern.compile("[+-/*]");
        identPattern = Pattern.compile("[a-zA-Z]");
        whiteSpacePattern = Pattern.compile("[ \t\n]");
        bracketPattern = Pattern.compile("[()]");
        digitPattern = Pattern.compile("[0-9]");
        this.string = string;
    }

    public ArrayList<Token> lex() {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<Token> tokenArrayList = new ArrayList<>();
        boolean digitFlag = false, identFlag = false;
        for (int i = 0; i < string.length(); i++) {
            String c = Character.toString(string.charAt(i));
            if (digitPattern.matcher(c).matches()) {
                digitFlag = !identFlag;
                stringBuilder.append(c);
            } else if (identPattern.matcher(c).matches()) {
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
                if (binaryOpPattern.matcher(c).matches()) {
                    switch (c) {
                        case "+":
                            tokenArrayList.add(new Token(Token.TokenType.PLUS, c));
                            break;
                        case "-":
                            tokenArrayList.add(new Token(Token.TokenType.MINUS, c));
                            break;
                        case "*":
                            tokenArrayList.add(new Token(Token.TokenType.MUL, c));
                            break;
                        case "/":
                            tokenArrayList.add(new Token(Token.TokenType.DIV, c));
                            break;
                    }
                } else if (bracketPattern.matcher(c).matches()) {
                    if (c.equals(")")) {
                        tokenArrayList.add(new Token(Token.TokenType.CLOSEBRACKET, c));
                    } else {
                        tokenArrayList.add(new Token(Token.TokenType.OPENBRACKET, c));
                    }
                } else if (!whiteSpacePattern.matcher(c).matches()) {
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

    public int Parse() {
        return E();
    }

    private int E() {
        return T() + Ee();
    }

    private int Ee() throws RuntimeException {
        if (index >= tokens.size()) {
            return 0;
        }
        Token.TokenType type = tokens.get(index++).getTokenType();
        if (type == Token.TokenType.PLUS) {
            return T() + Ee(); //<E> ::= <T> <E’>. <E’> ::= + <T> <E’>
        } else if (type == Token.TokenType.MINUS) {
            return -T() + Ee(); // <E>  ::= <T> <E’>. <E’> ::= ... | - <T> <E’>
        } else {
            if (type == Token.TokenType.NUMBER || type == Token.TokenType.IDENT) {
                throw new RuntimeException("Missing operation between numbers");
            }
            index--;
            return 0; // <E>  ::= <T> <E’>. <E’> ::= ... | .
        }
    }

    private int T() {
        ArrayList<Token> a = new ArrayList<>();
        int f = F();
        Tt(a);
        System.out.println(a);
        boolean mulFlag = false, divFlag = false;
        for (Token t :
                a) {
            if (t.getTokenType() == Token.TokenType.MUL) {
                mulFlag = true;
            } else if (t.getTokenType() == Token.TokenType.DIV) {
                divFlag = true;
            }
            if (mulFlag && t.getTokenType() == Token.TokenType.NUMBER) {
                mulFlag = false;
                f *= Integer.parseInt(t.string);
            } else if (divFlag && t.getTokenType() == Token.TokenType.NUMBER) {
                divFlag = false;
                f /= Integer.parseInt(t.string);
            }
        }
        return f;
    }

    private void Tt(ArrayList<Token> a) {
        if (index < tokens.size()) {
            Token.TokenType type = tokens.get(index++).getTokenType();
            if (type == Token.TokenType.MUL) {
                a.add(new Token(Token.TokenType.MUL, "*"));
                a.add(new Token(Token.TokenType.NUMBER, Integer.toString(F())));
                Tt(a); // <T> ::= <F> <T’>. <T’> ::= * <F> <T’> | ...
            } else if (type == Token.TokenType.DIV) {
                a.add(new Token(Token.TokenType.DIV, "/"));
                a.add(new Token(Token.TokenType.NUMBER, Integer.toString(F())));
                Tt(a); // <T>  ::= <F> <T’>. <T’> ::= / <F> <T’>
            } else {
                index--; // <T> ::= <F> <T’>. <T’> ::= ... | .
            }
        }
    }

    private int F() {
        try {
            Token currentToken = tokens.get(index++);
            Token.TokenType type = currentToken.getTokenType();
            String currentString = currentToken.string;
            if (type == Token.TokenType.NUMBER) {
                return Integer.parseInt(currentString); // <F> ::= number | ...
            } else if (type == Token.TokenType.IDENT) {
                if (identMap.containsKey(currentString)) {
                    return identMap.get(currentString); // <F> ::= value | ...
                } else {
                    Integer currentInt = scanner.nextInt();
                    identMap.put(currentString, currentInt);
                    return currentInt; // <F> ::= value | ...
                }
            } else if (type == Token.TokenType.MINUS) {
                return -F(); // <F> ::= ... | -<F>.
            } else if (type == Token.TokenType.OPENBRACKET) {
                int eReturn = E();
                try {
                    if (tokens.get(index++).getTokenType() != Token.TokenType.CLOSEBRACKET) {
                        throw new RuntimeException("Bracket error, using '(' after (");
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
    public final String string;
    private final TokenType tokenType;

    public Token(TokenType tokenType, String string) {
        this.tokenType = tokenType;
        this.string = string;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        return string + " " + tokenType;
    }

    public enum TokenType {
        IDENT, NUMBER, OPENBRACKET, CLOSEBRACKET, PLUS, MINUS, MUL, DIV
    }
}