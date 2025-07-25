package com.example.studentmanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.content.Intent;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.ArrayList;

public class AddStudentToClassActivity extends AppCompatActivity {
    private ListView lvAvailableStudents;
    private DatabaseHelper dbHelper;
    private int classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student_to_class);

        lvAvailableStudents = findViewById(R.id.lv_available_students);
        dbHelper = new DatabaseHelper(this);
        classId = getIntent().getIntExtra("class_id", -1);

        loadAvailableStudents();

        lvAvailableStudents.setOnItemClickListener((parent, view, position, id) -> {
            List<Student> students = dbHelper.getAllStudents();
            if (position < students.size()) {
                int studentId = students.get(position).getId();
                dbHelper.addStudentToClass(studentId, classId);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void loadAvailableStudents() {
        List<Student> students = dbHelper.getAllStudents();
        List<String> studentNames = new ArrayList<>();
        for (Student student : students) {
            studentNames.add(student.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentNames);
        lvAvailableStudents.setAdapter(adapter);
    }
}