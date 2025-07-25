package com.example.studentmanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentDetailsActivity extends AppCompatActivity {
    private TextView tvName, tvDob, tvHometown;
    private ListView lvClasses;
    private Button btnAddClass, btnEdit, btnDelete, btnDeleteClasses;
    private DatabaseHelper dbHelper;
    private int studentId;
    private Set<Integer> selectedClassPositions = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        tvName = findViewById(R.id.tv_student_name);
        tvDob = findViewById(R.id.tv_student_dob);
        tvHometown = findViewById(R.id.tv_student_hometown);
        lvClasses = findViewById(R.id.lv_classes);
        btnAddClass = findViewById(R.id.btn_add_class);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
        btnDeleteClasses = findViewById(R.id.btn_delete_classes);
        dbHelper = new DatabaseHelper(this);

        studentId = getIntent().getIntExtra("student_id", -1);
        loadStudentDetails();

        btnAddClass.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddClassToStudentActivity.class);
            intent.putExtra("student_id", studentId);
            startActivity(intent);
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditStudentActivity.class);
            intent.putExtra("student_id", studentId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            dbHelper.deleteStudent(studentId);
            Toast.makeText(this, "Đã xóa sinh viên", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnDeleteClasses.setOnClickListener(v -> {
            if (!selectedClassPositions.isEmpty()) {
                List<Class> classes = dbHelper.getClassesForStudent(studentId);
                for (int position : selectedClassPositions) {
                    if (position < classes.size()) {
                        dbHelper.deleteStudentClass(studentId, classes.get(position).getId());
                    }
                }
                loadStudentDetails();
                Toast.makeText(this, "Đã xóa " + selectedClassPositions.size() + " lớp", Toast.LENGTH_SHORT).show();
                selectedClassPositions.clear();
                updateDeleteClassesButton();
            }
        });

        lvClasses.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lvClasses.setOnItemClickListener((parent, view, position, id) -> {
            if (lvClasses.isItemChecked(position)) {
                selectedClassPositions.add(position);
            } else {
                selectedClassPositions.remove(position);
            }
            updateDeleteClassesButton();
        });
    }

    private void updateDeleteClassesButton() {
        btnDeleteClasses.setEnabled(!selectedClassPositions.isEmpty());
        btnDeleteClasses.setText("Xóa lớp (" + selectedClassPositions.size() + ")");
    }

    private void loadStudentDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_STUDENTS +
                        " WHERE " + DatabaseHelper.COLUMN_STUDENT_ID + " = ?",
                new String[]{String.valueOf(studentId)});
        if (cursor.moveToFirst()) {
            tvName.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STUDENT_NAME)));
            tvDob.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STUDENT_DOB)));
            tvHometown.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STUDENT_HOMETOWN)));
        }
        cursor.close();
        db.close();

        List<Class> classes = dbHelper.getClassesForStudent(studentId);
        List<String> classNames = new ArrayList<>();
        for (Class cls : classes) {
            classNames.add(cls.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, classNames);
        lvClasses.setAdapter(adapter);
    }
}