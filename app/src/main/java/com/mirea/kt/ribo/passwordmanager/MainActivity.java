package com.mirea.kt.ribo.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!CryptoHelper.hasKey()) {
            startActivity(new Intent(this, MasterPasswordActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        TextView tvBanks = findViewById(R.id.tvBanks);
        TextView tvMail = findViewById(R.id.tvMail);
        TextView tvSocial = findViewById(R.id.tvSocial);
        TextView tvOther = findViewById(R.id.tvOther);
        Button btnGenerator = findViewById(R.id.btnGenerator);

        Button btnLock = findViewById(R.id.btnLock);
        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MasterPasswordActivity.lock(MainActivity.this);
            }
        });

        tvBanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory(getString(R.string.category_banks));
            }
        });
        tvMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory(getString(R.string.category_mail));
            }
        });
        tvSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory(getString(R.string.category_social));
            }
        });
        tvOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategory(getString(R.string.category_other));
            }
        });

        btnGenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GeneratorActivity.class));
            }
        });
    }

    private void openCategory(String name) {
        Intent intent = new Intent(this, EntriesActivity.class);
        intent.putExtra("category", name);
        startActivity(intent);
    }
}
