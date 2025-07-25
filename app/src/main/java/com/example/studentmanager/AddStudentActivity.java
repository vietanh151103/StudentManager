package com.example.studentmanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class AddStudentActivity extends AppCompatActivity {
    private EditText etName, etDob, etHometown;
    private Button btnSave;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        etName = findViewById(R.id.et_student_name);
        etDob = findViewById(R.id.et_student_dob);
        etHometown = findViewById(R.id.et_student_hometown);
        btnSave = findViewById(R.id.btn_save_student);
        dbHelper = new DatabaseHelper(this);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String dob = etDob.getText().toString().trim();
            String hometown = etHometown.getText().toString().trim();
            if (!name.isEmpty() && !dob.isEmpty() && !hometown.isEmpty()) {
                dbHelper.addStudent(name, dob, hometown);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}