package com.fenchtose.ormlitedemo.ui;

/**
 * Created by Jay Rambhia on 05/04/15.
 */
public interface NoteListPresenter {

    public void loadInitData();
    public void createNote(String title, String message);
    public RecyclerViewScrollListener getScrollListener();
    public void deleteSelected();
    public void editSelectedNote(String title, String message);

}
