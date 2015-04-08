package com.fenchtose.ormlitedemo.ui;

import android.content.Context;
import android.util.Log;

import com.fenchtose.ormlitedemo.model.Note;
import com.fenchtose.ormlitedemo.model.NoteProvider;
import com.fenchtose.ormlitedemo.model.NoteRecyclerAdapter;
import com.fenchtose.ormlitedemo.utils.NoteCallback;

import java.util.Date;

/**
 * Created by Jay Rambhia on 05/04/15.
 */
public class NoteListPresenterImpl implements NoteListPresenter {

    private Context context;
    private NoteListView noteListView;
    private NoteProvider noteProvider;
    private NoteRecyclerAdapter noteRecyclerAdapter;

    private Note selectedNote;
    private int selectedPosition;

    private String TAG = "NoteListPresenter";

    public NoteListPresenterImpl(NoteListView view, Context context) {
        noteListView = view;
        this.context = context;
        noteProvider = new NoteProvider(this.context);
        noteRecyclerAdapter = new NoteRecyclerAdapter(this.context, noteProvider.getNotes());
        noteRecyclerAdapter.setClicklisternCallback(new RecyclerItemClickCallback());
    }

    @Override
    public void loadInitData() {

        noteListView.setAdapter(noteRecyclerAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                int num = noteProvider.getLatestdNotes(10);
                if (num > 0) {
                    noteListView.notifyItemRangeInserted(noteRecyclerAdapter, 0, num+2);
                }
            }
        }).start();
    }

    @Override
    public void createNote(final String title, final String message) {
        if (title.isEmpty() && message.isEmpty()) {
            return;
        }

        noteListView.hideKeyboard();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Note note = new Note();
                note.setTitle(title);
                note.setMessage(message);
                note.setCreated_ts(new Date());

                boolean success = noteProvider.createNote(note);
                if (success) {
                    noteListView.notifyNewNote();
                } else {
                    Log.e(TAG, "failed to add note");
                }
            }
        }).start();
    }

    @Override
    public RecyclerViewScrollListener getScrollListener() {
        return recyclerViewScrollListener;
    }

    @Override
    public void deleteSelected() {
        if (selectedNote != null) {
            int success = noteProvider.deleteNote(selectedNote);
            if (success != 0 && selectedPosition != -1) {
                noteRecyclerAdapter.notifyItemRemoved(selectedPosition);
                selectedNote = null;
                selectedPosition = -1;
                noteListView.showEditLayouts(null, -1, false);
            }
        }
    }

    @Override
    public void editSelectedNote(String title, String message) {
        if (selectedNote != null) {
            selectedNote.setTitle(title);
            selectedNote.setMessage(message);

            int success = noteProvider.updateNote(selectedNote);
            if (success != 0 && selectedPosition != -1) {
                selectedNote.setSelected(false);
                Log.i(TAG, "edit pos: " + String.valueOf(selectedPosition));
                noteRecyclerAdapter.notifyItemChanged(selectedPosition);
                selectedNote = null;
                selectedPosition = -1;
                noteListView.showEditLayouts(null, -1, false);
                noteRecyclerAdapter.notifyDataSetChanged();
                noteListView.hideKeyboard();
            }
        }
    }

    RecyclerViewScrollListener recyclerViewScrollListener = new RecyclerViewScrollListener() {

        @Override
        void onScrollUp() {
            noteListView.showLayouts();
        }

        @Override
        void onScrollDown() {
            noteListView.hideLayouts();
        }

        @Override
        void onLoadMore() {
            Log.i(TAG, "onLoadMore");
//            getMoreNotes();
        }
    };

    public class RecyclerItemClickCallback implements NoteCallback {

        @Override
        public void invoke(int id, int position, boolean state) {

            if (state) {
                Note note = noteProvider.getNoteById(id);
                if (note != null) {
                    selectedNote = note;
                    selectedPosition = position;
                    noteListView.showEditLayouts(note, position, true);
                    return;
                }
            }
            selectedNote = null;
            selectedPosition = -1;
            noteListView.showEditLayouts(null, -1, false);
        }
    }
}
