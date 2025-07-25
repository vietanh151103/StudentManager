package com.example.studentmanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;

public class EditClassActivity extends AppCompatActivity {
    private EditText etName, etDescription;
    private Button btnSave;
    private DatabaseHelper dbHelper;
    private int classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        etName = findViewById(R.id.et_class_name);
        etDescription = findViewById(R.id.et_class_description);
        btnSave = findViewById(R.id.btn_save_class);
        dbHelper = new DatabaseHelper(this);

        classId = getIntent().getIntExtra("class_id", -1);
        loadClassData();

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            if (!name.isEmpty() && !description.isEmpty()) {
                dbHelper.updateClass(classId, name, description);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void loadClassData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CLASSES + " WHERE " +
                DatabaseHelper.COLUMN_CLASS_ID + " = ?", new String[]{String.valueOf(classId)});
        if (cursor.moveToFirst()) {
            etName.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CLASS_NAME)));
            etDescription.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CLASS_DESCRIPTION)));
        }
        cursor.close();
        db.close();
    }
}