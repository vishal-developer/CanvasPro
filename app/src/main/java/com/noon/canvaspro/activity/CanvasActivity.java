package com.noon.canvaspro.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.noon.canvaspro.R;
import com.noon.canvaspro.customview.MCanvasView;
import com.noon.canvaspro.util.FilePathUtil;

public class CanvasActivity extends AppCompatActivity {

    public static final String TAG = CanvasActivity.class.getSimpleName();
    MCanvasView mCanvasView;
    public static int GALLERY_REQ_CODE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {/*Manifest.permission.WRITE_EXTERNAL_STORAGE,*/Manifest.permission.READ_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);
        verifyStoragePermissions();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
    }

    private void init(){
        mCanvasView = (MCanvasView)findViewById(R.id.draw_view);
        Button btnUndo, btnRedo, btnColors, btnSave;
        btnUndo = (Button)findViewById(R.id.btn_undo);
        btnUndo.setOnClickListener(clickListener);

        btnRedo = (Button)findViewById(R.id.btn_redo);
        btnRedo.setOnClickListener(clickListener);

        btnColors = (Button)findViewById(R.id.btn_color);
        btnColors.setOnClickListener(clickListener);

        btnSave = (Button)findViewById(R.id.btn_import);
        btnSave.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_undo:
                    mCanvasView.doUndo();
                    break;
                case R.id.btn_redo:
                    mCanvasView.doRedo();
                    break;
                case R.id.btn_color:
                    mCanvasView.drawText("Yes");
                    mCanvasView.setColor(Color.RED);
                    break;
                case R.id.btn_import:
                    onGalleryBtnClick();
                    break;
            }

        }
    };

    public void onGalleryBtnClick() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQ_CODE);
    }
    public  void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Cannot write images to external storage", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path = "";
        if (requestCode == GALLERY_REQ_CODE && resultCode == RESULT_OK && data != null) {
            // Let's read picked image data - its URI
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(filePath[0]));


            cursor.close();
        }
        mCanvasView.addBitmap(FilePathUtil.getImageFromPath(path));
        Log.d(TAG, "path: " + path);
    }
}
