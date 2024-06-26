package com.heaven.heavennote;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {

    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> noteList;
    private NoteDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view_notes);
        noteList = new ArrayList<>();
        databaseHelper = new NoteDatabaseHelper(this);

        // Setup RecyclerView with GridLayoutManager
        noteAdapter = new NoteAdapter(this, noteList, this); // Pass 'this' as the listener
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns in one row
        recyclerView.setAdapter(noteAdapter);

        // Load notes from database
        loadNotes();

        // Floating action button click listener
        findViewById(R.id.fab_add_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditNoteActivity.class));
            }
        });
    }

    // Method to load notes from database
    private void loadNotes() {
        noteList.clear();
        noteList.addAll(databaseHelper.getAllNotes());
        noteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNoteClick(int position) {
        // Handle click on a note item
        Note clickedNote = noteList.get(position);
        Intent intent = new Intent(MainActivity.this, NoteDetailsActivity.class);
        intent.putExtra("NOTE_ID", clickedNote.getId());
        startActivity(intent);
    }

    @Override
    public void onNoteLongClick(int position) {
        // Handle long click on a note item
        showPopupMenu(position);
    }

    private void showPopupMenu(final int position) {
        View itemView = recyclerView.getChildAt(position);
        if (itemView != null) {
            PopupMenu popupMenu = new PopupMenu(this, itemView);
            popupMenu.getMenuInflater().inflate(R.menu.note_options_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Note selectedNote = noteList.get(position);
                    int itemId = item.getItemId();

                    if (itemId == R.id.menu_edit) {
                        Intent editIntent = new Intent(MainActivity.this, EditNoteActivity.class);
                        editIntent.putExtra("NOTE_ID", selectedNote.getId());
                        startActivity(editIntent);
                        return true;
                    } else if (itemId == R.id.menu_share) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, selectedNote.getContent());
                        startActivity(Intent.createChooser(shareIntent, "Share Note"));
                        return true;
                    } else if (itemId == R.id.menu_delete) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Delete Note")
                                .setMessage("Are you sure you want to delete this note?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        databaseHelper.deleteNoteById(selectedNote.getId());
                                        loadNotes();
                                        Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            popupMenu.show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning to this activity
        loadNotes();
    }

    @Override
    public void onBackPressed() {
        new android.app.AlertDialog.Builder(MainActivity.this)
                .setTitle("Exit APP!")
                .setMessage("Do you Really want to Exit?")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // If the user clicked "No", dismiss the dialog and do nothing
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // If the user clicked "Yes", close the activity and exit the app
                        dialog.dismiss();
                        finishAndRemoveTask();
                    }
                })
                .show();
    }
}
