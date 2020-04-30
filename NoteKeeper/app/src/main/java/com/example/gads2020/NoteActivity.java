package com.example.gads2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_POSITION = "com.example.gads2020NOTE_POSITION";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo note;
    private boolean isNewNote;
    private int position;
    private Spinner courseSpinner;
    private EditText noteTitle;
    private EditText noteText;
    private int notePosition;
    private boolean isCancelling;

    private NoteActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar myToolBar = findViewById(R.id.myToolBar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setTitle("Note Keeper");




        noteTitle = findViewById(R.id.text_note_title);
        noteText = findViewById(R.id.text_note_text);

        ViewModelProvider viewModelProvider = new ViewModelProvider(getViewModelStore(),ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()));
        viewModel = viewModelProvider.get(NoteActivityViewModel.class);
        viewModel.isNewlyCreated = false;


        if (viewModel.isNewlyCreated && savedInstanceState != null ) {
            viewModel.restoreState(savedInstanceState);
        }

        courseSpinner = findViewById(R.id.spinner_courses);
        List<CourseInfo> coursee = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, coursee);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(courseAdapter);

        getValuesFromIntent();
        saveOriginalNoteValue();
        if (!isNewNote) displayNotes(courseSpinner, noteTitle, noteText);
    }

    private void saveOriginalNoteValue() {
        if(isNewNote){
            return;
        }else{
            viewModel.originalCourseId = note.getCourse().getmCourseId();
            viewModel.originalNoteTitle = note.getTitle();
            viewModel.originalNoteText = note.getText();
        }
    }

    private void displayNotes(Spinner courseSpinner, EditText noteTitle, EditText noteText) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(note.getCourse());
        courseSpinner.setSelection(courseIndex);
        noteTitle.setText(note.getTitle());
        noteText.setText(note.getText());
    }

    private void getValuesFromIntent() {
        Intent intent = getIntent();
        position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);

        isNewNote = position == POSITION_NOT_SET;
        if (isNewNote) {
            createNewNote();


        } else {
            note = DataManager.getInstance().getNotes().get(position);

        }

    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        notePosition = dm.createNewNote();
        note = dm.getNotes().get(notePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.send_as_email:
                sendEmail();
                return true;
            case R.id.action_cancel:
                isCancelling = true;
                finish();
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isCancelling){
            if(isNewNote){
                DataManager.getInstance().removeNote(notePosition);

            }else {
                storePreviousNoteValues();
            }

        }else
        saveNote();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState != null)
            viewModel.saveState(outState);
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(viewModel.originalCourseId);
        note.setCourse(course);
        note.setText(viewModel.originalNoteText);
        note.setTitle(viewModel.originalNoteTitle);
    }

    private void saveNote() {
        note.setCourse((CourseInfo) courseSpinner.getSelectedItem());
        note.setTitle(noteTitle.getText().toString());
        note.setText(noteText.getText().toString());
    }

    private void sendEmail() {

        CourseInfo course = (CourseInfo) courseSpinner.getSelectedItem();
        String subject = noteTitle.getText().toString();
        String text = course.getmTitle() + "\n" + noteText.getText();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }
}
