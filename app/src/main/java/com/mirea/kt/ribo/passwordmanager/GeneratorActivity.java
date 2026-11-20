package com.mirea.kt.ribo.passwordmanager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GeneratorActivity extends AppCompatActivity {

    private TextView tvResult;
    private TextView tvLength;
    private SeekBar seekLength;
    private CheckBox cbUpper;
    private CheckBox cbDigits;
    private CheckBox cbSymbols;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);

        tvResult = findViewById(R.id.tvResult);
        tvLength = findViewById(R.id.tvLength);
        seekLength = findViewById(R.id.seekLength);
        cbUpper = findViewById(R.id.cbUpper);
        cbDigits = findViewById(R.id.cbDigits);
        cbSymbols = findViewById(R.id.cbSymbols);
        Button btnGenerate = findViewById(R.id.btnGenerate);
        Button btnCopy = findViewById(R.id.btnCopy);
        Button btnBack = findViewById(R.id.btnBack);

        Button btnLock = findViewById(R.id.btnLock);
        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MasterPasswordActivity.lock(GeneratorActivity.this);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        seekLength.setProgress(8);
        updateLengthText();

        seekLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateLengthText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generate();
            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText("password", tvResult.getText().toString()));
                Toast.makeText(GeneratorActivity.this, getString(R.string.message_copied), Toast.LENGTH_SHORT).show();
            }
        });

        generate();
    }

    private int getLength() {
        return seekLength.getProgress() + 8;
    }

    private void updateLengthText() {
        tvLength.setText(getString(R.string.length_label, getLength()));
    }

    private void generate() {
        String password = PasswordGenerator.generate(getLength(),
                cbUpper.isChecked(), cbDigits.isChecked(), cbSymbols.isChecked());
        tvResult.setText(password);
    }
}
