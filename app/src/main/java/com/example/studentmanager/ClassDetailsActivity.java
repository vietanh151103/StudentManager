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

public class ClassDetailsActivity extends AppCompatActivity {
    private TextView tvClassName, tvDescription;
    private ListView lvStudents;
    private Button btnAddStudent, btnEdit, btnDelete, btnDeleteStudents;
    private DatabaseHelper dbHelper;
    private int classId;
    private Set<Integer> selectedStudentPositions = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        tvClassName = findViewById(R.id.tv_class_name);
        tvDescription = findViewById(R.id.tv_class_description);
        lvStudents = findViewById(R.id.lv_students);
        btnAddStudent = findViewById(R.id.btn_add_student);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
        btnDeleteStudents = findViewById(R.id.btn_delete_students);
        dbHelper = new DatabaseHelper(this);

        classId = getIntent().getIntExtra("class_id", -1);
        loadClassDetails();

        btnAddStudent.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddStudentToClassActivity.class);
            intent.putExtra("class_id", classId);
            startActivity(intent);
        });

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditClassActivity.class);
            intent.putExtra("class_id", classId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            dbHelper.deleteClass(classId);
            Toast.makeText(this, "Đã xóa lớp", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnDeleteStudents.setOnClickListener(v -> {
            if (!selectedStudentPositions.isEmpty()) {
                List<Student> students = dbHelper.getStudentsForClass(classId);
                for (int position : selectedStudentPositions) {
                    if (position < students.size()) {
                        dbHelper.deleteStudentClass(students.get(position).getId(), classId);
                    }
                }
                loadClassDetails();
                Toast.makeText(this, "Đã xóa " + selectedStudentPositions.size() + " sinh viên", Toast.LENGTH_SHORT).show();
                selectedStudentPositions.clear();
                updateDeleteStudentsButton();
            }
        });

        lvStudents.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lvStudents.setOnItemClickListener((parent, view, position, id) -> {
            if (lvStudents.isItemChecked(position)) {
                selectedStudentPositions.add(position);
            } else {
                selectedStudentPositions.remove(position);
            }
            updateDeleteStudentsButton();
        });
    }

    private void updateDeleteStudentsButton() {
        btnDeleteStudents.setEnabled(!selectedStudentPositions.isEmpty());
        btnDeleteStudents.setText("Xóa sinh viên (" + selectedStudentPositions.size() + ")");
    }

    private void loadClassDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CLASSES +
                        " WHERE " + DatabaseHelper.COLUMN_CLASS_ID + " = ?",
                new String[]{String.valueOf(classId)});
        if (cursor.moveToFirst()) {
            tvClassName.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CLASS_NAME)));
            tvDescription.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CLASS_DESCRIPTION)));
        }
        cursor.close();
        db.close();

        List<Student> students = dbHelper.getStudentsForClass(classId);
        List<String> studentNames = new ArrayList<>();
        for (Student student : students) {
            studentNames.add(student.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, studentNames);
        lvStudents.setAdapter(adapter);
    }
}