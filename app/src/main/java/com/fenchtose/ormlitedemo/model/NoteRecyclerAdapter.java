package com.fenchtose.ormlitedemo.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fenchtose.ormlitedemo.R;
import com.fenchtose.ormlitedemo.utils.DateUtils;
import com.fenchtose.ormlitedemo.utils.NoteCallback;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jay Rambhia on 05/04/15.
 */
public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {

    private int TYPE_NOTE = 1;
    private int TYPE_HEADER = 0;
    private int TYPE_FOOTER = -1;

    private List<Note> notes;
    private Context context;

    private NoteCallback clicklisternCallback;

    public NoteRecyclerAdapter(Context ctx, @NonNull List<Note> notes) {
        context = ctx;
        this.notes = notes;
    }

    @Override
    public NoteRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NOTE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout, parent, false);
            return new ViewHolder(v, viewType);

        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_header_view_layout, parent, false);
            return new ViewHolder(v, viewType);
        } else if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_footer_view_layout, parent, false);
            return new ViewHolder(v, viewType);
        } else {
            throw new RuntimeException("Invalid viewType: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(NoteRecyclerAdapter.ViewHolder viewHolder, final int pos) {
        if (isPositionHeader(pos) || isPositionFooter(pos)) {
            return;
        }

        final Note note = notes.get(pos-1);

        viewHolder.titleView.setText(note.getTitle());
        viewHolder.messageView.setText(note.getMessage());

        Date noteStamp = note.getCreated_ts();
        String time_text = "Long Ago";

        if (noteStamp != null) {
            long diff = DateUtils.getCurrentDiff(noteStamp, TimeUnit.MINUTES);
            if (diff < 5) {
                time_text = "Just Now";
            } else if (diff >= 5 && diff < 60) {
                time_text = String.valueOf(diff) + " mins";
            } else if (diff > 60 && diff < 1440) {
                time_text = String.valueOf(diff/60) + " hrs";
            } else if (diff >= 1440 && diff < 2880) {
                time_text = "Yesterday";
            } else {
                time_text = DateUtils.getDateString(noteStamp, "d-MMM-yyyy");
            }
        }

        viewHolder.timeView.setText(time_text);

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicklisternCallback != null) {

                    if (note.isSelected()) {
                        note.setSelected(false);
                        clicklisternCallback.invoke(note.id, pos, false);
                        notifyItemChanged(pos);
                        return;
                    }

                    int prev_pos = -1;
                    for(int i=0; i<notes.size(); i++) {
                        if (notes.get(i).isSelected()) {
                            notes.get(i).setSelected(false);
                            prev_pos = i;
                            break;
                        }
                    }

                    note.setSelected(true);
                    clicklisternCallback.invoke(note.id, pos, true);
                    if (prev_pos != -1) {
                        notifyItemChanged(prev_pos+1);
                    }
                    notifyItemChanged(pos);
                }
            }
        });

        if (note.isSelected()) {
            viewHolder.cardView.setCardElevation(12.0f);
        } else {
            viewHolder.cardView.setCardElevation(3.0f);
        }
//        viewHolder.setOn
    }

    @Override
    public int getItemCount() {
        return notes.size()+2;
    }

    public int getBaseItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }
        return TYPE_NOTE;
    }

    //added a method to check if given position is a header
    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private boolean isPositionFooter(int position) {
        return position == getItemCount()-1;
    }

    public void setClicklisternCallback(NoteCallback clicklisternCallback) {
        this.clicklisternCallback = clicklisternCallback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView titleView;
        public TextView messageView;
        public TextView timeView;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == TYPE_NOTE) {
                cardView = (CardView)itemView.findViewById(R.id.note_card_layout);
                titleView = (TextView) itemView.findViewById(R.id.note_title_textview);
                messageView = (TextView) itemView.findViewById(R.id.note_body_textview);
                timeView = (TextView) itemView.findViewById(R.id.time_textview);
            }
        }
    }
}
