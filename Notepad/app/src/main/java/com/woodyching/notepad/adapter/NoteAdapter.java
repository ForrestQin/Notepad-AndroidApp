package com.woodyching.notepad.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.woodyching.notepad.activity.MainActivity;
import com.woodyching.notepad.activity.NoteActivity;
import com.woodyching.notepad.R;
import com.woodyching.notepad.bean.Note;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Woody on 2016/12/31.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{
    public static NoteAdapter instance ;
    private Context mContext;

    private List<Note> mNoteList;


    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView outline;
        TextView content;
        ImageView noteImage;
        private static final String TAG = "ViewHolder";

        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            noteImage = (ImageView) view.findViewById(R.id.note_image_view);
            outline = (TextView)view.findViewById(R.id.title);
            content = (TextView) view.findViewById(R.id.content);
        }
    }

    public NoteAdapter(List<Note> noteList) {
        mNoteList = noteList;
    }

    private static final String TAG = "NoteAdapter onCreate";
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        instance = this;
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Note note = mNoteList.get(position);

                Intent intent = new Intent(mContext,NoteActivity.class);
                intent.putExtra(NoteActivity.NOTE_TITLE,note.getTitle());
                intent.putExtra(NoteActivity.NOTE_CONTENT,note.getContent());
                intent.putExtra(NoteActivity.NOTE_IMAGE_PATH,note.getImagePath());
                intent.putExtra(NoteActivity.NOTE_ID,note.getId());
                intent.putExtra(NoteActivity.NOTE_POSITION,position);
                intent.putExtra(NoteActivity.NOTE_STATE,1);

                mContext.startActivity(intent);
            }
        });
        


        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.instance);
                builder.setMessage(mContext.getResources().getString(R.string.delect_info));
                builder.setNegativeButton(mContext.getResources().getString(R.string.delete_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int position = holder.getAdapterPosition();
                        Note note = mNoteList.get(position);
                        Intent intent = new Intent(mContext,NoteActivity.class);
                        DataSupport.delete(Note.class,note.getId());
                        MainActivity.instance.delete(position);
                    }
                });
                builder.setPositiveButton(mContext.getResources().getString(R.string.delete_negative),null);
                builder.show();
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(NoteAdapter.ViewHolder holder, int position) {
        Note note = mNoteList.get(position);
        holder.outline.setText(note.getTitle());
        holder.content.setText(note.getContent());

//        for (Note c : mNoteList){
//            if (c.getTitle().equals(" ")){
//                holder.outline.setVisibility(View.GONE);
//            }
//            else {
//                holder.outline.setVisibility(View.VISIBLE);
//            }
//        }

    }

    public Note getNote(int position){
        return mNoteList.get(position);
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }
}
