package com.heaven.heavennote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context context;
    private List<Note> noteList;
    private OnNoteClickListener onNoteClickListener; // Listener for item click events

    // Constructor
    public NoteAdapter(Context context, List<Note> noteList, OnNoteClickListener onNoteClickListener) {
        this.context = context;
        this.noteList = noteList;
        this.onNoteClickListener = onNoteClickListener;
    }

    // Interface for click events
    public interface OnNoteClickListener {
        void onNoteClick(int position);

        void onNoteLongClick(int position);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_note, parent, false);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.tvTitle.setText(note.getTitle());
        holder.tvContent.setText(note.getContent());

        // Set click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onNoteClickListener != null) {
                    onNoteClickListener.onNoteClick(holder.getAdapterPosition());
                }
            }
        });

        // Set long click listener
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onNoteClickListener != null) {
                    onNoteClickListener.onNoteLongClick(holder.getAdapterPosition());
                }
                return true; // Consume the long click
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    // ViewHolder class
    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent;

        public NoteViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_note_title);
            tvContent = itemView.findViewById(R.id.tv_note_content);
        }
    }
}
