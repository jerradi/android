package com.oriana.moroccan.bdarija.model;

import android.util.Base64;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;


@DatabaseTable(tableName = "item")
public class Item implements Serializable {
    public static final int ALL = -1, HISTORIES = 0, JOKES = 1, PROBLEMS = 2, EXPERIENCES = 3, LIKES =  -2;
    public static final int SINGLE = 0, CATEGORY_QUOTES = 1, ALL_QUOTES = 2;
    public static final String TAG_DB_ID = "id", TAG_DB_TEXT = "text",
            TAG_DB_CAT = "category_id", TAG_DB_DATE = "date",
            TAG_DB_AUTH = "author";



    @Override
    public String toString() {
        return "Item{" +
                "id=" + like +
                ", description='" + description + '\'' +
                '}';
    }

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String description;


    @DatabaseField
    private Date dateCreated;

    @DatabaseField
    private int type;

    @DatabaseField
    private boolean like;

    @DatabaseField
    private int category;
   @DatabaseField
    private boolean seen = false;
    @DatabaseField
    private boolean toSend = false;
    @DatabaseField
    private Integer dbId;


    public Item() {
        dateCreated= new Date();
    }
    public Item(String desc, int type) {
        this.dateCreated = new Date();
        this.type = type;
        this.category = 4;
        this.description = desc;
        this.seen = false;
    }



    public Item(String description, int type, int category) {
        this.description = description;
        this.seen = false;
        this.dateCreated = new Date();
    }

    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
    return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public boolean isToSend() {
        return toSend;
    }

    public void setToSend(boolean toSend) {
        this.toSend = toSend;
    }
}
