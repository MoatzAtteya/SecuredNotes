package com.example.securenotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.securenotes.databinding.ActivityAddNewNoteBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEditNewNote extends AppCompatActivity {

    String register_date;
    ActivityAddNewNoteBinding binding;
    public static final String EXTRA_ID = "com.example.securenotes.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.securenotes.EXTRA_TITLE";
    public static final String EXTRA_DESC = "com.example.securenotes.EXTRA_DESC";
    public static final String EXTRA_DATE = "com.example.securenotes.EXTRA_DATE";
    public static final String EXTRA_PRIORITY = "com.example.securenotes.EXTRA_PRIORITY";
    public static final String EXTRA_SUBTITLE = "com.example.securenotes.EXTRA_SUBTITLE";
    public static final String EXTRA_COLOR = "com.example.securenotes.EXTRA_COLOR";
    public static final String EXTRA_IMAGE_PATH = "com.example.securenotes.EXTRA_IMAGE_PATH";
    public static final String EXTRA_WEB_LINk = "com.example.securenotes.EXTRA_WEB_LINk";


    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    private String selectedColorNote;
    private String selectedImagePath;
    private AlertDialog dialogAddUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        binding = ActivityAddNewNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setCurrentTimeDate();

        binding.prirityNumberPicker.setMinValue(1);
        binding.prirityNumberPicker.setMaxValue(10);
        binding.textDateTime.setText(register_date.toString());
        selectedColorNote = "#333333";


        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)){
            binding.inputNoteTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            binding.inputNoteSubtitle.setText(intent.getStringExtra(EXTRA_SUBTITLE));
            binding.inputeNoteContent.setText(intent.getStringExtra(EXTRA_DESC));
            binding.textDateTime.setText(intent.getStringExtra(EXTRA_DATE));
            binding.prirityNumberPicker.setValue(intent.getIntExtra(EXTRA_PRIORITY , 1));
            if (!intent.getStringExtra(EXTRA_IMAGE_PATH).toString().equals("empty")){
                binding.imageNote1.setImageBitmap(BitmapFactory.decodeFile(intent.getStringExtra(EXTRA_IMAGE_PATH)));
                binding.imageNote1.setVisibility(View.VISIBLE);
                binding.imageImgRomve.setVisibility(View.VISIBLE);
                selectedImagePath = intent.getStringExtra(EXTRA_IMAGE_PATH);
            }
            if (!intent.getStringExtra(EXTRA_WEB_LINk).equals("empty")){
                binding.textWebUrl.setText(intent.getStringExtra(EXTRA_WEB_LINk));
                binding.layoutWebURL.setVisibility(View.VISIBLE);
            }
            if (binding.textWebUrl.getText().toString().equals("")){
                binding.layoutWebURL.setVisibility(View.GONE);
            }
            selectedColorNote = intent.getStringExtra(EXTRA_COLOR);
        }

        binding.doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.imageRemoveURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textWebUrl.setText(null);
                binding.layoutWebURL.setVisibility(View.GONE);
            }
        });

        binding.imageImgRomve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.imageNote1.setImageBitmap(null);
                binding.imageNote1.setVisibility(View.GONE);
                binding.imageImgRomve.setVisibility(View.GONE);
                selectedImagePath = "empty";
            }
        });

        if(getIntent().getBooleanExtra("isFromQucikAction" , false)){
            String type = getIntent().getStringExtra("qucikActionTyype");
            if(type != null){
                if(type.equals("image")){
                    selectedImagePath = getIntent().getStringExtra("imagePath");
                    binding.imageNote1.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
                    binding.imageNote1.setVisibility(View.VISIBLE);
                    binding.imageImgRomve.setVisibility(View.VISIBLE);
                }else if(type.equals("URL")){
                    binding.textWebUrl.setText(getIntent().getStringExtra("URL"));
                    binding.layoutWebURL.setVisibility(View.VISIBLE);
                }
            }
        }


        initMiscellaneois();
        setViewSubtitleIndicatorColor();

    }

    public void initMiscellaneois() {
        final LinearLayout linearLayoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutMiscellaneous);
        linearLayoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        final ImageView imageView1 = linearLayoutMiscellaneous.findViewById(R.id.imagecolor1);
        final ImageView imageView2 = linearLayoutMiscellaneous.findViewById(R.id.imagecolor2);
        final ImageView imageView3 = linearLayoutMiscellaneous.findViewById(R.id.imagecolor3);
        final ImageView imageView4 = linearLayoutMiscellaneous.findViewById(R.id.imagecolor4);
        final ImageView imageView5 = linearLayoutMiscellaneous.findViewById(R.id.imagecolor5);
        final ImageView imageView6 = linearLayoutMiscellaneous.findViewById(R.id.imagecolor6);
        final ImageView imageView7 = linearLayoutMiscellaneous.findViewById(R.id.imagecolor7);

        linearLayoutMiscellaneous.findViewById(R.id.imagecolor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColorNote = "#333333";
                imageView1.setImageResource(R.drawable.ic_baseline_done_24);
                imageView2.setImageResource(0);
                imageView3.setImageResource(0);
                imageView4.setImageResource(0);
                imageView5.setImageResource(0);
                imageView6.setImageResource(0);
                imageView7.setImageResource(0);
                setViewSubtitleIndicatorColor();
            }
        });
        linearLayoutMiscellaneous.findViewById(R.id.imagecolor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColorNote = "#FDBE38";
                imageView1.setImageResource(0);
                imageView2.setImageResource(R.drawable.ic_baseline_done_24);
                imageView3.setImageResource(0);
                imageView4.setImageResource(0);
                imageView5.setImageResource(0);
                imageView6.setImageResource(0);
                imageView7.setImageResource(0);
                setViewSubtitleIndicatorColor();
            }
        });
        linearLayoutMiscellaneous.findViewById(R.id.imagecolor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColorNote = "#FF4842";
                imageView1.setImageResource(0);
                imageView2.setImageResource(0);
                imageView3.setImageResource(R.drawable.ic_baseline_done_24);
                imageView4.setImageResource(0);
                imageView5.setImageResource(0);
                imageView6.setImageResource(0);
                imageView7.setImageResource(0);
                setViewSubtitleIndicatorColor();
            }
        });
        linearLayoutMiscellaneous.findViewById(R.id.imagecolor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColorNote = "#3A52FC";
                imageView1.setImageResource(0);
                imageView2.setImageResource(0);
                imageView3.setImageResource(0);
                imageView4.setImageResource(R.drawable.ic_baseline_done_24);
                imageView5.setImageResource(0);
                imageView6.setImageResource(0);
                imageView7.setImageResource(0);
                setViewSubtitleIndicatorColor();
            }
        });
        linearLayoutMiscellaneous.findViewById(R.id.imagecolor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColorNote = "#000000";
                imageView1.setImageResource(0);
                imageView2.setImageResource(0);
                imageView3.setImageResource(0);
                imageView4.setImageResource(0);
                imageView5.setImageResource(R.drawable.ic_baseline_done_24);
                imageView6.setImageResource(0);
                imageView7.setImageResource(0);
                setViewSubtitleIndicatorColor();
            }
        });
        linearLayoutMiscellaneous.findViewById(R.id.imagecolor6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColorNote = "#c66900";
                imageView1.setImageResource(0);
                imageView2.setImageResource(0);
                imageView3.setImageResource(0);
                imageView4.setImageResource(0);
                imageView5.setImageResource(0);
                imageView6.setImageResource(R.drawable.ic_baseline_done_24);
                imageView7.setImageResource(0);
                setViewSubtitleIndicatorColor();
            }
        });
        linearLayoutMiscellaneous.findViewById(R.id.imagecolor7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedColorNote = "#4e342e";
                imageView1.setImageResource(0);
                imageView2.setImageResource(0);
                imageView3.setImageResource(0);
                imageView4.setImageResource(0);
                imageView5.setImageResource(0);
                imageView6.setImageResource(0);
                imageView7.setImageResource(R.drawable.ic_baseline_done_24);
                setViewSubtitleIndicatorColor();
            }
        });
        linearLayoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                )!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            AddEditNewNote.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                } else {
                    selectImage();
                }
            }
        });
        linearLayoutMiscellaneous.findViewById(R.id.layoutAddUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddURLDialoge();
            }
        });
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

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);
        }
    }

    private void saveNote() {
        String title = binding.inputNoteTitle.getText().toString();
        String subtitle = binding.inputNoteSubtitle.getText().toString();
        String description = binding.inputeNoteContent.getText().toString();
        int priority = binding.prirityNumberPicker.getValue();
        if(title.trim().isEmpty() || description.trim().isEmpty()){
            Toast.makeText(this, "Please Insert title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE , title);
        data.putExtra(EXTRA_SUBTITLE , subtitle);
        data.putExtra(EXTRA_DESC , description);
        data.putExtra(EXTRA_PRIORITY , priority);
        data.putExtra(EXTRA_DATE , register_date);
        data.putExtra(EXTRA_COLOR , selectedColorNote);
        if(selectedImagePath == null){
            selectedImagePath = "empty";
        }
        data.putExtra(EXTRA_IMAGE_PATH , selectedImagePath);

        if(binding.layoutWebURL.getVisibility() == View.VISIBLE || binding.textWebUrl.getText().toString()!=null || binding.textWebUrl.getText().toString()!=""){
            data.putExtra(EXTRA_WEB_LINk , binding.textWebUrl.getText().toString());
        }

        int id=getIntent().getIntExtra(EXTRA_ID , -1);
        if(id != -1){
            data.putExtra(EXTRA_ID , id);
        }

        setResult(RESULT_OK,data);
        finish();

    }

    private void setCurrentTimeDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = Calendar.getInstance().getTime();
        register_date=formatter.format(date);
    }

    private void setViewSubtitleIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) binding.viewSubTitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedColorNote));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode== RESULT_OK){
            if(data != null){
                Uri selectedImage = data.getData();
                if (selectedImage != null){
                    try {
                        binding.imageNote1.setImageURI(selectedImage);
                        binding.imageNote1.setVisibility(View.VISIBLE);
                        binding.imageImgRomve.setVisibility(View.VISIBLE);
                        selectedImagePath = getPathFromUri(selectedImage);
                    }catch (Exception e){
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver()
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

    private void showAddURLDialoge(){
        if(dialogAddUrl == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(AddEditNewNote.this);
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
                        Toast.makeText(AddEditNewNote.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    } else if(!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()){
                        Toast.makeText(AddEditNewNote.this, "Enter Valid URL", Toast.LENGTH_SHORT).show();
                    } else {
                        binding.textWebUrl.setText(inputURL.getText().toString());
                        binding.layoutWebURL.setVisibility(View.VISIBLE);
                        dialogAddUrl.dismiss();
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