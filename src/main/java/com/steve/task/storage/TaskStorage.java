package com.steve.task.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.steve.task.Task;

/**
 * Created by Steve Tchatchouang on 10/01/2018
 */

public class TaskStorage {
    private static final String _ID         = "_id";
    private static final String COLUMN_TASK = "task";
    private static final String TABLE_NAME  = "adwa_task";
    private static final String TAG         = TaskStorage.class.getSimpleName();

    private Context            context;
    private TaskSerializer<Task> serializer;
    private DatabaseHelper     databaseHelper;

    public TaskStorage(Context context, String name, TaskSerializer<Task> serializer) {
        this.context = context;
        this.serializer = serializer;
        this.databaseHelper = new DatabaseHelper(context, name);
    }

    public void persist(Task task) throws IOException {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, serializer.serialize(task));
        long id = databaseHelper.getWritableDatabase().insert(TABLE_NAME, null, values);
        task.setStorageId(id);
    }

    public List<Task> getAllTasks() {
        List<Task> list = new LinkedList<>();
        Cursor cursor = databaseHelper.getReadableDatabase().query(
                TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));
                String serTask = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK));
                try {
                    Task task = serializer.deserialize(serTask);
                    task.setContext(context);
                    list.add(task);
                } catch (IOException e) {
                    Log.e(TAG, "getAllTasks: ", e);
                    remove(id);
                }
            }
            cursor.close();
        }
        return list;
    }

    public void remove(long id) {
        databaseHelper.getWritableDatabase().delete(
                TABLE_NAME,
                _ID+"=?",
                new String[]{String.valueOf(id)}
        );
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        private static final int DB_VERSION = 1;


        DatabaseHelper(Context context, String name) {
            super(context, name+"_Tasks", null, DB_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            final String SQL_CREATE_ADWA_TASK_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_TASK + " TEXT NOT NULL" +
                    ")";
            db.execSQL(SQL_CREATE_ADWA_TASK_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
