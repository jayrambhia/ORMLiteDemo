package com.fenchtose.ormlitedemo.ui;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fenchtose.ormlitedemo.R;
import com.fenchtose.ormlitedemo.model.Note;
import com.fenchtose.ormlitedemo.model.NoteRecyclerAdapter;


public class NoteListActivity extends ActionBarActivity implements NoteListView {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LinearLayout composeLayout;
    private EditText titleEditText;
    private EditText messageEditText;
    private ImageButton composeButton;

    private Context context;

    private NoteListPresenter presenter;

    private final int STATE_NORMAL = 1;
    private final int STATE_EDIT = 2;

    private int state = STATE_NORMAL;

    private Menu menu;
//    private int selectedNotePosition

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        setContentView(R.layout.activity_note_list);
        setupToolbar();
        setupLayout();

        presenter = new NoteListPresenterImpl(this, context);
        presenter.loadInitData();
    }

    private void setupToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupLayout() {

        composeLayout = (LinearLayout)findViewById(R.id.compose_layout);
        titleEditText = (EditText)composeLayout.findViewById(R.id.title_edittext);
        messageEditText = (EditText)composeLayout.findViewById(R.id.message_edittext);
        messageEditText.setVisibility(View.GONE);
        composeButton = (ImageButton)composeLayout.findViewById(R.id.action_send);

        titleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.e("TitleEditText", "hasFocus");
//                    messageEditText.setVisibility(View.VISIBLE);
                }
            }
        });

        titleEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageEditText.setVisibility(View.VISIBLE);
            }
        });

        composeButton.setOnClickListener(composeListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;

        if (state == STATE_NORMAL) {
            getMenuInflater().inflate(R.menu.menu_note_list, menu);
        } else if (state == STATE_EDIT) {
            getMenuInflater().inflate(R.menu.menu_note_list_edit, menu);
        } else {
            return false;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete:
                presenter.deleteSelected();
                break;
        }
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setAdapter(final NoteRecyclerAdapter adapter) {
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.getItemAnimator().setAddDuration(700);
        recyclerView.getItemAnimator().setRemoveDuration(700);
        recyclerView.getItemAnimator().setMoveDuration(700);
        recyclerView.getItemAnimator().setChangeDuration(700);

        recyclerView.setAdapter(adapter);

        recyclerView.setOnScrollListener(presenter.getScrollListener());
    }

    @Override
    public void notifyNewNote() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                clearEditText();
                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                adapter.notifyItemInserted(1);

            }
        });
    }

    @Override
    public void notifyItemRangeInserted(final NoteRecyclerAdapter adapter, final int first, final int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                adapter.notifyItemRangeInserted(first, size);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void showLayouts() {
        if (toolbar != null) {
            toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        }

        if (composeLayout != null) {
            composeLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        }
    }

    @Override
    public void hideLayouts() {
        hideKeyboard();

        if (toolbar != null) {
            toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        }

        if (composeLayout != null){
            composeLayout.animate().translationY(composeLayout.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        }
    }

    @Override
    public void showEditLayouts(Note note, int position, boolean status) {
        if (status) {
            state = STATE_EDIT;
            showLayouts();
            titleEditText.setText(note.getTitle());
            messageEditText.setText(note.getMessage());
        } else {
            state = STATE_NORMAL;
            titleEditText.setText("");
            messageEditText.setText("");
        }

        menu.clear();
        onCreateOptionsMenu(menu);
//        Menu menu = getMe
    }

    private void clearEditText() {
        titleEditText.clearFocus();
        messageEditText.clearFocus();

        titleEditText.setText("");
        messageEditText.setText("");
    }

    private void composeNote() {
        String title = titleEditText.getText().toString();
        String message = messageEditText.getText().toString();

        if (state == STATE_NORMAL) {
            presenter.createNote(title, message);
        } else if (state == STATE_EDIT) {
            presenter.editSelectedNote(title, message);
        }
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        messageEditText.setVisibility(View.GONE);
    }

    View.OnClickListener composeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            composeNote();
        }
    };
}
