package com.siddiquiarham.converter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Converter extends AppCompatActivity {

    private EditText decimalEditText;
    private EditText hexEditText;
    private EditText binaryEditText;
    private ToggleButton toggleButton;
    private TextView counter;
    private Timer timer;
    private boolean gameMode;
    private int counterValue;
    private int number;
    private boolean decimalCorrect;
    private boolean hexCorrect;
    private boolean binaryCorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        setEditTexts();
        setToggleButton();
    }

    private void setEditTexts() {
        setDecimalEditText();
        setHexEditText();
        setBinaryEditText();
    }

    private void setDecimalEditText() {
        decimalEditText = (EditText) findViewById(R.id.decimal);
        decimalEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String input = v.getText().toString();
                    try {
                        int decimal = Integer.parseInt(input);
                        String hex = Integer.toHexString(decimal);
                        String binary = Integer.toBinaryString(decimal);
                        if (!gameMode) {
                            hexEditText.setText(hex);
                            binaryEditText.setText(binary);
                            handled = true;
                        } else {
                            if (decimal == number) {
                                decimalCorrect = true;
                            }
                            advanceGame();
                        }
                    } catch (NumberFormatException e) { }
                }
                return handled;
            }
        });
    }

    private void setHexEditText() {
        hexEditText = (EditText) findViewById(R.id.hex);
        hexEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String input = v.getText().toString();
                    try {
                        int decimal = Integer.parseInt(input, 16);
                        String binary = Integer.toBinaryString(decimal);
                        if (!gameMode) {
                            decimalEditText.setText(Integer.toString(decimal));
                            binaryEditText.setText(binary);
                            handled = true;
                        } else {
                            if (decimal == number) {
                                hexCorrect = true;
                            }
                            advanceGame();
                        }
                    } catch (NumberFormatException e) { }
                }
                return handled;
            }
        });
    }

    private void setBinaryEditText() {
        binaryEditText = (EditText) findViewById(R.id.binary);
        binaryEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String input = v.getText().toString();
                    try {
                        int decimal = Integer.parseInt(input, 2);
                        String hex = Integer.toHexString(decimal);
                        if (!gameMode) {
                            decimalEditText.setText(Integer.toString(decimal));
                            hexEditText.setText(hex);
                            handled = true;
                        } else {
                            if (decimal == number) {
                                binaryCorrect = true;
                            }
                            advanceGame();
                        }
                    } catch (NumberFormatException e) { }
                }
                return handled;
            }
        });
    }

    public boolean allCorrect() {
        if (Objects.equals(decimalEditText.getText().toString(), Integer.toString(number))) {
            decimalCorrect = true;
        }
        if (Objects.equals(hexEditText.getText().toString(), Integer.toHexString(number))) {
            hexCorrect = true;
        }
        if (Objects.equals(binaryEditText.getText().toString(), Integer.toBinaryString(number))) {
            binaryCorrect = true;
        }
        return decimalCorrect && hexCorrect && binaryCorrect;
    }

    private void setToggleButton() {
        toggleButton = (ToggleButton) findViewById(R.id.gameMode);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gameMode = true;
                    handleGame();
                } else {
                    gameMode = false;
                    suspendGame();
                }
            }
        });
    }

    private void handleGame() {
        final Random random = new Random();
        timer = new Timer();
        final int gameType = random.nextInt(3);
        number = random.nextInt(255);
        counter = (TextView) findViewById(R.id.counter);
        counterValue = 30;
        clearText();
        switch (gameType) {
            case 0: // decimal
                decimalEditText.setText(Integer.toString(number));
                decimalCorrect = true;
                break;
            case 1: // hexadecimal
                hexEditText.setText(Integer.toHexString(number));
                hexCorrect = true;
                break;
            case 2: // binary
                binaryEditText.setText(Integer.toBinaryString(number));
                binaryCorrect = true;
                break;
        }
        timer.schedule(new CounterTask(this), 0, 1000);
    }

    private void suspendGame() {
        timer.cancel();
        timer.purge();
        counterValue = 30;
        toggleButton.setChecked(false);
        clearText();
    }

    private void clearText() {
        decimalEditText.setText("");
        hexEditText.setText("");
        binaryEditText.setText("");
        counter.setText("");
    }

    public void advanceGame() {
        if (allCorrect()) {
            decimalCorrect = false;
            hexCorrect = false;
            binaryCorrect = false;
            timer.cancel();
            timer.purge();
            handleGame();
        }
    }

    private class CounterTask extends TimerTask {

        private Converter converter;

        private CounterTask(Converter converter) {
            this.converter = converter;
        }

        @Override
        public void run() {
            if (counterValue <= 1) {
                timer.cancel();
                timer.purge();
                converter.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        converter.suspendGame();
                    }
                });
            }
            counterValue--;
            converter.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    counter.setText(Integer.toString(counterValue));
                }
            });
        }
    }
}