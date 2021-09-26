package com.example.securenotes;

import android.app.Application;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NoteAdapter  extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{

    private List<Note> notes = new ArrayList<>();
    private OnItemClickListener listener;
    NoteViewModel noteViewModel;
    private List<Note> notesSource;
    Timer timer;


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.note_item , parent , false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.textTitle.setText(note.getTitle());
        holder.textSubtitle.setText(note.getSubtitle());
        holder.textdate.setText(note.getDateTime());
        holder.textpriority.setText(String.valueOf(note.getPriority()));
        if (note.getColor() != null){
           holder.cardViewNote.setCardBackgroundColor(Color.parseColor(note.getColor()));
        } else {
            holder.cardViewNote.setCardBackgroundColor(Color.parseColor("#333333"));
        }
        if(note.getColor().equals("#FDBE38") ){
            holder.textSubtitle.setTextColor(Color.parseColor("#FFFFFFFF"));
            holder.textdate.setTextColor(Color.parseColor("#FFFFFFFF"));
            holder.textpriority.setTextColor(Color.parseColor("#FFFFFFFF"));
        }
        if(note.getImagePath() != null){
            holder.imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
            holder.imageNote.setVisibility(View.VISIBLE);
        }else{
            holder.imageNote.setVisibility(View.GONE);
        }
    }

    public void setNotes(List<Note> notes){
        this.notes = notes;
        this.notesSource = notes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textTitle,textSubtitle,textdate,textpriority;
        ImageView menuImg;
        CardView cardViewNote;
        RoundedImageView imageNote;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textdate = itemView.findViewById(R.id.textDateTime2);
            textpriority = itemView.findViewById(R.id.textPriority);
            menuImg = itemView.findViewById(R.id.popupMenuImg);
            cardViewNote = itemView.findViewById(R.id.noteCardView);
            imageNote = itemView.findViewById(R.id.imageNote);
            noteViewModel = ViewModelProviders.of((FragmentActivity) itemView.getContext()).get(NoteViewModel.class);
            menuImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(itemView.getContext() , itemView);
                    popupMenu.inflate(R.menu.popup_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        int pos = getAdapterPosition();
                        Note note = notes.get(pos);
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.popup_Archive:
                                    break;
                                case R.id.popUp_delete:
                                    System.out.println("note title is : " + note.getTitle());
                                    noteViewModel.delete(note);
                                    break;
                                case R.id.popUp_MakeACopy:
                                    break;
                                case R.id.popup_changeColor:
                                    break;

                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null && position != RecyclerView.NO_POSITION)
                         listener.onItemClick(notes.get(position));
                }
            });

        }
    }


    public interface OnItemClickListener{
        void onItemClick(Note note);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void searchNotes(final String searchKeyword){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKeyword.trim().isEmpty()){
                    notes = notesSource;
                } else {
                    ArrayList<Note> temp = new ArrayList<>();
                    for (Note note : notesSource) {
                        if(note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                            || note.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                            || note.getDescription().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(note);
                        }
                    }
                    notes = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        },500);
    }

    public void cancelTimer(){
        if(timer!=null){
            timer.cancel();
        }
    }
}
