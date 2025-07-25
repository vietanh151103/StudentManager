package com.example.studentmanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class AddClassActivity extends AppCompatActivity {
    private EditText etName, etDescription;
    private Button btnSave;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        etName = findViewById(R.id.et_class_name);
        etDescription = findViewById(R.id.et_class_description);
        btnSave = findViewById(R.id.btn_save_class);
        dbHelper = new DatabaseHelper(this);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            if (!name.isEmpty() && !description.isEmpty()) {
                dbHelper.addClass(name, description);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}