package gloomyfish.opencvdemo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class QueryDatabase {
    //建立SQLiteDatabase物件
    private SQLiteDatabase db = null;

    private final static String TABLE_NAME = "TableInformation";
    //    建立欄位名稱
    //    編排編號
    private final static String _ID = "_id";
    //    商品名稱
    private final static String NAME = "name";
    //    商品廠商
    private final static String COMPANY = "company";
    //    商品長度
    private final static String WEIGHT = "weight";
    //    商品寬度
    private final static String HEIGHT = "height";
    //    商品dpi
    private final static String DPI = "dpi";
    //    商品角度
    private final static String ANGLE = "angle";


    //    建立商品表格
    private final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY,"
            + NAME + " TEXT," + COMPANY + " TEXT," + HEIGHT + " TEXT," + WEIGHT + " TEXT," + DPI + " TEXT," + ANGLE + " TEXT)";

    private Context context;

    //    QueryDatabase的建構式
    public QueryDatabase(Context context) {
        this.context = context;
    }

    //     建立open()方法，資料庫存執行開啟資料庫，尚未存在則建立資料庫
    public void open() throws SQLException {
        try {
            //        建立資料庫並指定權限
            db = context.openOrCreateDatabase("informationdb.db", Context.MODE_PRIVATE, null);
            //        建立表格
            db.execSQL(CREATE_TABLE);

            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "痛護寧膜衣錠" + "','" + "佐藤製藥" + "'," + "7" + "," + "9" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "長壽白軟包菸" + "','" + "TTL" + "'," + "7" + "," + "9" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "膠原蛋白美妍粉" + "','" + "台鹽生技" + "'," + "7" + "," + "9" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "威舒益益生菌" + "','" + "立萬利" + "'," + "9" + "," + "7" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "金門高粱酒" + "','" + "金門酒廠" + "'," + "9" + "," + "6" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "小象公仔" + "','" + "SHISEIDO" + "'," + "12" + "," + "26" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "純萃綠茶保濕乳液" + "','" + "SHISEIDO" + "'," + "10" + "," + "10" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "Lightning 8pin充電線" + "','" + "Apacer" + "'," + "10" + "," + "11" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "愛文芒果乾" + "','" + "統一" + "'," + "9" + "," + "9" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "純萃綠茶保濕噴霧" + "','" + "SHISEIDO" + "'," + "10" + "," + "10" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "經典高粱紀念酒" + "','" + "金門酒廠" + "'," + "12" + "," + "7" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "單一純麥威士忌" + "','" + "TTL" + "'," + "12" + "," + "26" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "Lightning 8pin充電線" + "','" + "Apacer" + "'," + "10" + "," + "9" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "恬然愛文芒果乾" + "','" + "統一" + "'," + "9" + "," + "8" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "2.5吋外接硬碟" + "','" + "ADATA" + "'," + "7" + "," + "9" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "五月天人生無限拼圖" + "','" + "相信音樂" + "'," + "7" + "," + "9" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "宏都拉斯濾掛式咖啡" + "','" + "UCC" + "'," + "9" + "," + "6" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "維他命E400軟膠囊" + "','" + "杏輝" + "'," + "9" + "," + "7" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "全效抗痕亮采賦活乳" + "','" + "SHISEIDO" + "'," + "9" + "," + "6" + "," + "101.16" + "," + "45" + ")");
            db.execSQL("Insert Into " + TABLE_NAME + " (NAME,COMPANY,HEIGHT,WEIGHT,DPI,ANGLE)Values('" + "迷你貨櫃車" + "','" + "NCTaiwan" + "'," + "17" + "," + "9" + "," + "101.16" + "," + "45" + ")");

      /*          // 20200807--add(增加預設資料)
                //sqlsite建立資料庫
                ContentValues cv = new ContentValues();
                cv.put("Session" , NAME);
                cv.put("1000" , PRICE);
                cv.put("200" , WEIGHT);
                cv.put("300" , HEIGHT);
                cv.put("600" , DPI);
                //            cv.put("MonthEnd" , endMonth);
                //            cv.put("DayEnd" , endDay);
                long rowID = db.insertOrThrow("TableInformation", null, cv);*/


        } catch (Exception e) {

            Toast.makeText(context, "產品查詢資料庫已建立", Toast.LENGTH_LONG).show();
        }

    }

    //    建立新增、修改(更新)、刪除，資料操作
    //    execSQL完整輸入SQL語法實現，資料操作

    //    建立方法append()
    public void append(String name, String company, String height, String weight, String dpi, String angle) {
        String insert_text = "INSERT INTO " + TABLE_NAME + "( " + NAME + "," + COMPANY + "," + HEIGHT + "," + WEIGHT + "," + DPI + "," + ANGLE + ") " +
                "values ('" + name.trim() + "','" + company + "'," + height + "," + weight + "," + dpi + "," + angle + ")";
        db.execSQL(insert_text);

    }

    //    建立方法update()
    public void update(String name, String company, String height, String weight, String dpi, String angle, long id) {
        String update_text = "UPDATE " + TABLE_NAME + " SET " + NAME + "='" + name + "'," + COMPANY + "='" + company + "'," + HEIGHT + "=" + height + "," + WEIGHT + "=" + weight + " ," + DPI + "=" + dpi + "," + ANGLE + "=" + angle + " WHERE " + _ID + "=" + id;
        db.execSQL(update_text);
    }

    //    建立方法delete()
    public void delete(long id) {
        String delete_text = "DELETE FROM " + TABLE_NAME + " WHERE " + _ID + "=" + id;
        db.execSQL(delete_text);

    }

    //    建立查詢方法select()，查詢單筆資料
    //    rawQuery完整輸入SQL語法實現資料查詢
//    public Cursor select(long id) {
//        String select_text = "SELECT * FROM " + TABLE_NAME + " WHERE " + _ID + "=" + id;
//        Cursor cursor = db.rawQuery(select_text, null);
//        return cursor;
//    }

    // 20200709--add()
    public Cursor select(String name) {
        Log.d("test2", name);
//        String select_text = "SELECT * FROM " + TABLE_NAME + " WHERE " + NAME + "=" + name;

        // 20200713--add(修改查詢的邏輯)
        String select_text = "SELECT * FROM " + TABLE_NAME + "  WHERE " + NAME + "= ?";
//        Cursor cursor = db.rawQuery(select_text, null);
        Cursor cursor = db.rawQuery(select_text, new String[]{name});
        return cursor;
    }

    //    建立查詢方法select_all()，查詢所有資料
    //    rawQuery完整輸入SQL語法實現資料查詢
    public Cursor select_all() {
        String select_text = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(select_text, null);
        return cursor;
    }
}


