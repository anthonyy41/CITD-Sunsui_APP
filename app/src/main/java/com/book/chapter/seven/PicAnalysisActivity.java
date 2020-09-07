package com.book.chapter.seven;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gloomyfish.opencvdemo.ImageSelectUtils;
import gloomyfish.opencvdemo.R;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class PicAnalysisActivity extends AppCompatActivity {
    private static final String TAG = null;
    private static final String CV_TAG = null;

    // 20200706--add
    //    private Bitmap srcBitmap;

/*    book example code
    private final int REQUEST_PERMISSION_CAMERA = 100;
    private boolean mbFaceDetAvailable;
    private int miMaxFaceCount = 0;
    private int miFaceDetMode;

    private TextureView mTextureView = null;

    private Size mPreviewSize = null ;
    private CameraDevice mCameraDevice = null;
    private CaptureRequest.Builder mPreviewBuilder = null;
    private CameraCaptureSession mCameraPreviewCaptureSession = null;*/

    //宣告
    private ImageView mImg;
    //    private Button ;
    private ImageView analysisresultimg;
    private DisplayMetrics mPhone;
    private final static int CAMERA = 88;
    private final static int PHOTO = 100;
    private final static int ANALYSISRESULT = 90;
    private int option = 0;

    private static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 2;
    //private Uri mCurrentPhotoPath; //圖片路徑路徑存取
    private Uri fileUri; //圖片路徑的變數

    //圖片名稱
    static final int REQUEST_IMAGE_CAPTURE = 1;

    //權限用圖表儲存
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    // 聲明一個集合，從後續的代码中用來存取用户拒絕授權的權
    List<String> mPermissionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_analysis);
        this.setTitle("光柵功能解析");

        // android 7.0解決系統拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        final int permisson = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        /*final int permisson1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        final int permisson2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);*/

        //讀取手機解析度
        mPhone = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mPhone);

        // 20200706--add
        // srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.imageView_pic);

        mImg = (ImageView) findViewById(R.id.imageView2);
//        analysisresultimg = (ImageView) findViewById(R.id.imageView5);
        Button mCamera = (Button) findViewById(R.id.takePic);
        Button mPhoto = (Button) findViewById(R.id.choosePic);
        Button mAnalysis = (Button) findViewById(R.id.analysispic);

        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //後須新增點擊權限後直接進入相機的邏輯
                if (Build.VERSION.SDK_INT >= 23) {
                    for (String permission : permissions) {
                        if (ContextCompat.checkSelfPermission(PicAnalysisActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                            mPermissionList.add(permission);
                            dispatchTakePictureIntent();
                        }
                    }
                    if (mPermissionList.isEmpty()) {//未授權的表示都接受權限
                        Toast.makeText(PicAnalysisActivity.this, "啟動相機", Toast.LENGTH_SHORT).show();
                    } else {//请求权限方法
                        String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
                        ActivityCompat.requestPermissions(PicAnalysisActivity.this, permissions, MY_PERMISSIONS_REQUEST_CALL_CAMERA);
                        dispatchTakePictureIntent();
                    }
                }

                if (permisson == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
//                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, CAMERA);
         /*           if (intent.resolveActivity(getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File

                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(PicAnalysisActivity.this,
                                    "gloomyfish.opencvdemo.fileprovider",
                                    photoFile);

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                        }
                    }*/
                }
                //開啟相機功能，並將拍照後的圖片存入SD卡相片集內，須由startActivityForResult且帶入requestCode進行呼叫，原因為拍照完畢後返回程式後則呼叫onActivityResult
           /*     ContentValues value = new ContentValues();
                value.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        value);
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, uri.getPath());
                startActivityForResult(intent, CAMERA);*/
            }
        });


        mPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // 開啟相簿相片集，須由startActivityForResult且帶入requestCode進行呼叫，原因為點選相片後返回程式呼叫onActivityResult
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PHOTO);
            }
        });

        mAnalysis.setOnClickListener(new View.OnClickListener() { //事件響應的方式進行處理並顯示結果
            @Override
            public void onClick(View view) {

                if (fileUri != null) {
                    // 20200713--add
                    LayoutInflater inflater = LayoutInflater.from(PicAnalysisActivity.this);
                    final View v = inflater.inflate(R.layout.content_grating, null);

                    new AlertDialog.Builder(PicAnalysisActivity.this).setTitle("參數設定").setMessage("若未輸入寬度及dpi將會以預設值進行解析").
                            setView(v).setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText editText = (EditText) (v.findViewById(R.id.editTextWidth)); //寬度
                            EditText editText1 = (EditText) (v.findViewById(R.id.editTextDpi)); //dpi
                            EditText editText2 = (EditText) (v.findViewById(R.id.editTextAngles));

                            if (editText.getText().toString().matches("") && editText1.getText().toString().matches("") && editText2.getText().toString().matches("")) { // 兩個欄位都空值的時候
                                String defaultWidth = "9";
                                String defaultDpi = "101.16";
                                String defaultAngle = "0";
                                editText.setText(defaultWidth);
                                editText1.setText(defaultDpi);
                                editText2.setText(defaultAngle);
                                process(editText.getText().toString(), editText1.getText().toString(), editText2.getText().toString()); //光柵處理
                            } else if (editText.getText().toString().matches("") || editText1.getText().toString().matches("") || editText2.getText().toString().matches("")) { // 其中一個值的時候
                                Toast toast = Toast.makeText(PicAnalysisActivity.this, "您少輸入其中一個欄位請再確認!", Toast.LENGTH_LONG);
                                toast.show();
//                                process(editText.getText().toString(), editText1.getText().toString()); //光柵處理
                            } else {
                                process(editText.getText().toString(), editText1.getText().toString(), editText2.getText().toString()); //光柵處理
                            }

                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    }).show();


                    //影像分析處理fun
                    // Mat src = Imgcodecs.imread(String.valueOf(mCurrentPhotoPath)); //載入呼叫定義
                   /* Mat src = Imgcodecs.imread(fileUri.getPath());
                    Mat dst = new Mat(); //輸出呼叫定義
                    Mat result = new Mat();
                    Bitmap bm = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);*/

                    /*if (src == null) {
                        Toast.makeText(PicAnalysisActivity.this, "沒有照片讀取至分析圖像", Toast.LENGTH_SHORT).show();
                    } else {*/

//                    process(); //光柵處理
//                    procSrc2Gray(); // 灰階影像處理測試
//                    imageContrast(); // 顏色相反處理

                    // Utils.bitmapToMat(bm, result); //來源圖bm轉換到result
                    // Imgproc.cvtColor(src, result, Imgproc.COLOR_BGR2GRAY);//src to gray result

                   /* Utils.matToBitmap(result, bm); // result轉換回圖片bm

                    //release memory
                    src.release();
                    dst.release();
                    result.release();

                    //顯示分析後的圖片
                    mImg.setImageBitmap(bm);*/

//                    Toast.makeText(PicAnalysisActivity.this, "照片準備進行分析", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PicAnalysisActivity.this, "沒有選擇的照片，請拍照或是相簿選取", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    //光柵影像處理
    private void process(String widthByInput, String dpiByInput, String angleByInput) {
        double widthByInputChangeDouble = Double.parseDouble(widthByInput);
        double dpiByInputChangeDouble = Double.parseDouble(dpiByInput);
        double angleByInputChangeDouble = Double.parseDouble(angleByInput);

        Mat src = Imgcodecs.imread(fileUri.getPath());
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2BGR);

        int bars = (int) (dpiByInputChangeDouble / 2.54 * widthByInputChangeDouble);
        // 密度(DPI) = 101.6
        // 寬度 = 9

        double bars_double = (double) (dpiByInputChangeDouble / 2.54 * widthByInputChangeDouble);
        int width = src.cols();
        int height = src.rows();
        Size src_sz = new Size(width, height);
        //int bar_height = height / bars;

        //影像預旋轉
        Mat rotMat = new Mat(2, 3, CvType.CV_32FC1);
        //Mat im_new_rotate = Mat.zeros(new Size(width*2, height*2), src.type());
        Mat dist_rotate = new Mat(src.rows(), src.cols(), src.type());
        Point center = new Point(src.cols() / 2, src.rows() / 2);
        rotMat = Imgproc.getRotationMatrix2D(center, angleByInputChangeDouble, 1);
        Imgproc.warpAffine(src, dist_rotate, rotMat, src.size());

        //光柵取樣處理
        //Log.d(TAG, "bars=" + String.valueOf(bars) + "width=" + String.valueOf(width) + "height=" + String.valueOf(height) + "bar_width=" + String.valueOf(bar_width));

        Mat im_new_rotate = Mat.zeros(src_sz, src.type());
        Mat dist = new Mat(src.rows(), bars, src.type());
        //Imgproc.resize(src, dist, dist.size(),1/(height/bars_double), 1, 0); //光柵水平解析
        //Imgproc.resize(dist, im_new, im_new.size(),(height/bars_double), 1, 0);  //光柵水平解析
        Imgproc.resize(dist_rotate, dist, dist.size(), 1, 1 / (src.cols() / bars_double), 0);//光柵垂直解析版
        Imgproc.resize(dist, im_new_rotate, im_new_rotate.size(), 1, (src.cols() / bars_double), 0);//光柵垂直解析版


        Mat im_new = Mat.zeros(src.size(), src.type());
        //Mat rotMat = new Mat(2, 3, CvType.CV_32FC1);
        //Mat im_new_rotate = Mat.zeros(new Size(width*2, height*2), src.type());
        //Mat dist_rotate = new Mat(src.rows(), src.cols(), src.type());
        //Point center = new Point(src.cols() / 2, src.rows() / 2);
        rotMat = Imgproc.getRotationMatrix2D(center, -(angleByInputChangeDouble), 1);
        Imgproc.warpAffine(im_new_rotate, im_new, rotMat, src.size());


/*        for (int bar = 0; bar <= bars; bar++) {

            int y = bar_height * bar;
            //int y_1 = y + bar_height / 5 * 2;
            Rect roi = new Rect(0, y, width, bar_height / 5);
            Mat cropped = new Mat(src, roi); //新影像
            //Utils.bitmapToMat(bm, src); //來源圖bm轉換到result
            Rect newroi = new Rect(0, y, width, bar_height);
            Mat dist = new Mat(width, bar_height, src.type());
            Size sz = new Size(width, bar_height);
            Imgproc.resize(cropped, dist, dist.size(),1, 5, 0);

            int y_max = y + bar_height;

            if (y_max > height) {
                y_max = height;
            }
            Log.e("TESS", "迴圈有跑完");
            dist.copyTo(im_new.adjustROI(y, height - y_max, 0, 0));
            cropped.release();
            dist.release();
        }*/

        // Bitmap bm = Bitmap.createBitmap(im_new.cols(), im_new.rows(), Bitmap.Config.ARGB_8888);
        Bitmap bm = Bitmap.createBitmap(im_new.width(), im_new.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(im_new, bm); // result轉換回圖片bm

        // 20200707--image resize test
        // Bitmap too large to be uploaded into a texture
        // Bitmap bm = BitmapFactory.decodeFile(imagePath);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        if (bm.getWidth() <= screenWidth) {
            mImg.setImageBitmap(bm);
        } else {
            Bitmap bmp = Bitmap.createScaledBitmap(bm, screenWidth, bm.getHeight() * screenWidth / bm.getWidth(), true);
            mImg.setImageBitmap(bmp);
        }

        MediaStore.Images.Media.insertImage(getContentResolver(), bm, String.valueOf(fileUri), "光柵處理後圖片存取"); // 20200713--add
        Toast toast = Toast.makeText(this, "光柵解析完成並且已經存取至相簿內!", Toast.LENGTH_LONG);
        toast.show();

        // 顯示分析後的圖片
        // mImg.setImageBitmap(bm);
    }

    public void procSrc2Gray() { // 將圖片變成灰階的運算方式
        Mat src = Imgcodecs.imread(fileUri.getPath());
        Mat dst = new Mat(src.rows(), src.cols(), src.type());
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2GRAY); // 轉換成灰階效果
        // Imgcodecs.imwrite(fileUri.getPath(), dst);

        //當前Mat與Bitmap轉換，只支援ARGB_8888和RGB_565
        Bitmap bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, bitmap);

        // 20200707--image resize test
        // Bitmap too large to be uploaded into a texture
        // Bitmap bm = BitmapFactory.decodeFile(imagePath);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        if (bitmap.getWidth() <= screenWidth) {
            mImg.setImageBitmap(bitmap);
        } else {
            Bitmap bmp = Bitmap.createScaledBitmap(bitmap, screenWidth, bitmap.getHeight() * screenWidth / bitmap.getWidth(), true);
            mImg.setImageBitmap(bmp);
        }

        // 顯示分析後的圖片
        // mImg.setImageBitmap(bitmap);
    }

    public void imageContrast() {
        Mat src = Imgcodecs.imread(fileUri.getPath());
        Mat destination = new Mat(src.rows(), src.cols(), src.type(), new Scalar(255, 255, 255));
        Mat dst = new Mat(src.rows(), src.cols(), src.type(), new Scalar(255, 255, 255));
        Core.bitwise_xor(src, destination, dst);

        //當前Mat與Bitmap轉換，只支援ARGB_8888和RGB_565
        Bitmap bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, bitmap, true);//新增透明度

        // 20200707--image resize test
        // Bitmap too large to be uploaded into a texture
        // Bitmap bm = BitmapFactory.decodeFile(imagePath);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        if (bitmap.getWidth() <= screenWidth) {
            mImg.setImageBitmap(bitmap);
        } else {
            Bitmap bmp = Bitmap.createScaledBitmap(bitmap, screenWidth, bitmap.getHeight() * screenWidth / bitmap.getWidth(), true);
            mImg.setImageBitmap(bmp);
        }

        //顯示分析後的圖片
        //mImg.setImageBitmap(bitmap);

    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }*/

    //openCV4Android 需要加载用到
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
//                    mOpenCvCameraView.enableView();
//                    mOpenCvCameraView.setOnTouchListener(ColorBlobDetectionActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    /**
     * 判斷使用者是否有權限，並且進行處理
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        if (requestCode == 200) {
            //用户允續權限
            if (grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "用户已經允許權限，準備啟動相機。");
                //啟動相機
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            } else {  //用户拒绝
                Log.d(TAG, "用户已經拒絕權限，程序终止。");
                Toast.makeText(this, "程序需要写入权限才能运行", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //拍照完畢或選取圖片後呼叫此函式
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Uri selectedImage = data.getData();

        //藉由requestCode判斷是否為開啟相機或開啟相簿而呼叫的，且data不為null
                /*if (data != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    mImg.setImageBitmap(photo);
                }*/
        //Bitmap photo = (Bitmap) data.getExtras().get("data");

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            if (extras != null) {
            galleryAddPic();


            //圖片解析成Bitmap
            Bitmap imageBitmap = BitmapFactory.decodeFile(fileUri.getPath());

//            saveToInternalStorage(imageBitmap); // 20200713--add(將照片存取至相簿測試)

               /* Bitmap imageBitmap = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(imageUri));*/

            //判斷照片為橫向或者為直向，並進入ScalePic判斷圖片是否要進行縮放
            if (imageBitmap.getWidth() > imageBitmap.getHeight()) ScalePic(imageBitmap,
                    mPhone.heightPixels);
            else ScalePic(imageBitmap, mPhone.widthPixels);

//            mImg.setImageBitmap(imageBitmap);
        }

        if (requestCode == PHOTO && data != null && resultCode == RESULT_OK) {
            //Uri uri = data.getData();//取得照片路徑uri

            fileUri = data.getData();//取得照片路徑uri
            File f = new File(ImageSelectUtils.getRealPath(fileUri, getApplicationContext())); //讀取影像路徑
            fileUri = Uri.fromFile(f); //讀取影像路徑

            if (fileUri == null) {
                Toast toast = Toast.makeText(this, "選取照片的路徑為空值", Toast.LENGTH_LONG);
                toast.show();
            } else {

                try {

                    ContentResolver cr = this.getContentResolver();
                    //讀取照片，型態為Bitmap
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(fileUri));

                    //Bitmap imageBitmap = BitmapFactory.decodeFile(fileUri.getPath());

                    //判斷照片為橫向或者為直向，並進入sScalePic判斷圖片是否要進行縮放
                    if (bitmap.getWidth() > bitmap.getHeight()) ScalePic(bitmap,
                            mPhone.heightPixels);
                    else ScalePic(bitmap, mPhone.widthPixels);
                } catch (FileNotFoundException ignored) {
                }
            }
        }

        /*if (requestCode == ANALYSISRESULT) { //等待編輯
            galleryAddPic();
        }*/

        super.onActivityResult(requestCode, resultCode, data);
    }

    private File createImageFile() throws IOException { //將照片存取手機硬碟中
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";


//        getExternalFilesDir() 與getExternalStorageDirectory()
/*      1.getExternalFilesDir
        It returns the path to files folder inside Android / data / data / your_package / on your SD
        card.It is used to store any required files for your app (e.g.images downloaded from web or
        cache files).Once the app is uninstalled, any data stored in this folder is gone too.

        2.getExternalStorageDirectory
        It returns the root path to your SD card(e.g mnt / sdcard /).If you save data on this
        path and uninstall the app, that data won 't be lost.*/


        // 外部私有目錄(如果用戶卸載(uninstall)應用程序，這個目錄及其所有內容將被刪除)
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File storageDir = Environment.getExternalStorageDirectory(); // 外部公有目錄

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        fileUri = Uri.parse(image.getAbsolutePath());
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(fileUri.getPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    //    If you are using an ImageView to display the Bitmap returned by Camera Intent you need to
    //    save the imageview reference inside onSaveInstanceState and also restore it later on inside
    //    onRestoreInstanceState.
/*    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        // System.out.println(mCurrentPhotoPath);
        mImg = (ImageView) findViewById(R.id.imageView2);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);
        // System.out.println(mCurrentPhotoPath);
        mImg = (ImageView) findViewById(R.id.imageView2);
    }*/

    // 20200706--add
    public void clearAllResources() {
        // Set related variables null
        System.gc();
        Runtime.getRuntime().gc();
    }

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
            mImg.setImageBitmap(mScaleBitmap);
        }
//        照片顯示於手機上
        else mImg.setImageBitmap(bitmap);
    }

    // 20200713--add(新增toolbar功能列)
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main2, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//
//            case R.id.action_settings: //點了重新整理
////                saveToInternalStorage(imageBitmap);
//
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

/*    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImg.getWidth();
        int targetH = mImg.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        mImg.setImageBitmap(bitmap);
    }*/
}
