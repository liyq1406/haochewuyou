package com.haoche51.buyerapp.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.haoche51.buyerapp.util.HCFileUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCViewClickListener;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.HCBannerEntity;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.ShareUtils;
import java.io.File;

public class WebBrowserActivity extends HCCommonTitleActivity {

  private final static String TAG = "hcWebBrowserActivity";

  private final static String HTTP = "http";

  private static final int FILECHOOSER_RESULTCODE = 1;
  private static final int REQ_CAMERA = FILECHOOSER_RESULTCODE + 1;
  private static final int REQ_CHOOSE = REQ_CAMERA + 1;

  private final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 11;
  private static final int REQ_CAMERA_RESULTCODE_FOR_ANDROID_5 =
      FILECHOOSER_RESULTCODE_FOR_ANDROID_5 + 1;
  private static final int REQ_CHOOSE_RESULTCODE_FOR_ANDROID_5 =
      REQ_CAMERA_RESULTCODE_FOR_ANDROID_5 + 1;
  private ValueCallback<Uri> mUploadMessage;
  private ValueCallback<Uri[]> mUploadMessageForAndroid5;
  private static final String DirName = "/haoche51/temp/";

  private LinearLayout mNetErrLinear;
  private WebView mWebView;
  private View mProgressView;
  private LinearLayout.LayoutParams mProgParams;
  private TextView mShareTv;
  private TextView mTitleTv;

  private String mShareTitle;
  private String mShareText;
  private String mSharePicUrl;
  private String mShareLinkUrl;
  private String mLoadURL;

  private boolean isFirstOnResume = true;

  private HCViewClickListener mClickListener = new HCViewClickListener() {
    @Override public void performViewClick(View v) {
      int id = v.getId();
      switch (id) {
        case R.id.linear_net_refresh://无网时刷新
          if (!HCUtils.isNetAvailable()) {
            mNetErrLinear.setVisibility(View.VISIBLE);
            HCUtils.toastNetError();
          } else {
            HCUtils.hideViewIfNeed(mNetErrLinear);
            mWebView.loadUrl(mLoadURL);
          }
          break;
      }
    }
  };

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
    String text = getIntent().getStringExtra(HCConsts.INTENT_KEY_TITLE);
    if (!TextUtils.isEmpty(text)) {
      titleTv.setText(text);
    }
    mShareTv = rightTv;
    mTitleTv = titleTv;
  }

  @Override public void handleBackTv(TextView mBackTv) {
    mBackTv.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        HCUtils.hideKeyboard(v);
        if (mWebView.canGoBack()) {
          mWebView.goBack();
        } else {
          finish();
        }
      }
    });
  }

  @SuppressLint("SetJavaScriptEnabled") @Override void initViews() {

    seeIfHasExtraData();

    mNetErrLinear = (LinearLayout) findViewById(R.id.linear_net_refresh);
    mNetErrLinear.setOnClickListener(mClickListener);
    mProgressView = findViewById(R.id.view_for_progress);
    mWebView = (WebView) findViewById(R.id.wv_common_browser);

    initWebView();

    //加载前判断网络状态
    if (!HCUtils.isNetAvailable()) {
      mNetErrLinear.setVisibility(View.VISIBLE);
      return;
    }
    HCUtils.hideViewIfNeed(mNetErrLinear);

    mWebView.loadUrl(mLoadURL);

    HCLog.d(TAG, "WebBrowserActivity loadURL = \n" + mLoadURL);
  }

  private void seeIfHasExtraData() {

    mLoadURL = getIntent().getStringExtra(HCConsts.INTENT_KEY_URL);

    String forumKey = HCConsts.INTENT_KEY_FORUM;
    boolean isFromForum = getIntent().hasExtra(forumKey);
    if (isFromForum) {
      mShareTv.setText("关闭");
      mShareTv.setTextSize(12);
      mShareTv.setVisibility(View.VISIBLE);
      mShareTv.setOnClickListener(new HCViewClickListener() {
        @Override public void performViewClick(View v) {
          finish();
        }
      });
      return;
    }
    String bannerKey = HCConsts.INTENT_KEY_BANNER;
    boolean isFromBanner = getIntent().hasExtra(bannerKey);
    if (isFromBanner) {
      HCBannerEntity enty = (HCBannerEntity) getIntent().getSerializableExtra(bannerKey);
      if (enty != null) {
        mShareTitle = enty.getShare_title();
        mShareText = enty.getShare_des();
        mSharePicUrl = enty.getShare_image();
        mShareLinkUrl = enty.getShare_link();
        if (TextUtils.isEmpty(mShareLinkUrl)) {
          mShareLinkUrl = mSharePicUrl;
        }
      }
    }
    seeIfNeedShare();
  }

  private void seeIfNeedShare() {
    if (!TextUtils.isEmpty(mShareTitle) && !TextUtils.isEmpty(mShareText) && !TextUtils.isEmpty(
        mSharePicUrl)) {
      mShareTv.setText("分享");
      mShareTv.setTextSize(15);
      mShareTv.setVisibility(View.VISIBLE);
      mShareTv.setOnClickListener(new HCViewClickListener() {
        @Override public void performViewClick(View v) {
          ShareUtils.share(WebBrowserActivity.this, mShareTitle, mShareText, mShareLinkUrl,
              mSharePicUrl);
        }
      });
    }
  }

  private void initWebView() {
    mWebView.getSettings().setDomStorageEnabled(true);
    mWebView.getSettings().setAllowFileAccess(true);
    try {
      mWebView.getSettings().setJavaScriptEnabled(true);
    } catch (Exception e) {
    }
    mWebView.getSettings().setBuiltInZoomControls(true);
    mWebView.getSettings().setSupportZoom(true);
    mWebView.getSettings().setUseWideViewPort(true);
    mWebView.getSettings().setLoadWithOverviewMode(true);
    mWebView.setWebViewClient(new WebViewClient() {
      // 这里可以设置使用哪种类型打开连接，当前内置or外部浏览器
      @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("tel:")) {
          Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
          startActivity(intent);
        } else {
          mLoadURL = url;
          //当前页面跳转时先判断网络状态。
          if (!HCUtils.isNetAvailable()) {
            mNetErrLinear.setVisibility(View.VISIBLE);
          } else {
            HCUtils.hideViewIfNeed(mNetErrLinear);
            view.loadUrl(url);
          }
        }
        return true;
      }
    });
    // 监听文件下载连接
    mWebView.setDownloadListener(new DownloadListener() {
      @Override public void onDownloadStart(String url, String userAgent, String contentDisposition,
          String mimetype, long contentLength) {
        // 监听下载功能，当用户点击下载链接的时候，直接调用系统的浏览器来下载
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
      }
    });

    final int sWidth = HCUtils.getScreenWidthInPixels();
    WebChromeClient chromeClient = new WebChromeClient() {
      public void onProgressChanged(WebView view, int progress) {

        //这里怎么会有mProgressView为null的情况
        if (mProgressView == null) return;

        final int width = sWidth * progress / 100;
        if (mProgParams == null) {
          mProgParams = (LinearLayout.LayoutParams) mProgressView.getLayoutParams();
          mProgParams.height = HCUtils.getDimenPixels(R.dimen.px_2dp);
          mProgressView.setVisibility(View.VISIBLE);
        }
        mProgParams.width = width;
        mProgressView.setLayoutParams(mProgParams);
        if (progress == 100) {
          mProgressView.setVisibility(View.GONE);
        }
      }

      @Override public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);

        if (getIntent().hasExtra(HCConsts.INTENT_KEY_TITLE)) {
          String intentTitle = getIntent().getStringExtra(HCConsts.INTENT_KEY_TITLE);
          String couponTitle = HCUtils.getResString(R.string.hc_my_coupon);
          if (couponTitle.equals(intentTitle)) {
            return;
          }
        }

        if (!TextUtils.isEmpty(title)) {
          mTitleTv.setText(title);
        }
      }

      // For Android 3.X
      public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        if (mUploadMessage != null) return;
        mUploadMessage = uploadMsg;
        selectImage();
      }

      // For Android < 3.0
      public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, "");
      }

      // For Android 4.X
      public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooser(uploadMsg, acceptType);
      }

      // For Android 5.X
      @Override public boolean onShowFileChooser(WebView webView,
          ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (mUploadMessageForAndroid5 != null) return true;
        mUploadMessageForAndroid5 = filePathCallback;
        selectImage();
        return true;
      }
    };
    mWebView.setWebChromeClient(chromeClient);
  }

  /**
   * 检查SD卡是否存在
   */
  private boolean checkSDCard() {
    boolean flag = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    if (!flag) {
      HCUtils.showToast("请插入手机存储卡再使用本功能");
    }
    return flag;
  }

  String compressPath = "";

  private void selectImage() {
    if (!checkSDCard()) return;
    String[] selectPicTypeStr = { "拍照", "从相册中选择" };
    Dialog dialog =
        new AlertDialog.Builder(this).setOnCancelListener(new DialogInterface.OnCancelListener() {
          @Override public void onCancel(DialogInterface dialog) {
            if (mUploadMessage != null) {
              mUploadMessage.onReceiveValue(null);
              mUploadMessage = null;
            }
            if (mUploadMessageForAndroid5 != null) {
              mUploadMessageForAndroid5.onReceiveValue(null);
              mUploadMessageForAndroid5 = null;
            }
          }
        }).setItems(selectPicTypeStr, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            switch (which) {
              // 相机拍摄
              case 0:
                openCamera();
                break;
              // 手机相册
              case 1:
                chosePic();
                break;
              default:
                break;
            }
            compressPath = Environment.getExternalStorageDirectory().getPath() + DirName;
            new File(compressPath).mkdirs();
            compressPath = compressPath + File.separator + "compress.jpg";
          }
        }).create();
    dialog.setCanceledOnTouchOutside(true);
    dialog.show();
  }

  String imagePaths;
  Uri cameraUri;

  /**
   * 打开照相机
   */
  private void openCamera() {
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    imagePaths =
        Environment.getExternalStorageDirectory().getPath() + DirName + (System.currentTimeMillis()
            + ".jpg");
    // 必须确保文件夹路径存在，否则拍照后无法完成回调
    File vFile = new File(imagePaths);
    if (!vFile.exists()) {
      File vDirPath = vFile.getParentFile();
      vDirPath.mkdirs();
    } else {
      if (vFile.exists()) {
        vFile.delete();
      }
    }
    cameraUri = Uri.fromFile(vFile);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
    if (mUploadMessage != null) {
      startActivityForResult(intent, REQ_CAMERA);
    } else if (mUploadMessageForAndroid5 != null) {
      startActivityForResult(intent, REQ_CAMERA_RESULTCODE_FOR_ANDROID_5);
    }
  }

  /**
   * 拍照结束后
   */
  private void afterOpenCamera() {
    File f = new File(imagePaths);
    addImageGallery(f);
    HCFileUtils.compressFile(f.getPath(), compressPath);
  }

  /** 解决拍照后在相册中找不到的问题 */
  private void addImageGallery(File file) {
    ContentValues values = new ContentValues();
    values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
    getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
  }

  /**
   * 本地相册选择图片
   */
  private void chosePic() {
    HCFileUtils.delFile(compressPath);
    Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
    String IMAGE_UNSPECIFIED = "image/*";
    innerIntent.setType(IMAGE_UNSPECIFIED); // 查看类型
    Intent wrapperIntent = Intent.createChooser(innerIntent, null);
    if (mUploadMessage != null) {
      startActivityForResult(wrapperIntent, REQ_CHOOSE);
    } else if (mUploadMessageForAndroid5 != null) {
      startActivityForResult(wrapperIntent, REQ_CHOOSE_RESULTCODE_FOR_ANDROID_5);
    }
  }

  /**
   * 选择照片后结束
   */
  private Uri afterChosePic(Intent data) {

    // 获取图片的路径：
    String[] proj = { MediaStore.Images.Media.DATA };
    // 好像是android多媒体数据库的封装接口，具体的看Android文档
    Cursor cursor = managedQuery(data.getData(), proj, null, null, null);
    if (cursor == null) {
      HCUtils.showToast("上传的图片仅支持png或jpg格式");
      return null;
    }
    // 按我个人理解 这个是获得用户选择的图片的索引值
    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    // 将光标移至开头 ，这个很重要，不小心很容易引起越界
    cursor.moveToFirst();
    // 最后根据索引值获取图片路径
    String path = cursor.getString(column_index);
    if (path != null && (path.endsWith(".png") || path.endsWith(".PNG") || path.endsWith(".jpg")
        || path.endsWith(".JPG"))) {
      File newFile = HCFileUtils.compressFile(path, compressPath);
      return Uri.fromFile(newFile);
    } else {
      HCUtils.showToast("上传的图片仅支持png或jpg格式");
    }
    return null;
  }

  /**
   * 返回文件选择
   */
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if (null == mUploadMessage && null == mUploadMessageForAndroid5) return;
    if (resultCode != Activity.RESULT_OK) {
      if (mUploadMessage != null) {
        mUploadMessage.onReceiveValue(null);//不加这段代码的话，第一次若取消第二次会报错
        mUploadMessage = null;//不加这句话第一次取消，第二次就不能点击
      }
      if (mUploadMessageForAndroid5 != null) {
        mUploadMessageForAndroid5.onReceiveValue(null);
        mUploadMessageForAndroid5 = null;
      }
      return;
    }
    if (mUploadMessage != null) {
      Uri uri = null;
      if (requestCode == REQ_CAMERA) {
        afterOpenCamera();
        uri = cameraUri;
      } else if (requestCode == REQ_CHOOSE) {
        uri = afterChosePic(intent);
      }
      if (uri != null) {
        mUploadMessage.onReceiveValue(uri);
      }
      mUploadMessage = null;
    } else if (mUploadMessageForAndroid5 != null) {
      Uri result = null;
      if (requestCode == REQ_CAMERA_RESULTCODE_FOR_ANDROID_5) {
        afterOpenCamera();
        result = cameraUri;
      } else if (requestCode == REQ_CHOOSE_RESULTCODE_FOR_ANDROID_5) {
        result = afterChosePic(intent);
      }
      if (result != null) {
        mUploadMessageForAndroid5.onReceiveValue(new Uri[] { result });
      }
      mUploadMessageForAndroid5 = null;
    }
    super.onActivityResult(requestCode, resultCode, intent);
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (mWebView.canGoBack()) {
        mWebView.goBack();
        return true;
      } else {
        finish();
      }
    }
    return false;
  }

  public static void urlToThis(Context context, String url) {

    if (TextUtils.isEmpty(url)) return;

    url = url.trim();

    if (!url.startsWith(HTTP)) return;

    Intent mIntent = new Intent(GlobalData.mContext, WebBrowserActivity.class);
    mIntent.putExtra(HCConsts.INTENT_KEY_URL, url);
    if (context instanceof Activity) {
      context.startActivity(mIntent);
    } else {
      mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(mIntent);
    }
  }

  /**
   * 解决webview退出时，视频播放不退出的问题
   */
  @Override protected void onPause() {
    super.onPause();
    doThirdPartyPause();
    if (mWebView != null) {
      mWebView.onPause();
    }
  }

  @Override protected void onResume() {
    super.onResume();
    doThirdPartyResume();
    if (mWebView != null) {
      mWebView.onResume();
    }

    if (isFirstOnResume) {
      isFirstOnResume = false;
      //通知MainActivity执行初始化
      if (mWebView != null) {
        mWebView.postDelayed(new Runnable() {
          @Override public void run() {
            HCEvent.postEvent(HCEvent.ACTION_WEBBROWSER_LOADED);
          }
        }, 200);
      }
    }
  }

  @Override int getContentViewResouceId() {
    return R.layout.activity_web_browser;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }

  private void doThirdPartyResume() {
    String name = this.getClass().getSimpleName();
    ThirdPartInjector.onPageStart(name);
    ThirdPartInjector.onResume(this);
  }

  private void doThirdPartyPause() {
    String name = this.getClass().getSimpleName();
    ThirdPartInjector.onPageEnd(name);
    ThirdPartInjector.onPause(this);
  }
}
