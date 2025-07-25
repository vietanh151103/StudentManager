package com.example.studentmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StudentClassDB";
    private static final int DATABASE_VERSION = 1;

    // Bảng Students
    public static final String TABLE_STUDENTS = "students";
    public static final String COLUMN_STUDENT_ID = "student_id";
    public static final String COLUMN_STUDENT_NAME = "name";
    public static final String COLUMN_STUDENT_DOB = "date_of_birth";
    public static final String COLUMN_STUDENT_HOMETOWN = "hometown";

    // Bảng Classes
    public static final String TABLE_CLASSES = "classes";
    public static final String COLUMN_CLASS_ID = "class_id";
    public static final String COLUMN_CLASS_NAME = "class_name";
    public static final String COLUMN_CLASS_DESCRIPTION = "description";

    // Bảng Student_Class
    public static final String TABLE_STUDENT_CLASS = "student_class";
    public static final String COLUMN_STUDENT_CLASS_STUDENT_ID = "student_id";
    public static final String COLUMN_STUDENT_CLASS_CLASS_ID = "class_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createStudentTable = "CREATE TABLE " + TABLE_STUDENTS + " (" +
                COLUMN_STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_STUDENT_NAME + " TEXT, " +
                COLUMN_STUDENT_DOB + " TEXT, " +
                COLUMN_STUDENT_HOMETOWN + " TEXT)";
        db.execSQL(createStudentTable);

        String createClassTable = "CREATE TABLE " + TABLE_CLASSES + " (" +
                COLUMN_CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CLASS_NAME + " TEXT, " +
                COLUMN_CLASS_DESCRIPTION + " TEXT)";
        db.execSQL(createClassTable);

        String createStudentClassTable = "CREATE TABLE " + TABLE_STUDENT_CLASS + " (" +
                COLUMN_STUDENT_CLASS_STUDENT_ID + " INTEGER, " +
                COLUMN_STUDENT_CLASS_CLASS_ID + " INTEGER, " +
                "PRIMARY KEY (" + COLUMN_STUDENT_CLASS_STUDENT_ID + ", " + COLUMN_STUDENT_CLASS_CLASS_ID + "), " +
                "FOREIGN KEY (" + COLUMN_STUDENT_CLASS_STUDENT_ID + ") REFERENCES " + TABLE_STUDENTS + "(" + COLUMN_STUDENT_ID + "), " +
                "FOREIGN KEY (" + COLUMN_STUDENT_CLASS_CLASS_ID + ") REFERENCES " + TABLE_CLASSES + "(" + COLUMN_CLASS_ID + "))";
        db.execSQL(createStudentClassTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        onCreate(db);
    }

    // Thêm sinh viên
    public long addStudent(String name, String dob, String hometown) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_NAME, name);
        values.put(COLUMN_STUDENT_DOB, dob);
        values.put(COLUMN_STUDENT_HOMETOWN, hometown);
        long id = db.insert(TABLE_STUDENTS, null, values);
        db.close();
        return id;
    }

    // Lấy tất cả sinh viên
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STUDENTS, null);
        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_STUDENT_ID)));
                student.setName(cursor.getString(cursor.getColumnIndex(COLUMN_STUDENT_NAME)));
                student.setDob(cursor.getString(cursor.getColumnIndex(COLUMN_STUDENT_DOB)));
                student.setHometown(cursor.getString(cursor.getColumnIndex(COLUMN_STUDENT_HOMETOWN)));
                students.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return students;
    }

    // Cập nhật sinh viên
    public int updateStudent(int id, String name, String dob, String hometown) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_NAME, name);
        values.put(COLUMN_STUDENT_DOB, dob);
        values.put(COLUMN_STUDENT_HOMETOWN, hometown);
        int rowsAffected = db.update(TABLE_STUDENTS, values, COLUMN_STUDENT_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected;
    }

    // Xóa sinh viên
    public void deleteStudent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENT_CLASS, COLUMN_STUDENT_CLASS_STUDENT_ID + " = ?", new String[]{String.valueOf(id)});
        db.delete(TABLE_STUDENTS, COLUMN_STUDENT_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Thêm lớp
    public long addClass(String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_NAME, name);
        values.put(COLUMN_CLASS_DESCRIPTION, description);
        long id = db.insert(TABLE_CLASSES, null, values);
        db.close();
        return id;
    }

    // Lấy tất cả lớp
    public List<Class> getAllClasses() {
        List<Class> classes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLASSES, null);
        if (cursor.moveToFirst()) {
            do {
                Class cls = new Class();
                cls.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_CLASS_ID)));
                cls.setName(cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_NAME)));
                cls.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_DESCRIPTION)));
                classes.add(cls);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return classes;
    }

    // Cập nhật lớp
    public int updateClass(int id, String name, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_NAME, name);
        values.put(COLUMN_CLASS_DESCRIPTION, description);
        int rowsAffected = db.update(TABLE_CLASSES, values, COLUMN_CLASS_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected;
    }

    // Xóa lớp
    public void deleteClass(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENT_CLASS, COLUMN_STUDENT_CLASS_CLASS_ID + " = ?", new String[]{String.valueOf(id)});
        db.delete(TABLE_CLASSES, COLUMN_CLASS_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Thêm sinh viên vào lớp
    public void addStudentToClass(int studentId, int classId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_CLASS_STUDENT_ID, studentId);
        values.put(COLUMN_STUDENT_CLASS_CLASS_ID, classId);
        db.insert(TABLE_STUDENT_CLASS, null, values);
        db.close();
    }

    // Lấy danh sách lớp của sinh viên
    public List<Class> getClassesForStudent(int studentId) {
        List<Class> classes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT c.* FROM " + TABLE_CLASSES + " c " +
                "JOIN " + TABLE_STUDENT_CLASS + " sc ON c." + COLUMN_CLASS_ID + " = sc." + COLUMN_STUDENT_CLASS_CLASS_ID +
                " WHERE sc." + COLUMN_STUDENT_CLASS_STUDENT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});
        if (cursor.moveToFirst()) {
            do {
                Class cls = new Class();
                cls.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_CLASS_ID)));
                cls.setName(cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_NAME)));
                cls.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_CLASS_DESCRIPTION)));
                classes.add(cls);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return classes;
    }

    // Lấy danh sách sinh viên của lớp
    public List<Student> getStudentsForClass(int classId) {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT s.* FROM " + TABLE_STUDENTS + " s " +
                "JOIN " + TABLE_STUDENT_CLASS + " sc ON s." + COLUMN_STUDENT_ID + " = sc." + COLUMN_STUDENT_CLASS_STUDENT_ID +
                " WHERE sc." + COLUMN_STUDENT_CLASS_CLASS_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(classId)});
        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_STUDENT_ID)));
                student.setName(cursor.getString(cursor.getColumnIndex(COLUMN_STUDENT_NAME)));
                student.setDob(cursor.getString(cursor.getColumnIndex(COLUMN_STUDENT_DOB)));
                student.setHometown(cursor.getString(cursor.getColumnIndex(COLUMN_STUDENT_HOMETOWN)));
                students.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return students;
    }

    // Xóa mối quan hệ sinh viên-lớp
    public void deleteStudentClass(int studentId, int classId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENT_CLASS,
                COLUMN_STUDENT_CLASS_STUDENT_ID + " = ? AND " +
                        COLUMN_STUDENT_CLASS_CLASS_ID + " = ?",
                new String[]{String.valueOf(studentId), String.valueOf(classId)});
        db.close();
    }
}