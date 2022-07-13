package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    EditText editText1,editText2, fromEditText,toEditText;
    TextView textViewResult,textViewFunction;
    CardView layout;
    RelativeLayout exchangeLayout;
    LinearLayout calculatorLayout;
    boolean visibleCalculator =true, visibleExchange;
    double result;
    String BASE_URL="https://api.exchangerate.host/", from="EUR",to="USD";
    Spinner spinnerFrom,spinnerTo;
    double rate;
    JsonPlaceHolderApi jsonPlaceHolderApi;
    ImageView imageView;
    int DEFAULT_EUR_POSITION=46, DEFAULT_USD_POSITION=149;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText1=findViewById(R.id.editTextNumber1);
        editText2=findViewById(R.id.editTextNumber2);
        textViewResult=findViewById(R.id.resultTextView);
        textViewFunction=findViewById(R.id.textViewFunction);
        fromEditText= findViewById(R.id.fromEditText);
        toEditText= findViewById(R.id.toEditText);
        layout=findViewById(R.id.layout);
        exchangeLayout=findViewById(R.id.exchangeLayout);
        calculatorLayout=findViewById(R.id.calculatorLayout);
        spinnerFrom=findViewById(R.id.spinner1);
        spinnerTo=findViewById(R.id.spinner2);
        imageView=findViewById(R.id.statusImageView);


        //not to open keyboard
        editText1.setShowSoftInputOnFocus(false);
        editText2.setShowSoftInputOnFocus(false);
        fromEditText.setShowSoftInputOnFocus(false);

        spinnerFrom.setSelection(DEFAULT_EUR_POSITION); //EUR's position, default from
        spinnerTo.setSelection(DEFAULT_USD_POSITION); //USD's position, default to

        //get seletion from spinners
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView=(TextView) view;
                from=textView.getText().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView=(TextView) view;
                to=textView.getText().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                to="USD";
            }
        });

    }

    //transition for calculator-exchange layout
    public void transition(View view){
        TransitionManager.beginDelayedTransition(layout);
        visibleCalculator = !visibleCalculator;
        visibleExchange =!visibleExchange;
        calculatorLayout.setVisibility(visibleCalculator ? View.VISIBLE : View.GONE);
        exchangeLayout.setVisibility(visibleExchange ? View.VISIBLE : View.GONE);
        if(visibleCalculator){
            imageView.setImageResource(R.drawable.exchange);
            if(textViewResult.getText().equals("") && !editText2.getText().toString().equals("") && !editText1.getText().toString().equals(""))
                editText2.requestFocus();
        }
        else if(visibleExchange)
            imageView.setImageResource(R.drawable.calculator);
    }


    public void getExchange(View view){
        String fromText=fromEditText.getText().toString();

        //if there is amount to exchange
        if(!fromText.equals("") ){
            //check if negative from previous result and change it
            if(fromText.startsWith("-")) {
                fromText = fromText.substring(1);
                fromEditText.setText(fromText);
            }
            double fromAmount = Double.parseDouble(fromText);

            //make the call to fetch the rate
            Call<Post> call = jsonPlaceHolderApi.getRate(from, to);
            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if (response.isSuccessful()) {
                        rate = Double.parseDouble(response.body().getResult());
                        double exchangedResult = fromAmount * rate;
                        toEditText.setText(String.valueOf(exchangedResult));
                    }
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {

                }
            });
        }
        else Toast.makeText(this,"Please enter amount to convert!",Toast.LENGTH_LONG).show();
    }

    //when a digit is clicked
    public void getDigits(View view){
        String character = view.getTag().toString();
        checkFocus();
        //append text to focused EditText
        appendText((EditText) this.getCurrentFocus(),checkDot(character));
    }

    //check which editText needs focus
    public void checkFocus(){
        //check if calculator layout is visible
        if(visibleCalculator){
            //if there is final result restart
            if(!textViewResult.getText().toString().equals(""))
                resetNumbers();
            if(editText1.getText().toString().equals("") || editText1.getText().toString().equals("+") || editText1.getText().toString().equals("-"))
                editText1.requestFocus(); //give focus when digit is clicked
            else if(!editText1.hasFocus() && editText2.getText().toString().equals("") || editText2.getText().toString().equals("+") || editText2.getText().toString().equals("-"))
                editText2.requestFocus();
        }
        else if(visibleExchange){ //if exchanged layout visible
            if(!toEditText.getText().toString().equals(""))
                resetNumbers();
            if(!fromEditText.hasFocus())
                fromEditText.requestFocus();
            if(!fromEditText.getText().toString().equals(""))
                fromEditText.setSelection(fromEditText.getText().toString().length());
        }
    }

    public String checkDot(String character){
        EditText editText= (EditText) this.getCurrentFocus();
        //check if first element clicked is "." and format right
        if(editText.getText().toString().equals("") && character.equals("."))
            character="0.";
        if(editText.getText().toString().contains("."))
            character="";
        return character;
    }

    public void plusMinus(View view){
        if(!textViewResult.getText().toString().equals(""))
            resetNumbers();
        checkFocus(); //check which editext needs focus
        EditText editText = (EditText)this.getCurrentFocus();
        String text= editText.getText().toString();

        if(text.startsWith("+")){
            String tmp="-"+text.substring(1);
            editText.setText(tmp);
        }
        else if(text.startsWith("-")){
            String tmp="+"+text.substring(1);
            editText.setText(tmp);
        }
        else {
            String tmp="+"+text;
            editText.setText(tmp);
        }
        editText.setSelection(editText.getText().toString().length());
    }

    public void getFunction(View view){
        if(visibleCalculator) {
            String function = view.getTag().toString();
            //when function clicked move to next number
            if (editText2.getText().toString().equals(""))
                editText2.requestFocus();

            //when equals is last function restart with result as number1
            if (!textViewResult.getText().toString().equals("")) {
                String tmp = textViewResult.getText().toString();
                resetNumbers();
                editText1.setText(tmp);
                editText2.requestFocus();
            }
            textViewFunction.setText(function);
        }
    }

    public void getEquals(View view){
        String function=textViewFunction.getText().toString();
        if(!editText1.getText().toString().equals("") && !editText2.getText().toString().equals("")){
            double num1= Double.parseDouble(editText1.getText().toString());
            double num2= Double.parseDouble(editText2.getText().toString());

            switch (function){
                case "+":
                    result=num1+num2;
                    break;
                case "-":
                    result=num1-num2;
                    break;
                case "x":
                    result=num1*num2;
                    break;
            }
            if(function.equals("/") && num2==0)
                Toast.makeText(this,"Can't divide by 0",Toast.LENGTH_LONG).show();
            else if(function.equals("/"))
                result=num1/num2;

            //give right format
            String resultStr= String.valueOf(result);
            if(resultStr.endsWith(".0"))
                resultStr= resultStr.substring(0,resultStr.length()-2);

            textViewResult.setText(resultStr);
            fromEditText.setText(resultStr);
            editText2.clearFocus();
        }
        else Toast.makeText(this,"Enter both numbers to get a result!",Toast.LENGTH_LONG).show();

    }

    public void appendText(EditText editText, String digit){
        if(!digit.equals("")) {
            String oldStr = editText.getText().toString();
            int cursorPosition = editText.getSelectionStart(); //get cursors position to add char there
            String leftStr = oldStr.substring(0, cursorPosition); //left of cursor
            String rightStr = oldStr.substring(cursorPosition);//right of cursor
            String updatedStr = leftStr + digit + rightStr;
            editText.setText(updatedStr);
            //check and put cursor in right position
            if (digit.equals("0."))
                editText.setSelection(updatedStr.length());
            else editText.setSelection(cursorPosition + 1);
        }
    }

    public void clearButton(View view){
        resetNumbers();
    }


    public void resetNumbers(){
        if(visibleCalculator){
            editText2.setText("");
            editText1.setText("");
            textViewResult.setText("");
        }
        else if(visibleExchange) {
            fromEditText.setText("");
            toEditText.setText("");
            spinnerTo.setSelection(DEFAULT_USD_POSITION);
            spinnerFrom.setSelection(DEFAULT_EUR_POSITION);
        }

    }

    public void backSpace(View view) {
        EditText editText = (EditText) this.getCurrentFocus();
        //check if any editText has focus
        if (editText != null) {
            int cursorPosition = editText.getSelectionStart();
            int textLength = editText.getText().length();

            if (cursorPosition != 0 && textLength != 0) {
                SpannableStringBuilder selection = (SpannableStringBuilder) editText.getText();
                selection.replace(cursorPosition - 1, cursorPosition, "");
                editText.setText(selection.toString());
                editText.setSelection(cursorPosition - 1);
            }
        }
    }
}