package gloomyfish.opencvdemo;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.book.chapter.seven.PicAnalysisActivity;

import org.opencv.android.OpenCVLoader;

public class MainPageActivity extends AppCompatActivity {

    private static final String CV_TAG = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        iniLoadOpenCV();

        getSupportActionBar().hide();
        //        action bar隱藏

        Button btn1 = (Button) findViewById(R.id.button);
        Button btn2 = (Button) findViewById(R.id.button5);
        Button btn3 = (Button) findViewById(R.id.button3);
        Button btn4 = (Button) findViewById(R.id.button4);

        //        行動相機解密點擊
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPageActivity.this, PicAnalysisActivity.class);
                startActivity(intent);
            }
        });

        //        影像上傳解密點擊
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPageActivity.this, ImageUploadAndDecryptionActivity.class);
                startActivity(intent);
            }
        });

        //        產品資訊查詢點擊
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPageActivity.this, ProductInformationQueryActivity.class);
                startActivity(intent);
            }
        });

        //        使用說明點擊
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPageActivity.this, InstructionsForUseActivity.class);
                startActivity(intent);
            }
        });

    }

    private void iniLoadOpenCV() {
        boolean success = OpenCVLoader.initDebug();
        if (success) {
            Log.i(CV_TAG, "OpenCV Libraries loaded...");
        } else {
            Toast.makeText(this.getApplicationContext(), "WARNING: Could not load OpenCV Libraries!", Toast.LENGTH_LONG).show();
        }
    }
}
