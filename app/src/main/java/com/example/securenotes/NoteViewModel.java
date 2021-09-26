package com.example.securenotes;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static com.example.securenotes.AddEditNewNote.EXTRA_COLOR;
import static com.example.securenotes.AddEditNewNote.EXTRA_DATE;
import static com.example.securenotes.AddEditNewNote.EXTRA_DESC;
import static com.example.securenotes.AddEditNewNote.EXTRA_ID;
import static com.example.securenotes.AddEditNewNote.EXTRA_IMAGE_PATH;
import static com.example.securenotes.AddEditNewNote.EXTRA_PRIORITY;
import static com.example.securenotes.AddEditNewNote.EXTRA_SUBTITLE;
import static com.example.securenotes.AddEditNewNote.EXTRA_TITLE;
import static com.example.securenotes.AddEditNewNote.EXTRA_WEB_LINk;



public class NoteViewModel extends AndroidViewModel {
    NoteRepository repository;
    LiveData<List<Note>> allNotes;
    Context context;


    public NoteViewModel(@NonNull @NotNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
        this.context = application.getApplicationContext();
    }

    public void insert(Note note){
        repository.insert(note);
    }

    public void update(Note note){
        repository.update(note);
    }

    public void delete(Note note){
        repository.delete(note);
    }

    public void deleteAllNotes(){
        repository.deleteAllNotes();
    }

    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }

    public Intent intentToUpdateNote(Note note){
        Intent intent = new Intent(context.getApplicationContext() , AddEditNewNote.class);
        intent.putExtra(EXTRA_ID , note.getId());
        intent.putExtra(EXTRA_TITLE , note.getTitle());
        intent.putExtra(EXTRA_SUBTITLE , note.getSubtitle());
        intent.putExtra(EXTRA_DESC , note.getDescription());
        intent.putExtra(EXTRA_PRIORITY , note.getPriority());
        intent.putExtra(EXTRA_DATE , note.getDateTime());
        intent.putExtra(EXTRA_IMAGE_PATH , note.getImagePath());
        intent.putExtra(EXTRA_WEB_LINk , note.getWebLink());
        intent.putExtra(EXTRA_COLOR , note.getColor());
        return intent;
    }

    public void addNewNote(Intent data){
        String title = data.getStringExtra(EXTRA_TITLE);
        String subtitle =data.getStringExtra(EXTRA_SUBTITLE);
        String description =data.getStringExtra(EXTRA_DESC);
        String regDate = data.getStringExtra(EXTRA_DATE);
        int priority = data.getIntExtra(EXTRA_PRIORITY , 1);
        String color = data.getStringExtra(EXTRA_COLOR);
        String imagePath = data.getStringExtra(EXTRA_IMAGE_PATH);
        String webLink = data.getStringExtra(EXTRA_WEB_LINk);
        Note note = new Note(title , description , priority , regDate , subtitle , color , imagePath , webLink);
        insert(note);
        Toast.makeText(context.getApplicationContext(), "Note Saved", Toast.LENGTH_SHORT).show();

    }

    public void updateNote(Intent data){
        int id = data.getIntExtra(EXTRA_ID , -1);
        if (id == -1){
            Toast.makeText(context.getApplicationContext(), "Note Can't be updated", Toast.LENGTH_SHORT).show();
            return;
        }
        String title = data.getStringExtra(EXTRA_TITLE);
        String subtitle =data.getStringExtra(EXTRA_SUBTITLE);
        String description =data.getStringExtra(EXTRA_DESC);
        String regDate = data.getStringExtra(EXTRA_DATE);
        int priority = data.getIntExtra(EXTRA_PRIORITY , 1);
        String color = data.getStringExtra(EXTRA_COLOR);
        String imagePath = data.getStringExtra(EXTRA_IMAGE_PATH);
        String webLink = data.getStringExtra(EXTRA_WEB_LINk);
        Note note = new Note(title , description , priority , regDate , subtitle , color , imagePath , webLink);
        note.setId(id);
        update(note);
        Toast.makeText(context.getApplicationContext(), "Note updated", Toast.LENGTH_SHORT).show();
    }


    public String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor = context.getContentResolver()
                .query(contentUri , null , null , null , null);
        if(cursor == null){
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

}
