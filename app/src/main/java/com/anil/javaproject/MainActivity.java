package com.anil.javaproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.anil.javaproject.databinding.ActivityMainBinding;
import com.apex.bluetooth.utils.LogUtils;
import com.example.custom_dial.CustomDialCallback;
import com.example.custom_dial.CustomDiffTxtColorDialParam;
import com.example.custom_dial.RGBAPlatformDiffTxtUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final int IMAGE_REQUEST_CODE = 0;
    private Bitmap backBitmap;
    RGBAPlatformDiffTxtUtils rgbaPlatformDiffTxtUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, IMAGE_REQUEST_CODE);

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    LogUtils.d("MainActivity","IMAGE_REQUEST_CODE ");
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();
                    Log.d("MainActivity", "resulturi " + resultUri);
                    if (resultUri == null) {
                        return;
                    }
                    BitmapFactory.Options options2 = new BitmapFactory.Options();
                    options2.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    backBitmap = BitmapFactory.decodeFile(resultUri.getPath(), options2);
                    Log.d("MainActivity", "backBitmap " + backBitmap.getHeight() +" "+ backBitmap.isRecycled()+" Memory Used in bytes "+backBitmap.getByteCount());
                    Toast.makeText(MainActivity.this, "backBitmap "+backBitmap.isRecycled(), Toast.LENGTH_SHORT).show();
                    produceDial();
                    if (backBitmap == null) {
                        return;
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void startPhotoZoom(Uri uri) {
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setRequestedSize(466, 466, CropImageView.RequestSizeOptions.RESIZE_EXACT)//解决有些图片裁剪得出239*239而导致更换不了表盘背景的问题
                .setMinCropResultSize(466, 466)
                .setAspectRatio(466, 466)//根据手表屏幕长方形或正方形进行背景裁剪，避免背景图被拉伸
                .start(this);

    }

    private void produceDial(){
        RGBAPlatformDiffTxtUtils rgbaPlatformDiffTxtUtils;
        rgbaPlatformDiffTxtUtils = new RGBAPlatformDiffTxtUtils(MainActivity.this, true);
        rgbaPlatformDiffTxtUtils.showData(true);
        CustomDiffTxtColorDialParam customDiffTxtColorDialParam = new CustomDiffTxtColorDialParam();
        customDiffTxtColorDialParam.setBackBitmap(backBitmap);
        customDiffTxtColorDialParam.setTxtColor(Color.BLACK);
        customDiffTxtColorDialParam.setpHigh(280);
        customDiffTxtColorDialParam.setpWidth(280);
        customDiffTxtColorDialParam.setScreenHigh(466);
        customDiffTxtColorDialParam.setScreenWidth(466);
        customDiffTxtColorDialParam.setCornerRadius(0);
        customDiffTxtColorDialParam.setScreenType(0);
        customDiffTxtColorDialParam.setStartX(127);
        customDiffTxtColorDialParam.setStartY(30);
        customDiffTxtColorDialParam.setWx(140);
        customDiffTxtColorDialParam.setWy(140);
        customDiffTxtColorDialParam.setDateX(260);

        rgbaPlatformDiffTxtUtils.produceDialBin(customDiffTxtColorDialParam, new CustomDialCallback() {
            @Override
            public void dialPath(String dPath) {
                if (TextUtils.isEmpty(dPath)) {
                    Toast.makeText(MainActivity.this, "dpath is null", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, dPath, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}