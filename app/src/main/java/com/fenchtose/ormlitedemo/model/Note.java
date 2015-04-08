package com.fenchtose.ormlitedemo.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Jay Rambhia on 05/04/15.
 */

@DatabaseTable(tableName = "notes")
public class Note {

    public static final String TIMESTAMP_FIELD = "timestamp";

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    public int id;

    @DatabaseField(useGetSet = true)
    private String title;

    @DatabaseField(canBeNull = false, useGetSet = true)
    private String message;

    @DatabaseField(dataType = DataType.DATE_LONG, canBeNull = false, useGetSet = true, columnName = TIMESTAMP_FIELD)
    private Date created_ts;

    // For UI Purposes
    private boolean selected;

    // Empty Constructor for ORMLite
    public Note() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreated_ts() {
        return created_ts;
    }

    public void setCreated_ts(Date created_ts) {
        this.created_ts = created_ts;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
