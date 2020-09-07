package gloomyfish.opencvdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.book.chapter.five.ImageAnalysisActivity;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageUploadAndDecryptionActivity extends AppCompatActivity implements View.OnClickListener {

   /* //宣告物件
    private TextView messageText;
    private Button uploadButton, selectpic;
    private ImageView mImg;

    private int serverResponseCode = 0; //伺服器上傳結果回傳
    private ProgressDialog dialog = null;

    private String upLoadServerUri = null;
    private String imagepath = null;
    private Uri fileUri; //圖片路徑的變數


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload_and_decryption);
        this.setTitle("影像上傳解密");

        //宣告的物件，跟view上的物件連結一起

        messageText = (TextView) findViewById(R.id.lblmessage);

        mImg = (ImageView) findViewById(R.id.imageView_pic);


        //設定連到要上傳的網址
        //upLoadServerUri = "http://220.128.56.35/meowmeowvirus/static/upload.php";
        upLoadServerUri = "http://192.168.0.158:8080/uploadtest/upload.php";

        Button uploadButton = (Button) findViewById(R.id.uploadButton);
        Button selectpic = (Button) findViewById(R.id.button_selectpic);

        selectpic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // 開啟相簿相片集，須由startActivityForResult且帶入requestCode進行呼叫，原因為點選相片後返回程式呼叫onActivityResult
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //按上傳檔案的按鈕並且在處理時。需要用Thread處理Http Post的行為
                dialog = ProgressDialog.show(ImageUploadAndDecryptionActivity.this, " Uploading file...", "true");
                messageText.setText("準備要進行上傳...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                      uploadFile(imagepath); //將圖片路徑呼叫上傳照片的fun
                        uploadFile(fileUri); //將圖片路徑呼叫上傳照片的fun
                    }
                }).start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null && resultCode == RESULT_OK) {
            fileUri = data.getData();
            //imagepath = getPath(selectedImageUri);

            File f = new File(ImageSelectUtils.getRealPath(fileUri, getApplicationContext())); //讀取影像路徑
            fileUri = Uri.fromFile(f); //讀取影像路徑

            if (fileUri == null) {
                Toast toast = Toast.makeText(this, "選取照片的路徑為空值", Toast.LENGTH_LONG);
                toast.show();
            } else {

                try {
                    ContentResolver cr = this.getContentResolver();
                    //依照相片的路徑，轉換成Bitmap的型態顯示至imageview
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(fileUri));
                    mImg.setImageBitmap(bitmap);
                    messageText.setText("上傳的檔案路徑為:" + fileUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //取得選取照片的檔案路徑
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //進行檔案上傳的行為
    public int uploadFile(Uri sourceFileUri) {
        //String fileName = sourceFileUri;
        String fileName = String.valueOf(sourceFileUri);
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphes = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        File sourceFile = new File(String.valueOf(sourceFileUri));

        if (!sourceFile.isFile()) {
            dialog.dismiss();
            Log.e("uploadfile", "Source File not exist :" + fileUri);

            runOnUiThread((new Runnable() {
                @Override
                public void run() {
                    messageText.setText("source file net exist:" + fileUri);
                }
            }));

            return 0;
        } else {
            try {
                //使用HttpURLConnection，連到Server瑞的網頁
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                //打開http連接到url的網頁，在設定透過多媒體的方式，post到server端
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphes + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                //上傳檔案的過程中不是一次全部上傳，是要一部分一部分上傳。
                //因此要設定buffer，把檔案的內容批次上傳。
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                //傳送多媒體的form資料。
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphes + boundary + twoHyphes + lineEnd);

                //接收Server端的訊息以及代碼
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is :" + serverResponseMessage + ":" + serverResponseCode);

                if (serverResponseCode == 200) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed.\n\n See uploaded file your server. \n\n";
                            messageText.setText(msg);
                            Toast.makeText(ImageUploadAndDecryptionActivity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(ImageUploadAndDecryptionActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(ImageUploadAndDecryptionActivity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            dialog.dismiss();
            return serverResponseCode;
        }
    }*/

    //////20200706--test
    //宣告物件
    private TextView messageText;
    private Button uploadButton, btnSelectPic, btnUploadWithBrowse;
    private ImageView imageview;

    private int serverResponseCode = 0;
    private ProgressDialog dialog = null;

    private String upLoadServerUri = null;
    private String imagepath = null;
    private Uri selectedImageUri; //圖片路徑的變數

    private DisplayMetrics mPhone; // 20200709--add

    //webview呼叫使用圖片上傳參數
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload_and_decryption);
        //this.setTitle("上傳功能");

        //關閉actionBar(20200720--add)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //宣告的物件，跟View上的元件來連結。
        //註解物件類別，使用webview替代現有的功能(20200720--modify)
        //uploadButton = (Button) findViewById(R.id.uploadButton);
        //messageText = (TextView) findViewById(R.id.lblmessage);
        //btnSelectPic = (Button) findViewById(R.id.button_selectpic);
        //imageview = (ImageView) findViewById(R.id.imageView_pic);
        //btnUploadWithBrowse = (Button) findViewById(R.id.button14); // 20200713--add

        //設定Button的監聽事件。
        //註解物件類別，使用webview替代現有的功能(20200720--modify)
        //btnSelectPic.setOnClickListener(this);
        //uploadButton.setOnClickListener(this);
        //btnUploadWithBrowse.setOnClickListener(this); // 20200713--add

        //讀取手機解析度
        mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mPhone);

        //設定連結到PHP的網址。(建議用手機來測試，再連到固定IP的網址。)
        //upLoadServerUri = "http://192.168.0.158:8080/uploadtest/upload.php";
        upLoadServerUri = "http://220.128.56.35/androidupload/upload.php";


        //新增Webview顯示功能(20200720--add)
        //因應webview修改獲取infalter對象(20200720--add)
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        ConstraintLayout parent = (ConstraintLayout) inflater.inflate(R.layout.activity_image_upload_and_decryption, null);
        WebView child = (WebView) parent.findViewById(R.id.webview);
        setContentView(parent);

        WebView webView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        webView.setWebChromeClient(new WebChromeClient() {
            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openImageChooserActivity();
                return true;
            }
        });
        String targetUrl = "http://agda.tw/sunsui/grating/";
        webView.loadUrl(targetUrl);
    }

    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    public void onClick(View v) {

        //Button的監聽事件要做什麼事。
        if (v == btnSelectPic) {
            //觸發開啟手機上的相片(類似想檔案總管)，來選要上傳的照片。
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            // startActivityForResult(intent,PHOTO); // 20200709--add

            //回傳時，要如何處理。請重新Override onActivityResult函式。
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
        } else if (v == uploadButton) {

            if (imagepath == null) {
                Toast toast = Toast.makeText(this, "請選取照片再進行上傳!", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                //按上傳檔案的按鈕，要處理時，會用Thread 來處理 Http Post的動作。
                dialog = ProgressDialog.show(ImageUploadAndDecryptionActivity.this, "", "Uploading file...", true);
                messageText.setText("uploading started.....");
                new Thread(new Runnable() {
                    public void run() {
                        uploadFile(imagepath);
//                      uploadFile(selectedImageUri.toString());
                    }
                }).start();

            }
        } else if (v == btnUploadWithBrowse) {
            Uri uri = Uri.parse("https://agda.tw/sunsui/grating/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //判斷webview選取圖片後回傳的行為
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }

        if (requestCode == 1 && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            imagepath = getPath(selectedImageUri);
            File f = new File(ImageSelectUtils.getRealPath(selectedImageUri, getApplicationContext()));
            Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
            int bitmap_width = bitmap.getWidth();
            int bitmap_height = bitmap.getHeight();

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int screenWidth = dm.widthPixels;
            if (bitmap_width <= screenWidth) {
                imageview.setImageBitmap(bitmap);
            } else {
                Bitmap bmp = Bitmap.createScaledBitmap(bitmap, screenWidth, bitmap_height * screenWidth / bitmap_width, true);
                imageview.setImageBitmap(bmp);
            }
            messageText.setText("Uploading file path:" + imagepath);

            //依相片的路徑，轉成Bitmap的型態，在ImageView，顯示出選取的相片。 // 下段程式碼無法對應顯示最近照片
//        Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
//        imageview.setImageBitmap(bitmap);
//        messageText.setText("Uploading file path:" + imagepath);


//        if (selectedImageUri == null) {
//            Toast toast = Toast.makeText(this, "選取照片的路徑為空值", Toast.LENGTH_LONG);
//            toast.show();
//        } else {
//
//            try {
//
//                ContentResolver cr = this.getContentResolver();
//                //讀取照片，型態為Bitmap
//                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(selectedImageUri));
//
//                //Bitmap imageBitmap = BitmapFactory.decodeFile(fileUri.getPath());
//
//                //判斷照片為橫向或者為直向，並進入ScalePic判斷圖片是否要進行縮放
//                if (bitmap.getWidth() > bitmap.getHeight()) ScalePic(bitmap,
//                        mPhone.heightPixels);
//                else ScalePic(bitmap, mPhone.widthPixels);
//                messageText.setText("Uploading file path:" + selectedImageUri);
//
//            } catch (FileNotFoundException ignored) {
//            }
//        }
//        }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); ) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

    // 20200709--add
    private void ScalePic(Bitmap bitmap, int phone) {
        //縮放比例預設為1
        float mScale = 1;
        //如果圖片寬度大於手機寬度則進行縮放，否則直接將圖片放入ImageView內
        if (bitmap.getWidth() > phone) {

            //判斷縮放比例
            mScale = (float) phone / (float) bitmap.getWidth();

            Matrix mMat = new Matrix();
            mMat.setScale(mScale, mScale);

            Bitmap mScaleBitmap = Bitmap.createBitmap(bitmap, 0,
                    0,
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    mMat,
                    false);
            imageview.setImageBitmap(mScaleBitmap);
        }
//        照片顯示於手機上
        else imageview.setImageBitmap(bitmap);
    }

    //取得選取相片的檔案路徑。
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //進行檔案上傳的動作。
    public int uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            dialog.dismiss();
            Log.e("uploadFile", "Source File not exist :" + imagepath);

            runOnUiThread(new Runnable() {
                public void run() {
                    messageText.setText("Source File not exist :" + imagepath);
                }
            });

            return 0;
        } else {
            try {

                //使用HttpURLConnection，連到Server瑞的網頁
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                //打開 HTTP 連到 URL物件上的網頁，再設定要以多媒體的方式，POST資料到Server端。
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Input
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                //上傳檔案，不是一次就可以傳送上去。要一部份一部份的上傳。
                //所以，要先設定一個buffer，將檔案的內容分次上傳。
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                //傳送多媒體的form資料。
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                //接收Server端的回傳訊息及代碼
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {

                    runOnUiThread(new Runnable() {
                        public void run() {
//                            String msg = "File Upload Completed.\n\n See uploaded file your server. \n\n";
                            String msg = "File Upload Completed.\n";
                            messageText.setText(msg);
                            Toast.makeText(ImageUploadAndDecryptionActivity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(ImageUploadAndDecryptionActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(ImageUploadAndDecryptionActivity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            dialog.dismiss();
            return serverResponseCode;
        }
    }
}

