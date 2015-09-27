package in.buzzzz.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.buzzzz.R;
import in.buzzzz.utility.AlbumStorageDirFactory;
import in.buzzzz.utility.AppConstants;
import in.buzzzz.utility.BaseAlbumDirFactory;
import in.buzzzz.utility.Logger;

public class CameraGalleryActivity extends BaseActivity {
    public static final int RESULT_REMOVED = 2001;
    private static final int ACTION_CAMERA = 1001;
    private static final int ACTION_GALLERY = 1002;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private static final String TAG = "CameraGalleryActivity";
    boolean isCameraStatus;
    private String mCurrentPhotoPath;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_camera:
                    dispatchTakePictureIntent(ACTION_CAMERA);
                    break;
                case R.id.button_gallery:
                    openGallery(ACTION_GALLERY);
                    break;
                case R.id.textview_remove:
                    removeImage();
                    break;
                case R.id.linearlayout_container:
                    onBackPressed();
                default:
                    break;
            }
        }
    };

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.app_name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_gallery);

        // ApptimizeUtils apptimizeUtils = new ApptimizeUtils(this);
        mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        linkViewsId();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(AppConstants.FILE_PATH, mCurrentPhotoPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCurrentPhotoPath = savedInstanceState.getString(AppConstants.FILE_PATH);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void linkViewsId() {
        Button buttonCamera = (Button) findViewById(R.id.button_camera);
        Button buttonGallery = (Button) findViewById(R.id.button_gallery);
        TextView textViewRemove = (TextView) findViewById(R.id.textview_remove);
        buttonCamera.setOnClickListener(onClickListener);
        buttonGallery.setOnClickListener(onClickListener);
        textViewRemove.setOnClickListener(onClickListener);
        if (getIntent().hasExtra(AppConstants.EXTRA_IS_REMOVE_IMAGE)) {
            if (getIntent().getBooleanExtra(AppConstants.EXTRA_IS_REMOVE_IMAGE, false)) {
                removeComponentVisibility(View.VISIBLE, textViewRemove);
            } else {
                removeComponentVisibility(View.GONE, textViewRemove);
            }
        } else {
            removeComponentVisibility(View.GONE, textViewRemove);
        }
        findViewById(R.id.linearlayout_container).setOnClickListener(onClickListener);
    }

    private void removeComponentVisibility(int visiblity, TextView textViewRemove) {
        textViewRemove.setVisibility(visiblity);
    }

    private void removeImage() {
        Intent intent = new Intent();
        setResult(RESULT_REMOVED, intent);
        finish();
    }

    private void dispatchTakePictureIntent(int actionCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        switch (actionCode) {
            case ACTION_CAMERA:
                File f = null;
                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;
            default:
                break;
        } // switch
        startActivityForResult(takePictureIntent, actionCode);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {
        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();
        return f;
    }

    private File getAlbumDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Logger.i(TAG, "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Logger.i(TAG, "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.i("camera gallery", resultCode + "@");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ACTION_CAMERA:
                    isCameraStatus = true;
                    handleCameraPic(isCameraStatus);
                    break;
                case ACTION_GALLERY:
                    isCameraStatus = false;
                    // For fabric crash fix #3133 java.lang.NullPointerException
                    if (data != null) {
                        Uri picUri = data.getData();
                        handleGalleryPic(picUri, isCameraStatus);
//                    sendImage(data.getExtras().getString(AppConstants.EXTRA_IMAGE_PATH));
                    }
                    break;
            }
        }
    }

    private void handleGalleryPic(Uri picUri, boolean camPictureStatus) {
        if (picUri != null) {
            mCurrentPhotoPath = getPath(picUri);
            if (mCurrentPhotoPath != null) {
                sendImage(mCurrentPhotoPath, camPictureStatus);
            } else {
                Log.e(TAG, "Image path not found. Image not found in the gallery.");
            }
        }
    }

    private void sendImage(String path, boolean camPictureStatus) {
        Intent intent = new Intent();
        intent.putExtra(AppConstants.EXTRA_IMAGE_PATH, path);
        intent.putExtra(AppConstants.EXTRA_CAMERAPIC_STATUS, camPictureStatus);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void handleCameraPic(boolean pictureStatus) {
        if (mCurrentPhotoPath != null) {
            galleryAddPic();
            sendImage(mCurrentPhotoPath, pictureStatus);
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void openGallery(int req_code) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.msg_select_file)), req_code);
//        Intent intent = new Intent(this, CustomGalleryActivity.class);
////        intent.setAction(Intent.ACTION_PICK);
////        intent.setType("image/*");
////        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, req_code);
    }

    public String getPath(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri,
                filePathColumn, null, null, null);
        String picturePath = null;
        // Fix for crash - null pointer exception , check if cursor is not null before using it.
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return picturePath;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.slide_in, R.animator.slide_out);
    }
}
