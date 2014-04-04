package com.huderlem.caesarcypher.app;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

    NumberPicker rotationPicker;
    EditText inputText;
    TextView cipherText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cipherText = (TextView)findViewById(R.id.textView2);

        rotationPicker = (NumberPicker)findViewById(R.id.numberPicker);
        rotationPicker.setMinValue(0);
        rotationPicker.setMaxValue(25);

        rotationPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                refreshCipherText();
            }
        });


        inputText = (EditText) findViewById(R.id.editText);
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                refreshCipherText();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for ( int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i)) && source.charAt(i) != ' ') {
                        return "";
                    }
                }
                return null;
            }
        };
        inputText.setFilters(new InputFilter[]{filter});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshCipherText() {
        String output = computeCipher(rotationPicker.getValue(), inputText.getText().toString());
        cipherText.setText(output);
    }

    public String computeCipher(int rotation, String input) {
        // assume input is only a-zA-Z
        int a_num = (int) 'a';
        int A_num = (int) 'A';
        String output = "";
        for (int i = 0; i < input.length(); i++) {
            int cur = (int) input.charAt(i);
            // check if lowercase or uppercase
            if (input.charAt(i) == ' ') {
                output += " ";
            } else if (cur >= a_num && cur < a_num + 26) {
                output += Character.toString((char) ((((cur - a_num) + rotation) % 26) + a_num));
            } else {
                output += Character.toString((char) ((((cur - A_num) + rotation) % 26) + A_num));
            }
        }

        return output;
    }


}
