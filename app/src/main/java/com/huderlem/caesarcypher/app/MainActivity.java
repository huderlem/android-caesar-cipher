package com.huderlem.caesarcypher.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

    NumberPicker rotationPicker;
    EditText inputText;
    TextView cipherText;
    RadioGroup radioGroup;
    EditText codewordText;
    Button clearButton;

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

        codewordText = (EditText)findViewById(R.id.editText2);
        codewordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                refreshCipherText();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i) {
                    case R.id.radioButton:
                        codewordText.setVisibility(View.GONE);
                        rotationPicker.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radioButton2:
                        rotationPicker.setVisibility(View.GONE);
                        codewordText.setVisibility(View.VISIBLE);
                        break;
                }
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
        codewordText.setFilters(new InputFilter[]{filter});

        clearButton = (Button)findViewById(R.id.button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputText.setText("");
                cipherText.setText("");
            }
        });

        loadState();

        switch(radioGroup.getCheckedRadioButtonId()) {
            case R.id.radioButton:
                codewordText.setVisibility(View.GONE);
                rotationPicker.setVisibility(View.VISIBLE);
                break;
            case R.id.radioButton2:
                rotationPicker.setVisibility(View.GONE);
                codewordText.setVisibility(View.VISIBLE);
                break;
        }
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

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences settings = getSharedPreferences("cipherPrefs", 0);
        SharedPreferences.Editor editor = settings.edit();
        saveState(editor);
        editor.commit();
    }

    public void saveState(SharedPreferences.Editor editor) {
        editor.putString("inputText", inputText.getText().toString());
        editor.putInt("rotation", rotationPicker.getValue());
        editor.putString("codeword", codewordText.getText().toString());
        editor.putInt("checkedRadiobutton", radioGroup.getCheckedRadioButtonId());
    }

    public void loadState() {
        SharedPreferences settings = getSharedPreferences("cipherPrefs", 0);

        inputText.setText(settings.getString("inputText", ""));
        rotationPicker.setValue(settings.getInt("rotation", 0));
        codewordText.setText(settings.getString("codeword", ""));
        radioGroup.check(settings.getInt("checkedRadiobutton", R.id.radioButton));
        refreshCipherText();
    }

    public void refreshCipherText() {
        String output = computeCipher(inputText.getText().toString());
        cipherText.setText(output);
    }

    public String computeCipher(String input) {
        if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton) {
            return computeRotationCipher(rotationPicker.getValue(), input);
        } else if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton2) {
            return computeCodewordCipher(codewordText.getText().toString(), input);
        }
        return "";
    }

    public String computeRotationCipher(int rotation, String input) {
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

    public String computeCodewordCipher(String codeword, String input) {
        int a_num = (int) 'a';
        int A_num = (int) 'A';
        String output = "";
        for (int i = 0; i < input.length(); i++) {
            int rotation = codeword.length() > 0 && codeword.charAt(i % codeword.length()) != ' ' ? (int)input.charAt(i % codeword.length()) : 0;
            int cur = (int) input.charAt(i);
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
