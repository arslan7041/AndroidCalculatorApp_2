package com.arslansana.calculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.math.BigDecimal;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    // Widgets
    private EditText result;
    private EditText expression;

    // variables to hold the operands and type of calculations
//    private final int MAX_OPERATIONS = 6;
//    private int numOperations = 0;
    private String expressionStr = "";
    private int cursorPosition = 0;
    private int numOpeningParen = 0;

    // Keys for saving/restoring
//    private static final String STATE_MAX_OPERATIONS = "max_operations";
//    private static final String STATE_NUM_OPERATIONS = "numOperations";
    private static final String STATE_EXPRESSION = "expressionStr";
    private static final String STATE_CURSOR_POSITION = "cursorPosition";
    private static final String STATE_NUM_OPENING_PAREN = "numOpeningParen";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (EditText) findViewById(R.id.result);
        expression = (EditText) findViewById(R.id.expression);

        /***************** CODE TO DISABLE SOFT KEYBOARD *************/
//        final InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
//        expression.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                v.clearFocus();
//                if (inputMethodManager != null) {
//                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                }
//            }
//        });

        Button button0 = (Button) findViewById(R.id.button0);
        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);
        Button button6 = (Button) findViewById(R.id.button6);
        Button button7 = (Button) findViewById(R.id.button7);
        Button button8 = (Button) findViewById(R.id.button8);
        Button button9 = (Button) findViewById(R.id.button9);
        Button buttonDot = (Button) findViewById(R.id.buttonDot);

        Button buttonEquals = (Button) findViewById(R.id.buttonEquals);
        Button buttonDivide = (Button) findViewById(R.id.buttonDivide);
        Button buttonMultiply = (Button) findViewById(R.id.buttonMultiply);
        Button buttonMinus = (Button) findViewById(R.id.buttonMinus);
        Button buttonPlus = (Button) findViewById(R.id.buttonPlus);

        View.OnClickListener numListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                expressionStr = expression.getText().toString();
                cursorPosition = expression.getSelectionStart();

                // if last char was ')', new number entered will be multiplied
                if(expressionStr.length() >= 1 && cursorPosition > 0 && expressionStr.charAt(cursorPosition-1) == ')') {
                    expression.getText().insert(cursorPosition,"*");
                    cursorPosition = expression.getSelectionStart(); // update cursorPosition for possible insert afterwards
                }
                // if digit, insert new number into expression at cursorPosition
                if(isDigit(b.getText().toString().charAt(0))){
                    expression.getText().insert(cursorPosition, b.getText().toString());
                } else{ // if '.'
                    if(expressionStr.isEmpty())
                        expression.append("0.");
                    else if(isDotValid(cursorPosition, expressionStr)) { // if not empty and dot valid to put in
                        if(cursorPosition == 0)
                            expression.getText().insert(cursorPosition, "0.");
                        else if (isDigit(expressionStr.charAt(cursorPosition - 1)))
                            expression.getText().insert(cursorPosition, ".");
                        else
                            expression.getText().insert(cursorPosition, "0.");
                    }
                }
            }
        };

        button0.setOnClickListener(numListener);
        button1.setOnClickListener(numListener);
        button2.setOnClickListener(numListener);
        button3.setOnClickListener(numListener);
        button4.setOnClickListener(numListener);
        button5.setOnClickListener(numListener);
        button6.setOnClickListener(numListener);
        button7.setOnClickListener(numListener);
        button8.setOnClickListener(numListener);
        button9.setOnClickListener(numListener);
        buttonDot.setOnClickListener(numListener);

        View.OnClickListener opListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                String op = b.getText().toString();
                expressionStr = expression.getText().toString();
                cursorPosition = expression.getSelectionStart();

                if(!expressionStr.isEmpty()){ // operators only work if the expression is NOT empty
                    if(!op.equals("=") && cursorPosition != 0){
                        if(cursorPosition == expressionStr.length() && isOperator(expressionStr.charAt(cursorPosition-1))){ // cursor at end
                            StringBuilder sb = new StringBuilder(expressionStr);
                            String str = sb.replace(cursorPosition-1, cursorPosition, op).toString();
                            expression.setText(str);
                            expression.setSelection(cursorPosition); // keep cursor at current pos
                        } else if( cursorPosition > 0 && cursorPosition < expressionStr.length() ) { // cursor in middle
                            if(isOperator(expressionStr.charAt(cursorPosition-1))){ // operator before cursor
                                StringBuilder sb = new StringBuilder(expressionStr);
                                String str = sb.replace(cursorPosition-1, cursorPosition, op).toString();
                                expression.setText(str);
                                expression.setSelection(cursorPosition); // keep cursor at current pos
                            }else if(isOperator(expressionStr.charAt(cursorPosition))) { // operator after cursor
                                StringBuilder sb = new StringBuilder(expressionStr);
                                String str = sb.replace(cursorPosition, cursorPosition+1, op).toString();
                                expression.setText(str);
                                expression.setSelection(cursorPosition); // keep cursor at current pos
                            } else{
                                expression.getText().insert(cursorPosition, op);    // just insert the operator
                            }
                        } else{                             // cursor is at beginning or end, but no operator next to it
                            expression.getText().insert(cursorPosition, op);
                        }
                    } else if(op.equals("=")) { // operator '='
                        if(!isOperator(expressionStr.charAt(expressionStr.length()-1))) { // if last char is NOT an operator
                            BigDecimal answer = BigDecimal.valueOf(eval(expression.getText().toString()));
                            result.setText(String.valueOf(answer));
                        }
                    }
                }
            }
        };

        buttonEquals.setOnClickListener(opListener);
        buttonDivide.setOnClickListener(opListener);
        buttonMultiply.setOnClickListener(opListener);
        buttonPlus.setOnClickListener(opListener);
        buttonMinus.setOnClickListener(opListener);

        Button buttonNeg = (Button) findViewById(R.id.buttonNeg);
        buttonNeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expressionStr = expression.getText().toString();
                cursorPosition = expression.getSelectionStart();
                int res = isNegated(expression);

                // first test to see if the token is already negated
                if(res >= 0) { // if token is negated
                    //un-negate it
                    StringBuilder sb = new StringBuilder(expressionStr);
                    expressionStr = sb.delete(res, res + 2).toString();
                    expression.setText(expressionStr);

//                    if(getNumOpeningParen(expressionStr, cursorPosition) != 0)
//                        numOpeningParen--;

                    if(cursorPosition < 2 ) // if cursor was towards beginning, set it to beginning
                        expression.setSelection(0);
                    else                    // cursor will alays end up same relative position it was in
                        expression.setSelection(cursorPosition-2);

                } else{ // now we can execute cases where token is NOT already negated.

                    if (expressionStr.length() == 0) {
                        expression.append("(-");
                        expression.setSelection(expression.length()); // set cursor to end of expression
//                        numOpeningParen++;
                    } else if(cursorPosition == 0 || isOperator(expressionStr.charAt(cursorPosition-1)) || expressionStr.charAt(cursorPosition-1) == '('){ // before cursor is operator or '('
                        expression.getText().insert(cursorPosition, "(-");
//                        numOpeningParen++;
                    } else if(expressionStr.charAt(cursorPosition-1) == ')'){ // if before cursor is ')'
                        expression.getText().insert(cursorPosition, "*(-");
                    } else { // if before cursor is '.' or digit
                        int newCursorPosition = startOfNum(expressionStr);
                        expression.getText().insert(newCursorPosition+1, "(-");
                    }

//                    numOpeningParen++;
                }
            }
        });

        Button buttonClear = (Button) findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.setText("");
                expression.setText("");
                numOpeningParen = 0;
            }
        });

        Button buttonParen = (Button) findViewById(R.id.buttonParen);
        buttonParen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expressionStr = expression.getText().toString();
                cursorPosition = expression.getSelectionStart();
                char lastChar = '=';
                if(expressionStr.length() > 0 && cursorPosition > 0)
                    lastChar = expressionStr.charAt(cursorPosition-1);
                if(expressionStr.isEmpty() || cursorPosition == 0 || isOperator(lastChar) || lastChar == '(' || isBalancedParenthesis(expressionStr)) {
                    expression.getText().insert(cursorPosition, "(");
//                    numOpeningParen++;

                } else if(getNumOpeningParen(expressionStr, cursorPosition) != 0){
                    boolean isNumBeforeCursor = isDigit(expressionStr.charAt(cursorPosition-1));
                    boolean isNumAfterCursor = false; // set to false until we know cursor is not at end
                    if(cursorPosition < expressionStr.length() && isDigit(expressionStr.charAt(cursorPosition)) ){
                        isNumAfterCursor = true;
                    }
                    expression.getText().insert(cursorPosition, ")");
                    cursorPosition = expression.getSelectionStart(); // update cursorPosition for possible insert afterwards
                    if(isNumBeforeCursor && isNumAfterCursor){
                        expression.getText().insert(cursorPosition, "*");
                    }
//                    numOpeningParen--;
                } else if(!isOperator(lastChar) && getNumOpeningParen(expressionStr, cursorPosition) == 0){
                    expression.getText().insert(cursorPosition, "*(");
//                    numOpeningParen++;
                }
            }
        });


        /********** BUGS TO BE FIXED / FEATURES TO ADD ******************
         * 1. 2|35 + button paren --> 2*(35
         *    (2|35 + buttonParen --> (2)*35
         * 2. Create delete button
         * 3. Move answer down to expression bar or make an 'answer' button
         */
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

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

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
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
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

    private boolean isOperator(char c){
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private boolean isDigit(char c){
        for(char i = '0'; i <= '9'; i++){
            if(c == i) return true;
        }
        return false;
    }

    private boolean isDotValid(int cursorPosition, String expressionStr){
        boolean dotValid = true;
        int up = cursorPosition;
        int down = cursorPosition-1;
        boolean upOperator = false;
        boolean downOperator = false;
        while(up < expressionStr.length() || down >= 0){
            if( up < expressionStr.length() && !upOperator) {
                if(expressionStr.charAt(up) == '.')
                    return false;
                if(isOperator(expressionStr.charAt(up)))
                    upOperator = true;

                up++;
            }

            if(down >= 0 && !downOperator){
               if(expressionStr.charAt(down) == '.')
                   return false;
               if(isOperator(expressionStr.charAt(down)))
                   downOperator = true;

               down--;
            }

            if(upOperator && downOperator)
                return true;
            if(upOperator && down == -1)
                return true;
            if(downOperator && up == expressionStr.length())
                return true;
        }

        return true;
    }

    private boolean ArePair(char opening,char closing)
    {
        return (opening == '(' && closing == ')');
    }

    private boolean isBalancedParenthesis(String s) {
        Stack<Character> stack = new Stack<>();
        for(int i =0;i<s.length();i++)
        {
            if(s.charAt(i) == '(')
                stack.push(s.charAt(i));
            else
            {
                if(stack.empty() || !ArePair(stack.peek(), s.charAt(i)))
                    return false;
                else
                    stack.pop();
            }
        }
        return stack.empty();
    }

    private int getNumOpeningParen(String expressionStr, int cursorPosition){
        int openingParen = 0;
        for(int i = 0; i < cursorPosition; i++){
            if(expressionStr.charAt(i) == '(')
                openingParen++;
            if(expressionStr.charAt(i) == ')')
                openingParen--;
        }

        return openingParen;
    }

    private int startOfNum(String expressionStr){
        int i = expression.getSelectionStart() - 1;
        while(i >= 0 && (expressionStr.charAt(i) == '.' || isDigit(expressionStr.charAt(i))))
                i--;
        return i;
    }

    private int isNegated(EditText expression){
        String expressionStr = expression.getText().toString();
        if(expressionStr.length() < 2) return -1; // if its less than two chars, it can't have negation "(-"

        if( cursorPosition >= 2){
            if((expressionStr.charAt(cursorPosition-1) == '-' && expressionStr.charAt(cursorPosition-2) == '(') // "(-" directly before cursor
                    || (isDigit(expressionStr.charAt(cursorPosition-1)) || expressionStr.charAt(cursorPosition-1) == '.' ) // digits or '.' before cursor
                    || cursorPosition != expressionStr.length() &&                                                      // there is something after cursor
                    (isDigit(expressionStr.charAt(cursorPosition+1)) || expressionStr.charAt(cursorPosition+1) == '.')){ // digit or '.' after cursor

                int i = cursorPosition-1;
                while(true){

                    if(i == 0) return -1;
                    if(expressionStr.charAt(i) == '-' && expressionStr.charAt(i-1) == '(')
                        return i-1;
                    if(isOperator(expressionStr.charAt(i)) && expressionStr.charAt(i) != '-')
                        return -1;

                    i--;
                }
            }
            return -1;
        }

        if(expressionStr.charAt(0) == '(' && expressionStr.charAt(1) == '-')
            return 0;

        return -1;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_EXPRESSION, expressionStr);
        outState.putInt(STATE_CURSOR_POSITION, cursorPosition);
        //outState.put
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}


