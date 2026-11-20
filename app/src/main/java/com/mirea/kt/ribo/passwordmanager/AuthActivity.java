package com.mirea.kt.ribo.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";

    private EditText etLogin;
    private EditText etPassword;
    private EditText etGroup;
    private Button btnAuth;
    private ProgressBar progressBar;
    private final AuthClient authClient = new AuthClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        etLogin = findViewById(R.id.etAuthLogin);
        etPassword = findViewById(R.id.etAuthPassword);
        etGroup = findViewById(R.id.etAuthGroup);
        btnAuth = findViewById(R.id.btnAuth);
        progressBar = findViewById(R.id.progressBar);

        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToAuthorize();
            }
        });
    }

    private void tryToAuthorize() {
        final String login = etLogin.getText().toString().trim();
        final String password = etPassword.getText().toString();
        final String group = etGroup.getText().toString().trim();

        if (login.isEmpty() || password.isEmpty() || group.isEmpty()) {
            showMessage(R.string.error_empty_auth_fields);
            return;
        }

        Log.d(TAG, "Authorization request, login: " + login + ", group: " + group);
        setLoading(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final AuthResult result = authClient.login(login, password, group);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleResult(result, group);
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, "Network error", e);
                    showErrorFromThread(R.string.error_network);
                } catch (JSONException e) {
                    Log.e(TAG, "Wrong server response", e);
                    showErrorFromThread(R.string.error_server_response);
                }
            }
        }).start();
    }

    private void handleResult(AuthResult result, String group) {
        setLoading(false);
        if (result.isSuccess()) {
            Log.d(TAG, "Authorization success, variant: " + result.getVariant());
            Intent intent = new Intent(this, TaskInfoActivity.class);
            intent.putExtra(TaskInfoActivity.EXTRA_TASK, result.getTask());
            intent.putExtra(TaskInfoActivity.EXTRA_GROUP, group);
            startActivity(intent);
            finish();
        } else {
            Log.d(TAG, "Authorization failed, code: " + result.getResultCode());
            showMessage(R.string.error_invalid_params);
        }
    }

    private void showErrorFromThread(final int messageId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLoading(false);
                showMessage(messageId);
            }
        });
    }

    private void setLoading(boolean loading) {
        btnAuth.setEnabled(!loading);
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showMessage(int messageId) {
        Toast.makeText(this, getString(messageId), Toast.LENGTH_LONG).show();
    }
}
