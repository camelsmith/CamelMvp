package com.camel.mvp.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by CamelLuo on 2018/1/19.
 */

public class UriUtils {

    /**
     * 定义一个方法用于将图片的绝对路径转换为媒体Uri路径
     * /storage/emulated/0/PictureSelector/CameraImage/PictureSelector_20180119_093723.JPEG
     * content://media/external/images/media/3045
     * @param context 上下文对象
     * @param path 图片绝对路径
     * @return
     */
    public static Uri getMediaUriFromPath(Context context, String path) {
        Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(mediaUri,
                null,
                MediaStore.Images.Media.DISPLAY_NAME + "= ?",
                new String[] {path.substring(path.lastIndexOf("/") + 1)},
                null);

        Uri uri = null;
        if(cursor.moveToFirst()) {
            uri = ContentUris.withAppendedId(mediaUri,
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
        }
        cursor.close();
        return uri;
    }

    /**
     * 定义一个方法用于将文件绝对路径转换为文件Uri
     * @param imageName
     * @return
     */
    public static Uri getImageStreamFromExternal(String imageName) {
        File externalPubPath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
        );

        File picPath = new File(externalPubPath, imageName);
        Uri uri = null;
        if(picPath.exists()) {
            uri = Uri.fromFile(picPath);
        }

        return uri;
    }

}
