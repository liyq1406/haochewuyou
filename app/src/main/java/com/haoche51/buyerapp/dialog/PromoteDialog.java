package com.haoche51.buyerapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.WebBrowserActivity;
import com.haoche51.buyerapp.entity.HCPromoteEntity;
import com.haoche51.buyerapp.helper.ImageLoaderHelper;
import com.haoche51.buyerapp.util.DialogUtils;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class PromoteDialog {
  private Activity activity;
  private HCPromoteEntity entity;

  public PromoteDialog(Activity activity, HCPromoteEntity entity) {
    this.activity = activity;
    this.entity = entity;
  }

  public void showPromoteDialog() {
    int promote_id = entity.getId();
    String pic_url = entity.getImage_url();
    int sp_id = HCSpUtils.getPromoteId();

    if (promote_id == 0 || sp_id == promote_id || TextUtils.isEmpty(pic_url)) {
      return;
    }

    final Activity[] mAct = new Activity[1];
    mAct[0] = activity;

    final Bitmap[] bitmaps = new Bitmap[1];

    int layoutId = R.layout.dialog_promote;
    View contentView = LayoutInflater.from(mAct[0]).inflate(layoutId, null);
    ImageView mCancelIv = (ImageView) contentView.findViewById(R.id.iv_promote_cancel);
    final ImageView mPicIv = (ImageView) contentView.findViewById(R.id.iv_promote_pic);

    mPicIv.getLayoutParams().width = DialogUtils.PROMOTE_PIC_WIDTH;
    mPicIv.getLayoutParams().height = DialogUtils.PROMOTE_PIC_HEIGHT;

    pic_url = HCUtils.averageImageURL(pic_url, DialogUtils.PROMOTE_PIC_WIDTH,
        DialogUtils.PROMOTE_PIC_HEIGHT);

    final Dialog dialog = new Dialog(mAct[0], R.style.normal_dialog);
    dialog.setContentView(contentView);
    Window window = dialog.getWindow();
    WindowManager.LayoutParams lp = window.getAttributes();
    lp.width = HCUtils.getScreenWidthInPixels();
    lp.height = HCUtils.getScreenHeightPixels();

    ImageLoader.getInstance()
        .loadImage(pic_url, ImageLoaderHelper.getBannerOptions(R.drawable.default_template), new SimpleImageLoadingListener() {
          @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            super.onLoadingComplete(imageUri, view, loadedImage);
            if (mPicIv != null && loadedImage != null && mAct[0] != null
                && !mAct[0].isFinishing()) {
              mPicIv.setImageBitmap(loadedImage);
              bitmaps[0] = loadedImage;
              dialog.show();
            }
          }
        });

    mCancelIv.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dialog.dismiss();
      }
    });

    mPicIv.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dialog.dismiss();
        WebBrowserActivity.urlToThis(mAct[0], entity.getUrl());
      }
    });

    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
      @Override public void onDismiss(DialogInterface dialog) {
        mAct[0] = null;
        if (bitmaps[0] != null) {
          if (!bitmaps[0].isRecycled()) {
            //bitmaps[0].recycle();
            bitmaps[0] = null;
          }
        }

        HCSpUtils.setPromoteId(entity.getId());
      }
    });
  }
}
