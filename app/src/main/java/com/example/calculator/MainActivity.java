package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    TextView equation;
    int countOfFirstBracket = 0;
    int countOfEndBracket = 0;

    int countOfOperations = 0;
    int countOfPoints = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        equation = findViewById(R.id.textView);
    }

    public void equal(View view){
        double result = eval(equation.getText().toString());
        countOfFirstBracket = 0;
        countOfEndBracket = 0;
        countOfPoints = 1;
        countOfOperations = 0;
        equation.setText(String.valueOf(result));
    }
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

    public void deleteAll(View view){
        equation.setText("");
    }

    public void percent(View view){
        String text = equation.getText().toString();
        if (!text.equals("")){
            char last = text.charAt(text.length() - 1);
            if (last >= '0' && last <= '9'){
                equation.setText(text + " / 100");
            }
        }
    }

    public void addOperation(View view){
        Button button = (Button) view;
        String operation = button.getText().toString();
        String text = equation.getText().toString();
        if (!text.equals("")){
            char last = text.charAt(text.length() - 1);
            if ((last >= '0' && last <= '9') || last == ')'){
                equation.setText(text + operation);
                countOfOperations += 1;
            }
        }
    }

    public void addNumber(View view){
        Button button = (Button) view;
        String digit = button.getText().toString();
        String text = equation.getText().toString();
        if (!text.equals("")){
            char last = text.charAt(text.length() - 1);
            if (last != ')'){
                equation.setText(text + digit);
            }
        }
        else
            equation.setText(text + digit);
    }

    public void addPoint(View view){
        String text = equation.getText().toString();
        if (!text.equals("")){
            char last = text.charAt(text.length() - 1);
            if (last >= '0' && last <= '9' && countOfOperations >= countOfPoints){
                equation.setText(text + '.');
                countOfPoints += 1;
            }
        }
    }

    public void addBracket(View view){
        Button button = (Button) view;
        String bracket = button.getText().toString();
        if (bracket.equals(")")){

            if (countOfFirstBracket > countOfEndBracket){
                String text = equation.getText().toString();
                if (!text.equals("")){
                    char last = text.charAt(text.length() - 1);
                    if (last >= '0' && last <= '9'){
                        equation.setText(text + bracket);
                        countOfEndBracket++;
                    }
                }
            }
        }
        else{
            String text = equation.getText().toString();
            if (!text.equals("")){
                char last = text.charAt(text.length() - 1);
                if (last == '+' || last == '-' || last == '*' || last == '/'){
                    equation.setText(text + bracket);
                    countOfFirstBracket++;
                }
            }
        }

    }
}