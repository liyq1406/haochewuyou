package com.haoche51.buyerapp.util;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.nineoldandroids.animation.ValueAnimator;

public class HCViewUtils {

  private static final String TAG = "HCViewUtils";

  /***
   * 事件约定本地的品牌icon格式为: b品牌id.如(b110.png)
   * 根据传入的品牌id获取相应品牌icon
   * 如果找不到返回0
   */
  private static int getBrandResID(int brand_id) {
    final String packageName = HCUtils.getPackageName();
    Resources mRes = HCUtils.getResources();
    int brandIcon = mRes.getIdentifier(String.valueOf("b" + brand_id), "drawable", packageName);
    return brandIcon > 0 ? brandIcon : 0;
  }

  public static int getBrandResID(int brand_id, int default_id) {
    int result = getBrandResID(brand_id);
    return result > 0 ? result : default_id;
  }

  public static int getCarTypeResID(int id) {
    final String packageName = HCUtils.getPackageName();
    Resources mRes = HCUtils.getResources();
    int brandIcon = mRes.getIdentifier(String.valueOf("filter_stru" + id), "drawable", packageName);
    return brandIcon > 0 ? brandIcon : 0;
  }

  public static int getCountryResID(int id) {
    final String packageName = HCUtils.getPackageName();
    Resources mRes = HCUtils.getResources();
    int brandIcon =
        mRes.getIdentifier(String.valueOf("filter_country" + id), "drawable", packageName);
    return brandIcon > 0 ? brandIcon : 0;
  }

  public static int getColorResID(int id) {
    final String packageName = HCUtils.getPackageName();
    Resources mRes = HCUtils.getResources();
    int brandIcon =
        mRes.getIdentifier(String.valueOf("filter_color" + id), "drawable", packageName);
    return brandIcon > 0 ? brandIcon : 0;
  }

  public static void setIconById(ImageView iv, int brand_id) {
    if (iv != null) {
      iv.setImageResource(getBrandResID(brand_id));
    }
  }

  public static void setIconById(ImageView iv, int brand_id, int default_id) {
    if (iv != null) {
      iv.setImageResource(getBrandResID(brand_id, default_id));
    }
  }

  public static Animation getShakeAnim() {
    return AnimationUtils.loadAnimation(GlobalData.mContext, R.anim.anim_inpute_shake);
  }

  /***
   * view展开效果
   */
  public static void expand(final View v) {
    if (v == null) return;
    v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    final int targetHeight = v.getMeasuredHeight();

    v.getLayoutParams().height = 0;
    v.setVisibility(View.VISIBLE);
    Animation a = new Animation() {
      @Override protected void applyTransformation(float interpolatedTime, Transformation t) {
        v.getLayoutParams().height = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT
            : (int) (targetHeight * interpolatedTime);
        v.requestLayout();
      }

      @Override public boolean willChangeBounds() {
        return true;
      }
    };

    // 1dp/ms
    a.setDuration(150);
    v.startAnimation(a);
  }

  /***
   * 收缩效果
   */
  public static void collapse(final View v) {
    if (v == null) return;
    final int initialHeight = v.getMeasuredHeight();

    Animation a = new Animation() {
      @Override protected void applyTransformation(float interpolatedTime, Transformation t) {
        if (interpolatedTime == 1) {
          v.setVisibility(View.GONE);
        } else {
          v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
          v.requestLayout();
        }
      }

      @Override public boolean willChangeBounds() {
        return true;
      }
    };
    // 1dp/ms
    a.setDuration(150);
    v.startAnimation(a);
  }

  public static void animateLayout(final View view, int from, int to) {
    if (view == null) return;
    final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
    ValueAnimator animator = ValueAnimator.ofInt(from, to);
    animator.setDuration(150);
    animator.setInterpolator(new LinearInterpolator());
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        lp.topMargin = (int) animation.getAnimatedValue();
        lp.bottomMargin = -(int) animation.getAnimatedValue();
        view.requestLayout();
      }
    });
    animator.start();
  }

  /**
   * 设置tv字体颜色为指定color,从from到to
   */
  public static void changeTextViewColor(TextView tv, int color, int... scope) {
    if (tv == null) return;
    String str = tv.getText().toString();
    if (TextUtils.isEmpty(str)) return;

    int len = str.length();
    int from = 0;
    int to = 0;
    if (scope == null) {
      from = 0;
      to = len;
    } else {
      if (scope.length == 1) {
        from = scope[0];
        to = len;
      }
      if (scope.length == 2) {
        from = scope[0];
        to = scope[1];
      }
    }

    SpannableStringBuilder spb = new SpannableStringBuilder(str);
    spb.setSpan(new ForegroundColorSpan(color), from, to,
        SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
    tv.setText(spb);
  }

  private final static String VOICE_FORMAT = "收不到验证码? <u><font color=%s>收听语音验证码</font></u>";

  public static void formatReceiveCode(TextView textView, boolean enable) {

    String result = String.format(VOICE_FORMAT, enable ? "#dd0000" : "#ACACAC");
    Spanned sptext = Html.fromHtml(result);
    textView.setText(sptext);
  }

  /**
   * 设置字体大小，从0到position为一种，position到结束为一种
   */
  //public static void setTextFormat(TextView tv, String text, int position) {
  //  if (position < text.length() && position > 0) {
  //    SpannableString sp = new SpannableString(text);
  //    sp.setSpan(new TextAppearanceSpan(GlobalData.mContext, R.style.filter_hint_text_before), 0,
  //        position, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
  //    sp.setSpan(new TextAppearanceSpan(GlobalData.mContext, R.style.filter_hint_text_after),
  //        position, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
  //    tv.setText(sp, TextView.BufferType.SPANNABLE);
  //  }
  //}

  /**
   * 设置首页事故车数量字体
   */
  public static void setHomeTextFormat(TextView tv, String text) {
    if (TextUtils.isEmpty(text) || tv == null) return;
    if (!(text.contains("万") && text.contains("辆"))) {
      tv.setText(text);
      return;
    }

    int w_index = text.indexOf("万");
    int l_index = text.indexOf("辆");
    if (w_index == 0 || l_index - w_index < 1 || l_index + 1 > text.length()) {
      tv.setText(text);
      return;
    }
    SpannableString sp = new SpannableString(text);
    sp.setSpan(new TextAppearanceSpan(GlobalData.mContext, R.style.home_text_after), 0, w_index,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    sp.setSpan(new TextAppearanceSpan(GlobalData.mContext, R.style.home_text_before), w_index,
        w_index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    sp.setSpan(new TextAppearanceSpan(GlobalData.mContext, R.style.home_text_after), w_index + 1,
        l_index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    sp.setSpan(new TextAppearanceSpan(GlobalData.mContext, R.style.home_text_before), l_index,
        l_index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    tv.setText(sp, TextView.BufferType.SPANNABLE);
  }

  /**
   * 注意：如果要设置drawablepadding，那么就要在调用该方法前调用
   * tv.setCompoundDrawablePadding(padding);
   * 默认使用的是布局文件中设置的drawablepadding。
   */
  public static void setTextViewDrawable(TextView tv, int resDrawableID, int type) {
    Drawable logo = null;
    try {
      logo = HCUtils.getResDrawable(resDrawableID);
    } catch (Exception e) {
      HCLog.d(TAG, "setTextViewDrawable is crash ...");
    }
    if (logo != null && tv != null) {
      logo.setBounds(0, 0, logo.getIntrinsicWidth(), logo.getIntrinsicHeight());
      switch (type) {
        case HCConsts.DRAWABLE_LEFT:
          tv.setCompoundDrawables(logo, null, null, null);
          break;
        case HCConsts.DRAWABLE_TOP:
          tv.setCompoundDrawables(null, logo, null, null);
          break;
        case HCConsts.DRAWABLE_RIGHT:
          tv.setCompoundDrawables(null, null, logo, null);
          break;
        case HCConsts.DRAWABLE_BOTTOM:
          tv.setCompoundDrawables(null, null, null, logo);
          break;
      }
    }
  }

  public static void setTextViewDrawable(TextView tv, int resDrawableID, int type, int scale) {
    Drawable logo = null;
    try {
      logo = HCUtils.getResDrawable(resDrawableID);
    } catch (Exception e) {
      HCLog.d(TAG, "setTextViewDrawable is crash ...");
    }
    if (logo != null && tv != null) {
      logo.setBounds(0, 0, logo.getIntrinsicWidth() / scale, logo.getIntrinsicHeight() / scale);
      switch (type) {
        case HCConsts.DRAWABLE_LEFT:
          tv.setCompoundDrawables(logo, null, null, null);
          break;
        case HCConsts.DRAWABLE_TOP:
          tv.setCompoundDrawables(null, logo, null, null);
          break;
        case HCConsts.DRAWABLE_RIGHT:
          tv.setCompoundDrawables(null, null, logo, null);
          break;
        case HCConsts.DRAWABLE_BOTTOM:
          tv.setCompoundDrawables(null, null, null, logo);
          break;
      }
    }
  }
}
