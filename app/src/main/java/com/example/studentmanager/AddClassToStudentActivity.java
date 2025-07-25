package com.example.studentmanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.content.Intent;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.ArrayList;

public class AddClassToStudentActivity extends AppCompatActivity {
    private ListView lvAvailableClasses;
    private DatabaseHelper dbHelper;
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class_to_student);

        lvAvailableClasses = findViewById(R.id.lv_available_classes);
        dbHelper = new DatabaseHelper(this);
        studentId = getIntent().getIntExtra("student_id", -1);

        loadAvailableClasses();

        lvAvailableClasses.setOnItemClickListener((parent, view, position, id) -> {
            List<Class> classes = dbHelper.getAllClasses();
            if (position < classes.size()) {
                int classId = classes.get(position).getId();
                dbHelper.addStudentToClass(studentId, classId);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void loadAvailableClasses() {
        List<Class> classes = dbHelper.getAllClasses();
        List<String> classNames = new ArrayList<>();
        for (Class cls : classes) {
            classNames.add(cls.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, classNames);
        lvAvailableClasses.setAdapter(adapter);
    }
}