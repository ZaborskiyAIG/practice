package com.practice.nir.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecursiveDescentParserCalculator {

    public static int getSolution(String formula, Map<String, String> maps ){

        for (Map.Entry<String, String> map : maps.entrySet()) {
            formula = formula.replace( map.getKey(), map.getValue() );
        }


        List<Lexeme> lexemes = lexAnalyze(formula);

        LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemes);


        return expr(lexemeBuffer);
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
                    if (c <= '9' && c >= '0') {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(c);
                            pos++;
                            if (pos >= expText.length()) {
                                break;
                            }
                            c = expText.charAt(pos);
                        } while (c <= '9' && c >= '0');
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

    private static int expr(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        if (lexeme.type == LexemeType.EOF) {
            return 0;
        } else {
            lexemes.back();
            return implication(lexemes);
        }
    }

    private static int implication(LexemeBuffer lexemes) {
        int value = plus(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_IMPL:

                    int plus = plus(lexemes);

                    if(value == 1 && plus == 0){
                        value = 0;
                    }else {
                        value = 1;
                    }

                    break;
                case RIGHT_BRACKET:
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


    private static int plus(LexemeBuffer lexemes) {

        int value = multdiv(lexemes);
        while (true) {

            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_PLUS:

                    int mult = multdiv(lexemes);

                    if(value == 0 && mult == 0){
                        value = 0;
                    }else {
                        value = 1;
                    }

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


    private static int multdiv(LexemeBuffer lexemes) {
        int value = notExpression(lexemes);
        while (true) {

            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_MUL:

                    int expr = notExpression(lexemes);

                    if(value==1 && expr == 1){
                        value = 1;
                    } else {
                        value =  0;
                    }

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

    private static int notExpression(LexemeBuffer lexemes) {
        int value = factor(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();

            switch (lexeme.type) {
                case OP_NOT:

                    // value=value+"!";
                    //    value += factor(lexemes);


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



    private static int factor(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        switch (lexeme.type) {
            case LITERAL:
                return Integer.valueOf(lexeme.value);

            case OP_NOT:

                lexeme = lexemes.next();

                if(Integer.valueOf(lexeme.value) == 0){
                    lexeme.value = "1";
                } else {
                    lexeme.value = "0";
                }

                return Integer.valueOf(lexeme.value);

            case LEFT_BRACKET:
                int value = implication(lexemes);
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
