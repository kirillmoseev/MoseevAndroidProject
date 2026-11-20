package com.mirea.kt.ribo.passwordmanager;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EntriesActivity extends AppCompatActivity {

    private static final String TAG = "EntriesActivity";
    private String category;
    private DbHelper db;
    private List<Entry> entries;
    private EntryAdapter adapter;
    private ListView listView;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!CryptoHelper.hasKey()) {
            startActivity(new Intent(this, MasterPasswordActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_entries);

        category = getIntent().getStringExtra("category");
        db = new DbHelper(this);

        TextView tvCategory = findViewById(R.id.tvCategory);
        tvEmpty = findViewById(R.id.tvEmpty);
        listView = findViewById(R.id.listView);
        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnBack = findViewById(R.id.btnBack);

        Button btnLock = findViewById(R.id.btnLock);
        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MasterPasswordActivity.lock(EntriesActivity.this);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvCategory.setText(category);

        entries = new ArrayList<>();
        adapter = new EntryAdapter(this, entries);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEntry(entries.get(position));
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });

        loadEntries();
    }

    private void loadEntries() {
        entries.clear();
        entries.addAll(db.getEntries(category));
        adapter.notifyDataSetChanged();

        if (entries.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add, null);
        final EditText etService = dialogView.findViewById(R.id.etService);
        final EditText etLogin = dialogView.findViewById(R.id.etLogin);
        final EditText etPass = dialogView.findViewById(R.id.etPass);
        Button btnGen = dialogView.findViewById(R.id.btnGen);

        btnGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPass.setText(PasswordGenerator.generate(14, true, true, true));
            }
        });

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_new_entry))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.button_save), null)
                .setNegativeButton(getString(R.string.button_cancel), null)
                .create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String service = etService.getText().toString().trim();
                String login = etLogin.getText().toString().trim();
                String password = etPass.getText().toString();

                if (service.isEmpty() || login.isEmpty() || password.isEmpty()) {
                    Toast.makeText(EntriesActivity.this, getString(R.string.error_fill_all), Toast.LENGTH_SHORT).show();
                    return;
                }

                String encrypted = CryptoHelper.encrypt(password);
                if (encrypted == null) {
                    Toast.makeText(EntriesActivity.this, getString(R.string.error_encrypt), Toast.LENGTH_SHORT).show();
                    return;
                }

                db.addEntry(category, service, login, encrypted);
                Log.d(TAG, "Entry added to category: " + category);
                loadEntries();
                dialog.dismiss();
            }
        });
    }

    private void showEntry(final Entry entry) {
        final String password = CryptoHelper.decrypt(entry.password);
        if (password == null) {
            Toast.makeText(this, getString(R.string.error_decrypt), Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(entry.service)
                .setMessage(getString(R.string.entry_details, entry.login, password))
                .setPositiveButton(getString(R.string.button_copy), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        clipboard.setPrimaryClip(ClipData.newPlainText("password", password));
                        Toast.makeText(EntriesActivity.this, getString(R.string.message_password_copied), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.button_delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmDelete(entry);
                    }
                })
                .setNeutralButton(getString(R.string.button_close), null)
                .show();
    }

    private void confirmDelete(final Entry entry) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_delete_title))
                .setMessage(entry.service)
                .setPositiveButton(getString(R.string.button_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteEntry(entry.id);
                        Log.d(TAG, "Entry deleted, id: " + entry.id);
                        loadEntries();
                    }
                })
                .setNegativeButton(getString(R.string.button_no), null)
                .show();
    }
}
