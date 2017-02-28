package com.haoche51.buyerapp.util;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.fragment.CoreFragment;
import com.haoche51.buyerapp.fragment.ForumFragment;
import com.haoche51.buyerapp.fragment.HomePageFragment;
import com.haoche51.buyerapp.fragment.ProfileFragment;

public class FragmentController {

  /**
   * 首页
   */
  public static final String HOME_TAG = "home_page";
  /**
   * 选购好车
   */
  public static final String CORE_VEHICLE_TAG = "vehicle_core_page";
  /**
   * 论坛
   */
  public static final String FORUM_TAG = "forum_page";
  /**
   * 我的
   */
  public static final String PROFILE_TAG = "profile_page";

  public static Fragment newInstance(String tag) {
    Fragment fragment = null;
    if (tag.equals(CORE_VEHICLE_TAG)) {
      fragment = new CoreFragment();
    } else if (tag.equals(PROFILE_TAG)) {
      fragment = new ProfileFragment();
    } else if (tag.equals(FORUM_TAG)) {
      fragment = new ForumFragment();
    } else if (tag.equals(HOME_TAG)) {
      fragment = new HomePageFragment();
    }
    return fragment;
  }

  /**
   * 订阅条目数icon
   */
  private static int[] couns_icon = {
      0, R.drawable.icon_count_1, R.drawable.icon_count_2, R.drawable.icon_count_3,
      R.drawable.icon_count_4, R.drawable.icon_count_5
  };

  public static Drawable getCountIcon(int size) {
    Drawable logo = HCUtils.getResDrawable(couns_icon[size]);
    logo.setBounds(0, 0, logo.getIntrinsicWidth(), logo.getIntrinsicHeight());
    return logo;
  }
}
