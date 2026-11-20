package com.mirea.kt.ribo.passwordmanager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MasterPasswordActivity extends AppCompatActivity {

    private static final String TAG = "MasterPasswordActivity";
    private static final String CHECK_TEXT = "master_ok";

    private EditText etPassword;
    private EditText etConfirm;
    private TextView tvTitle;
    private TextView tvHint;
    private Button btnOk;
    private SharedPreferences prefs;
    private boolean firstRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_password);

        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);
        tvTitle = findViewById(R.id.tvTitle);
        tvHint = findViewById(R.id.tvHint);
        btnOk = findViewById(R.id.btnOk);

        prefs = getSharedPreferences("settings", MODE_PRIVATE);
        firstRun = prefs.getString("salt", null) == null;

        if (firstRun) {
            tvTitle.setText(getString(R.string.master_create_title));
            tvHint.setText(getString(R.string.master_create_hint));
            etConfirm.setVisibility(View.VISIBLE);
            btnOk.setText(getString(R.string.button_create));
        } else {
            tvTitle.setText(getString(R.string.master_login_title));
            tvHint.setText(getString(R.string.master_enter_hint));
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstRun) {
                    createMasterPassword();
                } else {
                    checkMasterPassword();
                }
            }
        });
    }

    private void createMasterPassword() {
        String password = etPassword.getText().toString();
        String confirm = etConfirm.getText().toString();

        if (password.length() < 6) {
            Toast.makeText(this, getString(R.string.error_password_short), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirm)) {
            Toast.makeText(this, getString(R.string.error_passwords_differ), Toast.LENGTH_SHORT).show();
            return;
        }

        String salt = CryptoHelper.createSalt();
        CryptoHelper.setKey(password, salt);
        String check = CryptoHelper.encrypt(CHECK_TEXT);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("salt", salt);
        editor.putString("check", check);
        editor.apply();

        Log.d(TAG, "Master password created");
        openMain();
    }

    private void checkMasterPassword() {
        String password = etPassword.getText().toString();
        String salt = prefs.getString("salt", "");
        String check = prefs.getString("check", "");

        CryptoHelper.setKey(password, salt);
        String result = CryptoHelper.decrypt(check);

        if (CHECK_TEXT.equals(result)) {
            Log.d(TAG, "Master password accepted");
            openMain();
        } else {
            Log.d(TAG, "Wrong master password");
            CryptoHelper.clearKey();
            Toast.makeText(this, getString(R.string.error_wrong_master), Toast.LENGTH_SHORT).show();
        }
    }

    public static void lock(Activity activity) {
        Log.d(TAG, "Application locked");
        CryptoHelper.clearKey();
        Intent intent = new Intent(activity, MasterPasswordActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    private void openMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
