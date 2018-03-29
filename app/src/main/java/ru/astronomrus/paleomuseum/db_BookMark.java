package ru.astronomrus.paleomuseum;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vkoroy on 23.03.18.
 */


import java.util.LinkedList;
import java.util.List;



import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;



 class PaleoItem {

    private int id;
    private String title;
    private String author;
     private String image;
     private String other ;
     private String description ;

     String getImage()
     {
        return image;
     }
     String getDescription()
     {
         return description;
     }
     String getOther()
     {
         return other;
     }
     String getTitle()
    {
        return title;
    }

    String getAuthor()
    {
        return author;
    }

    int getId()
    {
        return id;
    }
    void  setId(int _id)
    {
      id =_id;
    }

    void setImage(String _img)
    {
        image = _img;
    }
    void setDescription(String _d)
    {
        description = _d;
    }
    void setOther(String _ot)
    {
        other = _ot;
    }
    void setTitle(String _title)
    {
     title = _title;
    }

    void setAuthor(String _author)
    {
        author = _author;
    }
    public PaleoItem(){}

    public PaleoItem(String title, String author) {
        super();
        this.title = title;
        this.author = author;
    }

     public PaleoItem(String title, String author ,String  image , String description, String other ) {
         super();
         this.title = title;
         this.author = author;
         this.image = image;
         this.description = description;
         this.other = other;
     }
    //getters & setters

    @Override
    public String toString() {
        return "PaleoItem [id=" + id + ", title=" + title + ", author=" + author
                + "]";
    }
}

public class db_BookMark extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "Paleodb";

    public db_BookMark(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_BOOK_TABLE = "CREATE TABLE books ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, "+
                "author TEXT ," +
                "description TEXT, "+
                "image TEXT, "+
                "other TEXT)";

        // create books table
        db.execSQL(CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS books");

        // create fresh books table
        this.onCreate(db);
    }
    //---------------------------------------------------------------------

    /**
     * CRUD operations (create "add", read "get", update, delete) book + get all books + delete all books
     */

    // Books table name
    private static final String TABLE_BOOKMARKS = "books";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_OTHER = "other";


    private static final String[] COLUMNS = {KEY_ID,KEY_TITLE,KEY_AUTHOR, KEY_IMAGE , KEY_DESCRIPTION , KEY_OTHER};

    public void addBook(PaleoItem paleoItem){
        Log.d("addBook", paleoItem.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, paleoItem.getTitle()); //
        values.put(KEY_AUTHOR, paleoItem.getAuthor()); //
        values.put(KEY_IMAGE, paleoItem.getImage()); //
        values.put(KEY_DESCRIPTION, paleoItem.getDescription()); //
        values.put(KEY_OTHER, paleoItem.getOther()); //

        // 3. insert
        db.insert(TABLE_BOOKMARKS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public PaleoItem getBook(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_BOOKMARKS, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build paleoItem object
        PaleoItem paleoItem = new PaleoItem();
        paleoItem.setId(Integer.parseInt(cursor.getString(0)));
        paleoItem.setTitle(cursor.getString(1));
        paleoItem.setAuthor(cursor.getString(2));

        paleoItem.setDescription(cursor.getString(3));
        paleoItem.setImage(cursor.getString(4));
        paleoItem.setOther(cursor.getString(5));

        Log.d("getBook("+id+")", paleoItem.toString());

        // 5. return paleoItem
        return paleoItem;
    }

    // Get All Books
    public List<PaleoItem> getAllBooks() {
        List<PaleoItem> paleoItems = new LinkedList<PaleoItem>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_BOOKMARKS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build paleoItem and add it to list
        PaleoItem paleoItem = null;
        if (cursor.moveToFirst()) {
            do {
                paleoItem = new PaleoItem();
                paleoItem.setId(Integer.parseInt(cursor.getString(0)));
                paleoItem.setTitle(cursor.getString(1));
                paleoItem.setAuthor(cursor.getString(2));

                paleoItem.setDescription((cursor.getString(3)));
                paleoItem.setImage(cursor.getString(4));
                paleoItem.setOther(cursor.getString(5));

                // Add paleoItem to paleoItems
                paleoItems.add(paleoItem);
            } while (cursor.moveToNext());
        }

        Log.d("getAllBooks()", paleoItems.toString());

        // return paleoItems
        return paleoItems;
    }

    // Get All Books
    public  boolean  if_exists(String img) {
        // 1. build the query
        String query = "SELECT  image FROM " + TABLE_BOOKMARKS + " where image like '"+img+"'";

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build paleoItem and add it to list
        PaleoItem paleoItem = null;
        boolean res = cursor.getCount() >0;

        return  res;
    }


    // Deleting single paleoItem
    public void deleteBook(PaleoItem paleoItem) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_BOOKMARKS,
                KEY_IMAGE+" = ?",
                new String[] { String.valueOf(paleoItem.getImage()) });

        // 3. close
        db.close();

        Log.d("deleteBook", paleoItem.toString());

    }


    // Deleting single paleoItem
    public void deleteBook(String  urlumg) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_BOOKMARKS,
                KEY_IMAGE+" = ?",
                new String[] { String.valueOf( urlumg ) });

        // 3. close
        db.close();

        Log.d("deleteBook",  urlumg );

    }
}