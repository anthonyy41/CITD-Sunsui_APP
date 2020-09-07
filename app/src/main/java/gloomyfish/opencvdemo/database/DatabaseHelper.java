package gloomyfish.opencvdemo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import gloomyfish.opencvdemo.entities.Contact;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String dbName = "contactDB";
    private static int dbVersion = 1;
    private static String contactTable = "contact";
    private static String idColumn = "id";
    private static String nameColumn = "name";
    private static String phoneColumn = "phone";
    private static String addressColumn = "address";
    private static String emailColumn = "email";
    private static String descriptionColumn = "description";

    public DatabaseHelper(Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //sqlsite建立資料庫
        sqLiteDatabase.execSQL("create table " + contactTable + "(" +
                idColumn + " integer primary key autoincrement, " +
                nameColumn + " tex, " +
                phoneColumn + " text, " +
                addressColumn + " text, " +
                emailColumn + " text, " +
                descriptionColumn + " text " +
                ")"
        );

        //sqlsite建立資料庫
        sqLiteDatabase.execSQL("INSERT INTO " + contactTable + "(nameColumn, phoneColumn, addressColumn, emailColumn, descriptionColumn ) VALUES ('WGS 84', 6378137, 6356752.314, 0.9996, 500000)"
        );
    }

    //找尋所有的資料
    public List<Contact> findAll() {
        List<Contact> contacts = null;
        try {
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("select * from " + contactTable, null);
            if (cursor.moveToFirst()) {
                contacts = new ArrayList<Contact>();
                do {
                    Contact contact = new Contact();
                    contact.setId(cursor.getInt(0));
                    contact.setName(cursor.getString(1));
                    contact.setPhone(cursor.getString(2));
                    contact.setAddress(cursor.getString(3));
                    contact.setEmail(cursor.getString(4));
                    contact.setDescription(cursor.getString(5));
                    contacts.add(contact);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            contacts = null;
        }
        return contacts;
    }

    //搜尋資料的行為
    public List<Contact> search(String keyword) {
        List<Contact> contacts = null;
        try {
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("select * from " + contactTable + " where " + nameColumn + " like ?", new String[]{"%" + keyword + "%"});
            if (cursor.moveToFirst()) {
                contacts = new ArrayList<Contact>();
                do {
                    Contact contact = new Contact();
                    contact.setId(cursor.getInt(0));
                    contact.setName(cursor.getString(1));
                    contact.setPhone(cursor.getString(2));
                    contact.setAddress(cursor.getString(3));
                    contact.setEmail(cursor.getString(4));
                    contact.setDescription(cursor.getString(5));
                    contacts.add(contact);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            contacts = null;
        }
        return contacts;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        //        20200807--add
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + contactTable);
        //        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_POINTS);
        onCreate(sqLiteDatabase);
    }
}
