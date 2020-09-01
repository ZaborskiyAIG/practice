package com.practice.nir.util;

import java.util.ArrayList;
import java.util.List;

public class RecursiveDescentParser {

    private static List<String> formulas = new ArrayList<>();

    public static void clear(){
        formulas.clear();
    }

    public static List<String> getFormulas(String formula){

        List<Lexeme> lexemes = lexAnalyze(formula);

        LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemes);

        expr(lexemeBuffer);
        return formulas;
    }

    public enum LexemeType {
        LEFT_BRACKET, RIGHT_BRACKET,
        OP_NOT, OP_MUL, OP_PLUS, OP_IMPL,
        LITERAL,
        EOF;
    }

    public static class Lexeme {
        LexemeType type;
        String value;

        Lexeme(LexemeType type, String value) {
            this.type = type;
            this.value = value;
        }

        Lexeme(LexemeType type, Character value) {
            this.type = type;
            this.value = value.toString();
        }

        @Override
        public String toString() {
            return "Lexeme{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static class LexemeBuffer {
        private int pos;

        List<Lexeme> lexemes;

        LexemeBuffer(List<Lexeme> lexemes) {
            this.lexemes = lexemes;
        }

        Lexeme next() {
            return lexemes.get(pos++);
        }

        void back() {
            pos--;
        }

        int getPos() {
            return pos;
        }
    }

    private static List<Lexeme> lexAnalyze(String expText) {
        ArrayList<Lexeme> lexemes = new ArrayList<>();
        int pos = 0;
        while (pos< expText.length()) {
            char c = expText.charAt(pos);
            switch (c) {
                case '(':
                    lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, c));
                    pos++;
                    continue;
                case ')':
                    lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, c));
                    pos++;
                    continue;
                case '!':
                    lexemes.add(new Lexeme(LexemeType.OP_NOT, c));
                    pos++;
                    continue;
                case '@':
                    lexemes.add(new Lexeme(LexemeType.OP_IMPL, c));
                    pos++;
                    continue;
                case '*':
                    lexemes.add(new Lexeme(LexemeType.OP_MUL, c));
                    pos++;
                    continue;
                case '+':
                    lexemes.add(new Lexeme(LexemeType.OP_PLUS, c));
                    pos++;
                    continue;
                default:
                    //if (c <= '9' && c >= '0') {
                    if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(c);
                            pos++;
                            if (pos >= expText.length()) {
                                break;
                            }
                            c = expText.charAt(pos);
                            //   } while (c <= '9' && c >= '0');
                        } while  ((c >= 65 && c <= 90) || (c >= 97 && c <= 122)) ;
                        lexemes.add(new Lexeme(LexemeType.LITERAL, sb.toString()));
                    } else {
                        if (c != ' ') {
                            throw new RuntimeException("Unexpected character: " + c);
                        }
                        pos++;
                    }
            }
        }
        lexemes.add(new Lexeme(LexemeType.EOF, ""));
        return lexemes;
    }

    private static String expr(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        if (lexeme.type == LexemeType.EOF) {
            return "0";
        } else {
            lexemes.back();
            return implication(lexemes);
        }
    }

    private static String implication(LexemeBuffer lexemes) {
        String value = plus(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_IMPL:

                    value=value+"@";
                    value += plus(lexemes);

                    formulas.add(value);

                    break;
                case RIGHT_BRACKET:
                    value = value +")";
                    lexemes.back();
                    return value;
                case EOF:
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
            }
        }
    }


    private static String plus(LexemeBuffer lexemes) {

        String value = multdiv(lexemes);
        while (true) {

            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_PLUS:
                    value=value+"+";
                    value += multdiv(lexemes);
                    formulas.add(value);

                    break;
                case EOF:
                case RIGHT_BRACKET:
                case OP_IMPL:
                    lexemes.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
            }
        }
    }


    private static String multdiv(LexemeBuffer lexemes) {
        String value = notExpression(lexemes);
        while (true) {

            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_MUL:
                    value=value+"*";
                    value += notExpression(lexemes);
                    formulas.add(value);

                    break;
                case EOF:
                case RIGHT_BRACKET:
                case OP_PLUS:
                case OP_IMPL:

                    lexemes.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
            }
        }
    }

    private static String notExpression(LexemeBuffer lexemes) {
        String value = factor(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();

            switch (lexeme.type) {
                case OP_NOT:

                    // value=value+"!";
                    value += factor(lexemes);

                    formulas.add(value);

                    break;
                case RIGHT_BRACKET:
                case OP_MUL:
                case OP_PLUS:
                case OP_IMPL:
                case EOF:
                    lexemes.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
            }
        }
    }



    private static String factor(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        switch (lexeme.type) {
            case LITERAL:
                return lexeme.value;

            case OP_NOT:
                lexemes.back();
                return lexeme.value;

            case LEFT_BRACKET:
                String value ="(" + implication(lexemes);
                lexeme = lexemes.next();

                if (lexeme.type != LexemeType.RIGHT_BRACKET) {
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
                }

                return value;


            default:
                throw new RuntimeException("Unexpected token: " + lexeme.value
                        + " at position: " + lexemes.getPos());
        }
    }

}
