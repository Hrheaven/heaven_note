package com.heaven.heavennote;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class EditNoteActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextContent;
    private Spinner spinnerTextAlignment, spinnerTextStyle, spinnerBulletList;
    private CardView buttonSave;

    private NoteDatabaseHelper databaseHelper;
    private Note currentNote;
    private String currentBullet = "Normal";
    private int lineNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        // Initialize views
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextContent = findViewById(R.id.edit_text_content);
        spinnerTextAlignment = findViewById(R.id.spinner_text_alignment);
        spinnerTextStyle = findViewById(R.id.text_style_options);
        spinnerBulletList = findViewById(R.id.spinner_bullet_list);
        buttonSave = findViewById(R.id.button_save_text);

        // Initialize database helper
        databaseHelper = new NoteDatabaseHelper(this);

        // Load note data if available
        long noteId = getIntent().getLongExtra("NOTE_ID", -1);
        if (noteId != -1) {
            loadNoteData(noteId);
        }

        // Set up spinners
        setupSpinners();

        // Save button click listener
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        // Watch for text changes to apply bullet list options
        editTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Handle bullet list options here if needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && s.charAt(s.length() - 1) == '\n') {
                    int start = editTextContent.getSelectionStart();
                    applyBulletListOption(currentBullet, start);
                }
            }
        });
    }

    private void loadNoteData(long noteId) {
        currentNote = databaseHelper.getNoteById(noteId);
        if (currentNote != null) {
            editTextTitle.setText(currentNote.getTitle());
            editTextContent.setText(currentNote.getContent());
        }
    }

    private void setupSpinners() {
        // Set up text alignment spinner
        ArrayAdapter<CharSequence> alignmentAdapter = ArrayAdapter.createFromResource(this,
                R.array.text_alignment_options, android.R.layout.simple_spinner_item);
        alignmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTextAlignment.setAdapter(alignmentAdapter);
        spinnerTextAlignment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAlignment = parent.getItemAtPosition(position).toString();
                applyTextAlignment(selectedAlignment);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set up text style spinner
        ArrayAdapter<CharSequence> styleAdapter = ArrayAdapter.createFromResource(this,
                R.array.text_style_options, android.R.layout.simple_spinner_item);
        styleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTextStyle.setAdapter(styleAdapter);
        spinnerTextStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStyle = parent.getItemAtPosition(position).toString();
                applyTextStyle(selectedStyle);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set up bullet list spinner
        ArrayAdapter<CharSequence> bulletAdapter = ArrayAdapter.createFromResource(this,
                R.array.bullet_list_options, android.R.layout.simple_spinner_item);
        bulletAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBulletList.setAdapter(bulletAdapter);
        spinnerBulletList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentBullet = parent.getItemAtPosition(position).toString();
                if (currentBullet.equals("Normal")) {
                    lineNumber = 1;
                }
                int start = editTextContent.getSelectionStart();
                applyBulletListOption(currentBullet, start);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void applyTextAlignment(String alignment) {
        int gravity = Gravity.START; // Default to start (left) alignment

        switch (alignment) {
            case "Left":
                gravity = Gravity.START;
                break;
            case "Center":
                gravity = Gravity.CENTER;
                break;
            case "Right":
                gravity = Gravity.END;
                break;
        }

        editTextContent.setGravity(gravity);
    }

    private void applyTextStyle(String style) {
        int start = editTextContent.getSelectionStart();
        int end = editTextContent.getSelectionEnd();
        Editable text = editTextContent.getText();

        switch (style) {
            case "Bold":
                toggleSpan(text, start, end, new StyleSpan(Typeface.BOLD));
                break;
            case "Italic":
                toggleSpan(text, start, end, new StyleSpan(Typeface.ITALIC));
                break;
            case "Underline":
                toggleSpan(text, start, end, new UnderlineSpan());
                break;
        }
    }

    private void toggleSpan(Editable text, int start, int end, Object span) {
        Object[] spans = text.getSpans(start, end, span.getClass());
        boolean isSpanActive = false;

        for (Object s : spans) {
            if (text.getSpanStart(s) >= start && text.getSpanEnd(s) <= end) {
                text.removeSpan(s);
                isSpanActive = true;
            }
        }

        if (!isSpanActive) {
            text.setSpan(span, start, end, Editable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void applyBulletListOption(String option, int start) {
        switch (option) {
            case "• Bullet":
                editTextContent.getText().insert(start, "• ");
                break;
            case "1. Numbering":
                editTextContent.getText().insert(start, lineNumber + ". ");
                lineNumber++;
                break;
            case "> Greater Than":
                editTextContent.getText().insert(start, "> ");
                break;
            case "- Dash":
                editTextContent.getText().insert(start, "- ");
                break;
            case "Normal":
                // Do nothing, normal text input
                break;
        }
    }

    private void saveNote() {
        if (currentNote == null) {
            currentNote = new Note();
        }
        currentNote.setTitle(editTextTitle.getText().toString());
        currentNote.setContent(editTextContent.getText().toString());

        if (currentNote.getId() == 0) {
            databaseHelper.addNote(currentNote);
        } else {

            databaseHelper.updateNote(currentNote);
        }

        // Notify user and close the activity
        Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
