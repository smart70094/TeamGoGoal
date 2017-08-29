package com.example.teamgogoal.teamgogoal;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by MazeR on 2017/8/23.
 * Author: MazeR
 * E-mail: mazer0701@gmail.com
 * Description: tempFileManager is a Class that transform Uri Object to filePath and manager it
 */

public class tempFileManager {

    private Uri fileUri = null;
    private String filePath = null;
    private final String LOG_TAG = "tmpFileManagerLog";

    public tempFileManager(Uri uri) {
        this.fileUri = uri;
    }

    public tempFileManager() {
    }

    public Uri getFileUri(){
        return this.fileUri;
    }

    public void setFileUri(Uri uri) {
        this.fileUri = uri;
    }

    public void uri2tempFile(Context context) {
        String syspath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File mainPath = new File(syspath + "/imgTemp");

        if (!mainPath.exists()) {
            Log.d(LOG_TAG, "mkdirs for temp");
            mainPath.mkdirs();
        }else{
            Log.d(LOG_TAG, "already have temp dir");
        }
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getExtensionFromMimeType(cR.getType(this.fileUri));

        try{
            java.io.InputStream in = cR.openInputStream(this.fileUri);
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            File targetFile = new File(mainPath + "/temp." + type);
            java.io.OutputStream outStream = new java.io.FileOutputStream(targetFile);
            outStream.write(buffer);
            this.filePath = mainPath + "/temp." + type;
        }catch (Exception e){
            Log.d(LOG_TAG, e.toString());
        }

    }

    public String getFilePath(){
        return this.filePath;
    }

    public void destory(){
        try{
            File file = new File(this.filePath);
            if(file.delete()){
                Log.d(LOG_TAG, file.getName() + " is deleted!");
            }else{
                Log.e(LOG_TAG, "Delete operation is failed.");
            }
        }catch(Exception e){
            Log.e(LOG_TAG, e.toString());
        }
        this.filePath = null;
        this.fileUri = null;
    }
}
