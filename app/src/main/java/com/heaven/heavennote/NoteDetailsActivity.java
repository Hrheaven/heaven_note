package com.heaven.heavennote;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NoteDetailsActivity extends AppCompatActivity {

    private TextView tvTitle, tvContent;
    private NoteDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        tvTitle = findViewById(R.id.tv_note_details_title);
        tvContent = findViewById(R.id.tv_note_details_content);
        databaseHelper = new NoteDatabaseHelper(this);

        // Get note ID from intent
        long noteId = getIntent().getLongExtra("NOTE_ID", -1);
        if (noteId != -1) {
            Note note = databaseHelper.getNoteById(noteId);
            if (note != null) {
                tvTitle.setText(note.getTitle());
                tvContent.setText(note.getContent());
            }
        }
    }
}
