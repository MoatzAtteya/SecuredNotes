package com.example.securenotes;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.jetbrains.annotations.NotNull;

@Database(entities = {Note.class} , version = 1)
public abstract class NoteDataBase extends RoomDatabase {

    private static NoteDataBase instance;

    public abstract NoteDao noteDao();

    public static synchronized NoteDataBase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDataBase.class , "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull @NotNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(instance).execute();
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void , Void , Void>{
        private NoteDao noteDao;

        private PopulateDBAsyncTask(NoteDataBase db){
            noteDao = db.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Title 1" , "Description 1" , 1 , "19-2-2021" , "subtitle 1" , "#333333" , "empty" , "empty"));
            noteDao.insert(new Note("Title 2" , "Description 2" , 2 , "19-2-2021" , "subtitle 2" , "#333333" , "empty" , "empty"));
            noteDao.insert(new Note("Title 3" , "Description 3" , 3 , "19-2-2021" , "subtitle 3" , "#333333" , "empty" , "empty"));
            return null;
        }
    }
}
