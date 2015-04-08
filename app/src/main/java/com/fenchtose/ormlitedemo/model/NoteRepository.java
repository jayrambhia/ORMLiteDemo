package com.fenchtose.ormlitedemo.model;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Jay Rambhia on 05/04/15.
 */
public class NoteRepository {

    private DatabaseHelper dbHelper;
    private Dao<Note, Integer> noteDao;

    public NoteRepository(Context context) {
        DatabaseManager dbManager = new DatabaseManager();
        dbHelper = dbManager.getHelper(context);
        try {
            noteDao = dbHelper.getNoteDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int create(Note note) {
        try {
            return noteDao.create(note);
//            dbHelper.getDatab
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return 0;
    }

    public int update(Note note) {
        try {
            return noteDao.update(note);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int delete(Note note) {
        try {
            return noteDao.delete(note);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public Note getById(int id) {
        try {
            return noteDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Note> findAll() {
        try {
            return noteDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public long getNumberOfNotes() {
        QueryBuilder<Note, Integer> qb = noteDao.queryBuilder();
        try {
            return qb.countOf();
        } catch (SQLException e) {
            return -1;
        }
    }

    public List<Note> getRecentNotes(long limit) {
        if (limit < 1) {
            limit = 10;
        }

        QueryBuilder<Note, Integer> qb = noteDao.queryBuilder();
        try {
            qb.orderBy(Note.TIMESTAMP_FIELD, false);
            qb.limit(limit);
            PreparedQuery<Note> preparedQuery = qb.prepare();
            return noteDao.query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
