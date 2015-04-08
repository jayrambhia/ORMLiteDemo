package com.fenchtose.ormlitedemo.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jay Rambhia on 05/04/15.
 */
public class NoteProvider {

    private Context context;
    private NoteRepository noteRepository;
    private List<Note> notes;

    public NoteProvider(Context ctx) {
        context = ctx;
        noteRepository = new NoteRepository(context);
        notes = new ArrayList<>();
    }

    public int getLatestdNotes(long limit) {
        List<Note> new_notes = noteRepository.getRecentNotes(limit);
        notes.addAll(new_notes);
        return new_notes.size();
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public boolean createNote(Note note) {
        int success = noteRepository.create(note);
        if (success == 1) {
            notes.add(0, note);
            return true;
        }

        return false;
    }

    public Note getNoteById(int id) {
        Iterator<Note> iterator = notes.iterator();
        while (iterator.hasNext()) {
            Note note = iterator.next();
            if (note.id == id) {
                return note;
            }
        }

        return null;
    }

    public int deleteNote(Note note) {
        int success = noteRepository.delete(note);
        if (success == 1) {
            Iterator<Note> iterator = notes.iterator();
            while (iterator.hasNext()) {
                Note note_r = iterator.next();
                if (note_r.id == note.id) {
                    iterator.remove();
                    break;
                }
            }
        }

        return success;
    }

    public int updateNote(Note note) {
        return noteRepository.update(note);
    }
}
