package gloomyfish.opencvdemo;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity; //searchview有版本差異，在呼叫時要確認有沒有載入對的library

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.sql.SQLData;
import java.util.ArrayList;
import java.util.List;

//呼叫DBHelper類別定義的常數
import gloomyfish.opencvdemo.database.DatabaseHelper;

import gloomyfish.opencvdemo.adapters.ContactListAdapter;
import gloomyfish.opencvdemo.database.QueryDatabase;
import gloomyfish.opencvdemo.entities.Contact;

public class ProductInformationQueryActivity extends AppCompatActivity {
    //    建立欄位名稱
    //    編排編號
    private final static String _ID = "_id";
    //    名稱
    private final static String NAME = "name";
    //    價格
    private final static String PRICE = "price";
    private ListView lstView;
    private Button btnAppend, btnUpdate, btnSelect, btnSelectAll;
    private TextInputEditText edtId, edtName, edtCpmpany, edtAngle, edtWeight, edtHeight, edtDpi;
    private QueryDatabase db;
    private Cursor cursor;
//    private FriendDbOpenHelper mFriendDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_information_query);
        this.setTitle("查詢功能");
        //        尋找各個元件的ID
        findViews();
        //        建立FruitDB物件db
        db = new QueryDatabase(ProductInformationQueryActivity.this);
        //        查詢目前資料庫擁有的資料
        db.open();
        cursor = db.select_all();
        //        更新ListView重新顯示資料
        UpdateListView(cursor);
    }

    private void findViews() {
        btnAppend = (Button) findViewById(R.id.btnAppend);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnSelect = (Button) findViewById(R.id.btnSelect);
        btnSelectAll = (Button) findViewById(R.id.btnSelectAll);

        edtId = (TextInputEditText) findViewById(R.id.edtId);
        edtName = (TextInputEditText) findViewById(R.id.edtName);
        edtAngle = (TextInputEditText) findViewById(R.id.edtAngle);
        edtCpmpany = (TextInputEditText) findViewById(R.id.edtCompany);

        // 20200708--add 新增產品寬度、長度DPI欄位
        edtWeight = (TextInputEditText) findViewById(R.id.edtWeight);
        edtHeight = (TextInputEditText) findViewById(R.id.edtHeight);
        edtDpi = (TextInputEditText) findViewById(R.id.edtDpi);

        lstView = (ListView) findViewById(R.id.lstView);
        //        建立按鈕的偵聽器
        btnAppend.setOnClickListener(listener);
        btnUpdate.setOnClickListener(listener);
        btnSelect.setOnClickListener(listener);
        btnSelectAll.setOnClickListener(listener); // 20200713--add
        //        匿名類別偵聽

        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                cursor.moveToPosition(position);
                edtId.setText(cursor.getString(0));
                edtName.setText(cursor.getString(1));
                edtCpmpany.setText(cursor.getString(2));
                edtHeight.setText(cursor.getString(3));
                edtWeight.setText(cursor.getString(4));
                edtAngle.setText(cursor.getString(5));
                edtDpi.setText(cursor.getString(6));
            }
        });
        lstView.setSelector(android.R.drawable.alert_light_frame);
        lstView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                try {
                    cursor.moveToPosition(position);
                    edtId.setText(cursor.getString(0));
                    edtName.setText(cursor.getString(1));
                    edtCpmpany.setText(cursor.getString(2));
                    edtHeight.setText(cursor.getString(3));
                    edtWeight.setText(cursor.getString(4));
                    edtAngle.setText(cursor.getString(5));
                    edtDpi.setText(cursor.getString(6));

                    final int _id = Integer.parseInt(cursor.getString(0));
                    String name = cursor.getString(1);
                    String company = cursor.getString(2);
                    String height = cursor.getString(3);
                    String weight = cursor.getString(4);
                    String dpi = cursor.getString(5);
                    String angle = cursor.getString(5);

                    new AlertDialog.Builder(ProductInformationQueryActivity.this)
                            .setMessage("確定刪除\nID=" + id +
                                    "\n商品名稱 : "
                                    + name +
                                    "\n廠商名稱 : " + company)
                            .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //                    執行刪除動作
                                    db.delete(cursor.getInt(0));
                                    //                    更新顯示畫面
                                    UpdateListView(cursor = db.select_all());

                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                } catch (Exception e) {
                    Toast.makeText(ProductInformationQueryActivity.this, "刪除失敗", Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
    }

    public Button.OnClickListener listener = new Button.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnAppend: {
                    try {

                        //                    執行新增動作
                        db.append(edtName.getText().toString(), edtCpmpany.getText().toString(), edtAngle.getText().toString(), edtWeight.getText().toString()
                                , edtHeight.getText().toString(), edtDpi.getText().toString());
                        //                    更新顯示畫面
                        UpdateListView(cursor = db.select_all());
                    } catch (Exception e) {
                        Toast.makeText(ProductInformationQueryActivity.this, "新增失敗", Toast.LENGTH_LONG).show();
                    }

                    break;

                }
                case R.id.btnUpdate: {
                    try {
                        //                    執行修改動作
                        db.update(edtName.getText().toString(), edtCpmpany.getText().toString(), edtHeight.getText().toString(), edtWeight.getText().toString()
                                , edtDpi.getText().toString(), edtAngle.getText().toString(), Integer.parseInt(edtId.getText().toString()));
                        //                    更新顯示畫面
                        UpdateListView(cursor = db.select_all());
                    } catch (Exception e) {

                        Toast.makeText(ProductInformationQueryActivity.this, "更新失敗", Toast.LENGTH_LONG).show();
                    }
                    break;

                }

                // 搜尋按鍵
                case R.id.btnSelect: {
                    try {
                        Cursor select_cursor;

//                        select_cursor = db.select(Integer.parseInt(edtId.getText().toString()));
                        select_cursor = db.select(((edtName.getText().toString().trim())));
                        UpdateListView(select_cursor);
                        break;
                    } catch (Exception e) {
                        Toast.makeText(ProductInformationQueryActivity.this, "沒有輸入欄位或是沒有這個名稱", Toast.LENGTH_SHORT).show();
                    }

                    Log.d("test1", ((edtName.getText().toString())));
                }

                // 搜尋所有商品按鍵
                case R.id.btnSelectAll: {
                    try {
                        cursor = db.select_all();
                        UpdateListView(cursor);
                        break;
                    } catch (Exception e) {
                        Toast.makeText(ProductInformationQueryActivity.this, "沒有輸入欄位或是沒有這個名稱", Toast.LENGTH_SHORT).show();
                    }

                    Log.d("test1", ((edtName.getText().toString())));
                }

            }
        }
    };

    //    建立更新ListView的方法
    public void UpdateListView(Cursor fruitcursor) {
        MyAdapter adapter = new MyAdapter(fruitcursor);
        adapter.notifyDataSetChanged();
        lstView.setAdapter(adapter);
        cursor = fruitcursor;
    }

    public class MyAdapter extends BaseAdapter {
        private Cursor cursor;

        public MyAdapter(Cursor cursor) {
            this.cursor = cursor;
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            cursor.moveToPosition(position);
            return cursor.getInt(0);
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View getview = view;
            cursor.moveToPosition(position);
            getview = getLayoutInflater().inflate(R.layout.content_layout, null);

            // ImageView imgView = (ImageView) getview.findViewById(R.id.imgView);

            TextView txtId = (TextView) getview.findViewById(R.id.txtId);
            txtId.setText(String.valueOf(cursor.getString(0)));

            TextView txtName = (TextView) getview.findViewById(R.id.txtName);
            txtName.setText("品名 : "+cursor.getString(1));

            TextView txtCompany = (TextView) getview.findViewById(R.id.txtCompany);
            txtCompany.setText("廠牌 : " + cursor.getString(2));

            TextView txtHeight = (TextView) getview.findViewById(R.id.txtHeight);
            txtHeight.setText("高度 : " + cursor.getString(3) + "cm");

            TextView txtWeight = (TextView) getview.findViewById(R.id.txtWeight);
            txtWeight.setText("寬度 : " + cursor.getString(4) + "cm");

            TextView txtDpi = (TextView) getview.findViewById(R.id.txtDpi);
            txtDpi.setText("DPI : " + cursor.getString(5));

            TextView txtAngle = (TextView) getview.findViewById(R.id.txtAngle);
            txtAngle.setText("解密角度 : " + cursor.getString(6));

//            TextView txtHeight = (TextView) getview.findViewById(R.id.txtHeight);
//            txtHeight.setText(cursor.getString(4));
//            TextView txtDpi = (TextView) getview.findViewById(R.id.txtDpi);
//            txtDpi.setText(cursor.getString(5));

//            switch (cursor.getString(1).trim()) {
//                case "apple": {
////                    imgView.setImageResource(R.drawable.apple);
//                    break;
//                }
//                case "banana": {
////                    imgView.setImageResource(R.drawable.banana);
//                    break;
//                }
//                case "orange": {
////                    imgView.setImageResource(R.drawable.orange);
//                    break;
//                }
//                case "strawberry": {
////                    imgView.setImageResource(R.drawable.strawberry);
//                    break;
//                }
//                case "grape": {
////                    imgView.setImageResource(R.drawable.grape);
//                    break;
//                }
//                default: {
////                    imgView.setImageResource(R.drawable.fruit);
//                    Log.d("debug", cursor.getString(1));
//                    break;
//                }
//            }
            return getview;
        }
    }


//    private DatabaseHelper dbhelper = null;
//    private TextView result = null;
//    private ListView listViewContact;
//    public SQLData DB = null;
//    List<String> arrayList = new ArrayList<>();
//    ListAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_product_information_query);
//        this.setTitle("產品資訊查詢");
//
//        // 20200707--add
//        db=new FruitDB(MainActivity.this);
//        db.open();
//        cursor=db.select_all();
//        UpdateListView(cursor);
//
//
//
//        /*dbhelper = new DatabaseHelper(this);
//        dbhelper.close();*/
//        //add("123"); //加入資料
//
//        add(); //每次應用程式執行時,會新增一筆david的資料
//        //initView();
//        loadData();
//
//        /*Button btn = (Button) findViewById(R.id.button16);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri uri = Uri.parse("http://35.221.208.50");
//                Intent i = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(i);
//            }
//        });*/
//    }
//
// /*   private void add(String s) {
//        SQLiteDatabase db =DB.getWritebleDatabase();
//    }*/
//
//    //新增資料
//
//    private void add() {
//
//        SQLiteDatabase db = dbhelper.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//
///*        values.put(name, "david");
//
//        values.put(phone, "03-21451745");
//
//        values.put(mail, "david@yahoo.com");
//
//        db.insert(TABLE_NAME, null, values);*/
//
//    }
//
////    private void initView() {
////        // listViewContact = findViewById(R.id.listViewContact);
////        registerForContextMenu(listViewContact);
////    }
//
//    private void loadData() {
//        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
//        List<Contact> contacts = databaseHelper.findAll();
//        if (contacts != null) {
//            listViewContact.setAdapter(new ContactListAdapter(getApplicationContext(), contacts));
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        SearchView searchView = (SearchView) menu.findItem(R.id.searchView).getActionView();
//   /*     MenuItem item = menu.findItem(R.id.searchView);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);*/
//
//        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setSubmitButtonEnabled(true);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                searchContact(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                searchContact(newText);
//                return false;
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    private void searchContact(String keyword) {
//        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
//        List<Contact> contacts = databaseHelper.search(keyword);
//        if (contacts != null) {
//            listViewContact.setAdapter(new ContactListAdapter(getApplicationContext(), contacts));
//        }
//    }

}
