package com.haoche51.buyerapp.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;

public class HCFileUtils {

  /**
   * 把图片压缩到200K
   *
   * @param oldpath
   *            压缩前的图片路径
   * @param newPath
   *            压缩后的图片路径
   * @return
   */
  /**
   * 把图片压缩到200K
   *
   * @param oldpath 压缩前的图片路径
   * @param newPath 压缩后的图片路径
   */
  public static File compressFile(String oldpath, String newPath) {
    Bitmap compressBitmap = HCFileUtils.decodeFile(oldpath);
    Bitmap newBitmap = ratingImage(oldpath, compressBitmap);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    newBitmap.compress(CompressFormat.PNG, 100, os);
    byte[] bytes = os.toByteArray();

    File file = null;
    try {
      file = HCFileUtils.getFileFromBytes(bytes, newPath);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (newBitmap != null) {
        if (!newBitmap.isRecycled()) {
          newBitmap.recycle();
        }
        newBitmap = null;
      }
      if (compressBitmap != null) {
        if (!compressBitmap.isRecycled()) {
          compressBitmap.recycle();
        }
        compressBitmap = null;
      }
    }
    return file;
  }

  private static Bitmap ratingImage(String filePath, Bitmap bitmap) {
    int degree = readPictureDegree(filePath);
    return rotatingImageView(degree, bitmap);
  }

  /**
   * 旋转图片
   *
   * @return Bitmap
   */
  public static Bitmap rotatingImageView(int angle, Bitmap bitmap) {
    //旋转图片 动作
    Matrix matrix = new Matrix();
    matrix.postRotate(angle);
    System.out.println("angle2=" + angle);
    // 创建新的图片
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

  }

  /**
   * 读取图片属性：旋转的角度
   *
   * @param path 图片绝对路径
   * @return degree旋转的角度
   */
  public static int readPictureDegree(String path) {
    int degree = 0;
    try {
      ExifInterface exifInterface = new ExifInterface(path);
      int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
          ExifInterface.ORIENTATION_NORMAL);
      switch (orientation) {
        case ExifInterface.ORIENTATION_ROTATE_90:
          degree = 90;
          break;
        case ExifInterface.ORIENTATION_ROTATE_180:
          degree = 180;
          break;
        case ExifInterface.ORIENTATION_ROTATE_270:
          degree = 270;
          break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return degree;
  }

  /**
   * 把字节数组保存为一个文件
   */
  public static File getFileFromBytes(byte[] b, String outputFile) {
    File ret = null;
    BufferedOutputStream stream = null;
    try {
      ret = new File(outputFile);
      FileOutputStream fstream = new FileOutputStream(ret);
      stream = new BufferedOutputStream(fstream);
      stream.write(b);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return ret;
  }

  /**
   * 图片压缩
   */
  public static Bitmap decodeFile(String fPath) {
    BitmapFactory.Options opts = new BitmapFactory.Options();
    opts.inJustDecodeBounds = true;
    opts.inDither = false; // Disable Dithering mode
    opts.inPurgeable = true; // Tell to gc that whether it needs free
    opts.inInputShareable = true; // Which kind of reference will be used to
    BitmapFactory.decodeFile(fPath, opts);
    final int REQUIRED_SIZE = 200;
    int scale = 1;
    if (opts.outHeight > REQUIRED_SIZE || opts.outWidth > REQUIRED_SIZE) {
      final int heightRatio = Math.round((float) opts.outHeight / (float) REQUIRED_SIZE);
      final int widthRatio = Math.round((float) opts.outWidth / (float) REQUIRED_SIZE);
      scale = heightRatio < widthRatio ? heightRatio : widthRatio;//
    }
    opts.inJustDecodeBounds = false;
    opts.inSampleSize = scale;
    return BitmapFactory.decodeFile(fPath, opts).copy(Config.ARGB_8888, false);

  }

  /**
   * 创建目录
   */
  public static void setMkdir(String path) {
    File file = new File(path);
    if (!file.exists()) {
      file.mkdirs();
    }
  }

  /**
   * 获取目录名称
   *
   * @return FileName
   */
  public static String getFileName(String url) {
    int lastIndexStart = url.lastIndexOf("/");
    if (lastIndexStart != -1) {
      return url.substring(lastIndexStart + 1, url.length());
    } else {
      return null;
    }
  }

  /**
   * 删除该目录下的文件
   */
  public static void delFile(String path) {
    if (!TextUtils.isEmpty(path)) {
      File file = new File(path);
      if (file.exists()) {
        file.delete();
      }
    }
  }
}
