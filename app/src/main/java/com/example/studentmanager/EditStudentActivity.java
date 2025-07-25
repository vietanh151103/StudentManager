package com.example.studentmanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;

public class EditStudentActivity extends AppCompatActivity {
    private EditText etName, etDob, etHometown;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        etName = findViewById(R.id.et_student_name);
        etDob = findViewById(R.id.et_student_dob);
        etHometown = findViewById(R.id.et_student_hometown);
        btnSave = findViewById(R.id.btn_save_student);
        dbHelper = new DatabaseHelper(this);

        studentId = getIntent().getIntExtra("student_id", -1);
        loadStudentData();

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String dob = etDob.getText().toString().trim();
            String hometown = etHometown.getText().toString().trim();
            if (!name.isEmpty() && !dob.isEmpty() && !hometown.isEmpty()) {
                dbHelper.updateStudent(studentId, name, dob, hometown);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void loadStudentData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_STUDENTS + " WHERE " +
                DatabaseHelper.COLUMN_STUDENT_ID + " = ?", new String[]{String.valueOf(studentId)});
        if (cursor.moveToFirst()) {
            etName.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STUDENT_NAME)));
            etDob.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STUDENT_DOB)));
            etHometown.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STUDENT_HOMETOWN)));
        }
        cursor.close();
        db.close();
    }
}