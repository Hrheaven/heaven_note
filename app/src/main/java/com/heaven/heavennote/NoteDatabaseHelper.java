package com.heaven.heavennote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class NoteDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notes_db";
    private static final String TABLE_NOTES = "notes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public NoteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_NOTES = "CREATE TABLE " + TABLE_NOTES +
                "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_CONTENT + " TEXT," +
                COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
        db.execSQL(CREATE_TABLE_NOTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        // Create tables again
        onCreate(db);
    }

    // Add a new note
    public long addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_CONTENT, note.getContent());
        long id = db.insert(TABLE_NOTES, null, values);
        db.close();
        return id;
    }

    // Update an existing note
    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, note.getTitle());
        values.put(COLUMN_CONTENT, note.getContent());
        int rowsAffected = db.update(TABLE_NOTES, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
        return rowsAffected;
    }

    // Delete a note by ID
    public void deleteNoteById(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Get a single note by ID
    public Note getNoteById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NOTES,
                new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_CONTENT, COLUMN_TIMESTAMP},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null
        );

        Note note = null;

        if (cursor != null && cursor.moveToFirst()) {
            note = new Note();
            note.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
            note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
            note.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));

            cursor.close();
        }

        db.close();

        return note;
    }

    // Get all notes
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NOTES,
                new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_CONTENT, COLUMN_TIMESTAMP},
                null,
                null,
                null,
                null,
                COLUMN_TIMESTAMP + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
                notes.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return notes;
    }
}
