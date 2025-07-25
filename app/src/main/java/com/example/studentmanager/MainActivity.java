package com.example.studentmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private Spinner spinnerChoice;
    private ListView listView;
    private SearchView searchView;
    private Button btnDelete;
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private Set<Integer> selectedPositions = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerChoice = findViewById(R.id.spinner_choice);
        listView = findViewById(R.id.list_view);
        searchView = findViewById(R.id.search_view);
        btnDelete = findViewById(R.id.btn_delete);
        dbHelper = new DatabaseHelper(this);

        if (spinnerChoice == null || listView == null || searchView == null || btnDelete == null) {
            return;
        }

        spinnerChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPositions.clear();
                if (position == 0) {
                    loadStudents();
                } else {
                    loadClasses();
                }
                searchView.setQuery("", false);
                searchView.clearFocus();
                updateDeleteButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinnerChoice.getSelectedItemPosition() == 0) {
                    startActivity(new Intent(MainActivity.this, AddStudentActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, AddClassActivity.class));
                }
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (!selectedPositions.isEmpty()) {
                if (spinnerChoice.getSelectedItemPosition() == 0) {
                    List<Student> students = dbHelper.getAllStudents();
                    for (int position : selectedPositions) {
                        if (position < students.size()) {
                            dbHelper.deleteStudent(students.get(position).getId());
                        }
                    }
                    loadStudents();
                    Toast.makeText(MainActivity.this, "Đã xóa " + selectedPositions.size() + " sinh viên", Toast.LENGTH_SHORT).show();
                } else {
                    List<Class> classes = dbHelper.getAllClasses();
                    for (int position : selectedPositions) {
                        if (position < classes.size()) {
                            dbHelper.deleteClass(classes.get(position).getId());
                        }
                    }
                    loadClasses();
                    Toast.makeText(MainActivity.this, "Đã xóa " + selectedPositions.size() + " lớp", Toast.LENGTH_SHORT).show();
                }
                selectedPositions.clear();
                updateDeleteButton();
            }
        });

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listView.isItemChecked(position)) {
                    selectedPositions.add(position);
                } else {
                    selectedPositions.remove(position);
                }
                updateDeleteButton();
                if (!selectedPositions.contains(position)) {
                    if (spinnerChoice.getSelectedItemPosition() == 0) {
                        List<Student> students = dbHelper.getAllStudents();
                        if (position < students.size()) {
                            Intent intent = new Intent(MainActivity.this, StudentDetailsActivity.class);
                            intent.putExtra("student_id", students.get(position).getId());
                            startActivity(intent);
                        }
                    } else {
                        List<Class> classes = dbHelper.getAllClasses();
                        if (position < classes.size()) {
                            Intent intent = new Intent(MainActivity.this, ClassDetailsActivity.class);
                            intent.putExtra("class_id", classes.get(position).getId());
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }

    private void performSearch(String query) {
        if (spinnerChoice.getSelectedItemPosition() == 0) { // Chỉ áp dụng cho sinh viên
            if (query.startsWith("name:")) {
                filterStudentsByName(query.replace("name:", "").trim());
            } else if (query.startsWith("hometown:")) {
                filterStudentsByHometown(query.replace("hometown:", "").trim());
            } else if (query.startsWith("class:")) {
                filterStudentsByClass(query.replace("class:", "").trim());
            } else {
                filterStudentsByName(query); // Mặc định tìm theo tên nếu không có tiền tố
            }
        } else {
            filterClasses(query); // Tìm kiếm lớp
        }
    }

    private void updateDeleteButton() {
        btnDelete.setEnabled(!selectedPositions.isEmpty());
        btnDelete.setText("Xóa (" + selectedPositions.size() + ")");
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectedPositions.clear();
        if (spinnerChoice.getSelectedItemPosition() == 0) {
            loadStudents();
        } else {
            loadClasses();
        }
        updateDeleteButton();
    }

    private void loadStudents() {
        List<Student> students = dbHelper.getAllStudents();
        List<String> studentNames = new ArrayList<>();
        for (Student student : students) {
            studentNames.add(student.getName());
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, studentNames);
        listView.setAdapter(adapter);
    }

    private void loadClasses() {
        List<Class> classes = dbHelper.getAllClasses();
        List<String> classNames = new ArrayList<>();
        for (Class cls : classes) {
            classNames.add(cls.getName());
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, classNames);
        listView.setAdapter(adapter);
    }

    private void filterStudentsByName(String query) {
        List<Student> students = dbHelper.getAllStudents();
        List<String> filteredNames = new ArrayList<>();
        for (Student student : students) {
            if (student.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredNames.add(student.getName());
            }
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, filteredNames);
        listView.setAdapter(adapter);
    }

    private void filterStudentsByHometown(String query) {
        List<Student> students = dbHelper.getAllStudents();
        List<String> filteredNames = new ArrayList<>();
        for (Student student : students) {
            if (student.getHometown().toLowerCase().contains(query.toLowerCase())) {
                filteredNames.add(student.getName() + " (" + student.getHometown() + ")");
            }
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, filteredNames);
        listView.setAdapter(adapter);
    }

    private void filterStudentsByClass(String query) {
        List<Class> classes = dbHelper.getAllClasses();
        List<String> filteredNames = new ArrayList<>();
        for (Class cls : classes) {
            if (cls.getName().toLowerCase().contains(query.toLowerCase())) {
                List<Student> students = dbHelper.getStudentsForClass(cls.getId());
                for (Student student : students) {
                    filteredNames.add(student.getName() + " (Lớp: " + cls.getName() + ")");
                }
            }
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, filteredNames);
        listView.setAdapter(adapter);
    }

    private void filterClasses(String query) {
        List<Class> classes = dbHelper.getAllClasses();
        List<String> filteredNames = new ArrayList<>();
        for (Class cls : classes) {
            if (cls.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredNames.add(cls.getName());
            }
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, filteredNames);
        listView.setAdapter(adapter);
    }
}