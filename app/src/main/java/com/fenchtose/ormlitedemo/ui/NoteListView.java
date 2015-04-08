package com.fenchtose.ormlitedemo.ui;

import com.fenchtose.ormlitedemo.model.Note;
import com.fenchtose.ormlitedemo.model.NoteRecyclerAdapter;

/**
 * Created by Jay Rambhia on 05/04/15.
 */
public interface NoteListView {

    public void setAdapter(NoteRecyclerAdapter adapter);
    public void notifyNewNote();
    public void notifyItemRangeInserted(NoteRecyclerAdapter adapter, int first, int size);
    public void showLayouts();
    public void hideLayouts();
    public void showEditLayouts(Note note, int position, boolean status);
    public void hideKeyboard();
}
