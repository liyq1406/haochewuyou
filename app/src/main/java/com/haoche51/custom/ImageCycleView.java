package com.haoche51.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.entity.HCBannerEntity;

import java.util.ArrayList;

/***
 * 使用该View时,一定要将其放在FrameLayout容器中!!!!!
 */
public class ImageCycleView extends LinearLayout {
  /**
   * 上下文
   */
  private Context mContext;
  /**
   * 图片轮播视图
   */
  private ViewPager mAdvPager = null;

  /**
   * 滚动图片视图适配器
   */
  private ImageCycleAdapter mAdvAdapter;

  /**
   * 图片轮播指示器控件
   */
  private LinearLayout mGroup;

  /**
   * 图片轮播指示器-个图
   */
  private ImageView mImageView = null;

  /**
   * 滚动图片指示器-视图列表
   */
  private ImageView[] mImageViews = null;

  /**
   * 图片滚动当前图片下标
   */
  private int mImageIndex = 0;

  /**
   * 手机密度
   */
  private float mScale;

  private int bgColor;
  private int fgColor;
  private int position;

  public ImageCycleView(Context context) {
    super(context);
  }

  public ImageCycleView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ImageCycleView);
    bgColor =
        ta.getResourceId(R.styleable.ImageCycleView_bgColor, R.drawable.color_cycle_un_selected);
    fgColor =
        ta.getResourceId(R.styleable.ImageCycleView_fgColor, R.drawable.color_cycle_selected_red);
    position = ta.getInteger(R.styleable.ImageCycleView_position, 1);
    mScale = context.getResources().getDisplayMetrics().density;
    LayoutInflater.from(context).inflate(R.layout.ad_cycle_view, this);
    mAdvPager = (ViewPager) findViewById(R.id.adv_pager);
    mAdvPager.setOnPageChangeListener(new GuidePageChangeListener());
    mAdvPager.setOnTouchListener(new OnTouchListener() {
      @SuppressLint("ClickableViewAccessibility") @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_UP:
            // 开始图片滚动
            startImageTimerTask();
            break;
          default:
            // 停止图片滚动
            stopImageTimerTask();
            break;
        }
        return false;
      }
    });
    // 滚动图片右下指示器视图
    switch (position) {
      case 0:
        mGroup = (LinearLayout) findViewById(R.id.viewGroup_left);
        break;
      case 1:
        mGroup = (LinearLayout) findViewById(R.id.viewGroup_center);
        break;
      case 2:
        mGroup = (LinearLayout) findViewById(R.id.viewGroup_right);
        break;
    }
  }

  /**
   * 装填图片数据
   */
  public void setImageResources(ArrayList<HCBannerEntity> imageUrlList,
      ImageCycleViewListener imageCycleViewListener) {
    // 清除所有子视图
    mGroup.removeAllViews();
    // 图片广告数量
    final int imageCount = imageUrlList.size();
    if (imageCount > 1) {
      mImageViews = new ImageView[imageCount];
      for (int i = 0; i < imageCount; i++) {
        mImageView = new ImageView(mContext);
        int imageParams = (int) (mScale * 5 + 0.5f);// XP与DP转换，适应不同分辨率
        int imagePadding = (int) (mScale * 15 + 0.5f);
        int margin = mContext.getResources().getDimensionPixelSize(R.dimen.indicator_radius);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageParams, imageParams);

        params.leftMargin = margin;
        mImageView.setLayoutParams(params);
        mImageView.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
        mImageViews[i] = mImageView;
        if (i == 0) {
          mImageViews[i].setBackgroundResource(fgColor);
        } else {
          mImageViews[i].setBackgroundResource(bgColor);
        }
        mGroup.addView(mImageViews[i]);
      }
    }
    mAdvAdapter = new ImageCycleAdapter(mContext, imageUrlList, imageCycleViewListener);
    mAdvPager.setAdapter(mAdvAdapter);
    startImageTimerTask();
  }

  /**
   * 开始轮播(手动控制自动轮播与否，便于资源控制)
   */
  public void startImageCycle() {
    startImageTimerTask();
  }

  /**
   * 暂停轮播——用于节省资源
   */
  public void pauseImageCycle() {
    stopImageTimerTask();
  }

  /**
   * 开始图片滚动任务
   * for　SB产品的需求，哪有banner图不会自己滚的。
   * 需要滚时，把注释取消就可以了
   */
  private void startImageTimerTask() {
    stopImageTimerTask();
    // 图片每3秒滚动一次
    mHandler.postDelayed(mImageTimerTask, 3000);
  }

  /**
   * 停止图片滚动任务
   * 同上
   */
  private void stopImageTimerTask() {
    mHandler.removeCallbacks(mImageTimerTask);
  }

  private Handler mHandler = new Handler();

  /**
   * 图片自动轮播Task
   */
  private Runnable mImageTimerTask = new Runnable() {
    @Override public void run() {
      if (mImageViews != null) {
        // 下标等于图片列表长度说明已滚动到最后一张图片,重置下标
        if ((++mImageIndex) == mImageViews.length) {
          mImageIndex = 0;
        }
        mAdvPager.setCurrentItem(mImageIndex);
      }
    }
  };

  /**
   * 轮播图片状态监听器
   */
  private final class GuidePageChangeListener implements OnPageChangeListener {

    @Override public void onPageScrollStateChanged(int state) {
      if (state == ViewPager.SCROLL_STATE_IDLE) startImageTimerTask(); // 开始下次计时
    }

    @Override public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override public void onPageSelected(int index) {
      // 设置当前显示的图片下标
      mImageIndex = index;
      // 设置图片滚动指示器背景
      mImageViews[index].setBackgroundResource(fgColor);
      for (int i = 0; i < mImageViews.length; i++) {
        if (index != i) {
          mImageViews[i].setBackgroundResource(bgColor);
        }
      }
    }
  }

  private class ImageCycleAdapter extends PagerAdapter {
    /**
     * 图片视图缓存列表
     */
    private ArrayList<ImageView> mImageViewCacheList;

    /**
     * 图片资源列表
     */
    private ArrayList<HCBannerEntity> mAdList = new ArrayList<HCBannerEntity>();

    /**
     * 广告图片点击监听器
     */
    private ImageCycleViewListener mImageCycleViewListener;

    private Context mContext;

    public ImageCycleAdapter(Context context, ArrayList<HCBannerEntity> adList,
        ImageCycleViewListener imageCycleViewListener) {
      mContext = context;
      mAdList = adList;
      mImageCycleViewListener = imageCycleViewListener;
      mImageViewCacheList = new ArrayList<ImageView>();
    }

    @Override public int getCount() {
      return mAdList.size();
    }

    @Override public boolean isViewFromObject(View view, Object obj) {
      return view == obj;
    }

    @Override public Object instantiateItem(ViewGroup container, final int position) {
      HCBannerEntity entity = mAdList.get(position);
      String imageUrl = entity.getPic_url();
      ImageView imageView = null;
      if (mImageViewCacheList.isEmpty()) {
        imageView = new ImageView(mContext);
        imageView.setLayoutParams(
            new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
      } else {
        imageView = mImageViewCacheList.remove(0);
      }
      // 设置图片点击监听
      imageView.setOnClickListener(new OnClickListener() {
        @Override public void onClick(View v) {
          mImageCycleViewListener.onImageClick(position, v);
        }
      });
      imageView.setTag(imageUrl);
      container.addView(imageView);
      mImageCycleViewListener.displayImage(imageUrl, imageView);
      return imageView;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
      ImageView view = (ImageView) object;
      container.removeView(view);
      mImageViewCacheList.add(view);
    }
  }

  /**
   * 轮播控件的监听事件
   */
  public interface ImageCycleViewListener {

    /**
     * 加载图片资源
     */
    void displayImage(String imageURL, ImageView imageView);

    /**
     * 单击图片事件
     */
    void onImageClick(int position, View imageView);
  }
}
