package com.example.gads2020;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    private ArrayAdapter<NoteInfo> notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(NoteListActivity.this,NoteActivity.class));
            }
        });
        intialiseDisplayContent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        notesAdapter.notifyDataSetChanged();

    }
    private void intialiseDisplayContent() {
        final ListView noteList = findViewById(R.id.list_notes);
        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        notesAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,notes);
        noteList.setAdapter(notesAdapter);

        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NoteListActivity.this,NoteActivity.class);
//                NoteInfo note = (NoteInfo) noteList.getItemAtPosition(position);
                intent.putExtra(NoteActivity.NOTE_POSITION,position);
                startActivity(intent);
            }
        });
    }


}
