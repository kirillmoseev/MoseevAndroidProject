package com.mirea.kt.ribo.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TaskInfoActivity extends AppCompatActivity {

    public static final String EXTRA_TASK = "task";
    public static final String EXTRA_GROUP = "group";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);

        TextView tvTask = findViewById(R.id.tvTask);
        TextView tvAuthor = findViewById(R.id.tvAuthor);
        TextView tvGroup = findViewById(R.id.tvGroup);
        Button btnContinue = findViewById(R.id.btnContinue);

        tvTask.setText(getIntent().getStringExtra(EXTRA_TASK));
        tvAuthor.setText(getString(R.string.author_name));
        tvGroup.setText(getIntent().getStringExtra(EXTRA_GROUP));

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TaskInfoActivity.this, MasterPasswordActivity.class));
                finish();
            }
        });
    }
}
