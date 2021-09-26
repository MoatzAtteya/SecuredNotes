package com.example.securenotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.securenotes.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.List;

import static com.example.securenotes.AddEditNewNote.EXTRA_COLOR;
import static com.example.securenotes.AddEditNewNote.EXTRA_DATE;
import static com.example.securenotes.AddEditNewNote.EXTRA_DESC;
import static com.example.securenotes.AddEditNewNote.EXTRA_ID;
import static com.example.securenotes.AddEditNewNote.EXTRA_IMAGE_PATH;
import static com.example.securenotes.AddEditNewNote.EXTRA_PRIORITY;
import static com.example.securenotes.AddEditNewNote.EXTRA_SUBTITLE;
import static com.example.securenotes.AddEditNewNote.EXTRA_TITLE;
import static com.example.securenotes.AddEditNewNote.EXTRA_WEB_LINk;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_NEW_REQUEST = 1;
    public static final int EDIT_REQUEST = 2;
    public static final int SELECT_IMAGE = 3;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 5;


    NoteViewModel noteViewModel;
    NoteAdapter adapter;
    ActivityMainBinding binding;
    private AlertDialog dialogAddUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.notesRecycleView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        binding.notesRecycleView.setHasFixedSize(true);
        adapter = new NoteAdapter();
        binding.notesRecycleView.setAdapter(adapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.setNotes(notes);
                //System.out.println("color 3 is: " + notes.get(3).getColor());
            }
        });

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = noteViewModel.intentToUpdateNote(note);
                startActivityForResult(intent,EDIT_REQUEST);
            }
        });
        binding.addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , AddEditNewNote.class);
                startActivityForResult(intent , ADD_NEW_REQUEST );
            }
        });
        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.searchNotes(s.toString());
            }
        });
        binding.addImgShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                )!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                } else {
                    selectImage();
                }
            }
        });
        binding.addURLShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddURLDialoge();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_NEW_REQUEST && resultCode == RESULT_OK){
           noteViewModel.addNewNote(data);

        }else if (requestCode == EDIT_REQUEST && resultCode == RESULT_OK){
            noteViewModel.updateNote(data);
        }else if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {
            if(data != null){
                Uri selectedImage = data.getData();
                if (selectedImage != null){
                    try {
                        String selectedImagePath = noteViewModel.getPathFromUri(selectedImage);
                        Intent intent = new Intent(getApplicationContext() , AddEditNewNote.class);
                        intent.putExtra("isFromQucikAction" , true);
                        intent.putExtra("qucikActionTyype" , "image");
                        intent.putExtra("imagePath" , selectedImagePath);
                        startActivityForResult(intent , ADD_NEW_REQUEST);


                    }catch (Exception e){
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

        } else {
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,SELECT_IMAGE);
        }
    }

    private void showAddURLDialoge(){
        if(dialogAddUrl == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url,
                    (ViewGroup) findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);
            dialogAddUrl = builder.create();
            if(dialogAddUrl.getWindow() != null){
                dialogAddUrl.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText inputURL = view.findViewById(R.id.inputUrl);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(inputURL.getText().toString().trim().isEmpty()){
                        Toast.makeText(MainActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    } else if(!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()){
                        Toast.makeText(MainActivity.this, "Enter Valid URL", Toast.LENGTH_SHORT).show();
                    } else {
                        dialogAddUrl.dismiss();
                        Intent intent = new Intent(getApplicationContext() , AddEditNewNote.class);
                        intent.putExtra("isFromQucikAction" , true);
                        intent.putExtra("qucikActionTyype" , "URL");
                        intent.putExtra("URL" , inputURL.getText().toString());
                        startActivityForResult(intent , ADD_NEW_REQUEST);
                    }
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAddUrl.dismiss();
                }
            });
        }
        dialogAddUrl.show();
    }

}