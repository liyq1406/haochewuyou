package com.haoche51.buyerapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.HomeQuizAdapter;
import com.haoche51.buyerapp.entity.HCHomeDialogDataEntity;
import com.haoche51.buyerapp.entity.HomeDialogDataEntity;
import com.haoche51.buyerapp.util.HCSensorsUtil;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCStatistic;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.custom.HCViewClickListener;
import java.util.List;

/**
 * entity.getType();
 * -1,不显示；1不显示叉号；2显示叉号
 */
public class HomeQuizDialog {
  private Activity activity;
  private HCHomeDialogDataEntity entity;
  private TextView mKnowTv;
  private TextView mResultTv;
  private TextView mAnswerTv;
  private View mCenterV;
  private ListView mCenterLv;
  private TextView mTitleTv;
  private ImageView mCountIv;
  private int count = 0;
  private Dialog dialog;

  private boolean isAnswer = false;

  public HomeQuizDialog(Activity activity, HCHomeDialogDataEntity entity) {
    this.activity = activity;
    this.entity = entity;
  }

  public void showHomeQuizDialog() {
    int type = entity.getType();
    //将type存到sp中，用来提交线索
    List<HomeDialogDataEntity> quiz = entity.getContent();
    if (type == 0 || HCUtils.isListEmpty(quiz) || quiz.size() != 2) return;

    HCSpUtils.setHomeQuizDialog(type);

    int layoutId = R.layout.dialog_home_quiz;
    View contentView = LayoutInflater.from(activity).inflate(layoutId, null);
    ImageView mCancelIv = (ImageView) contentView.findViewById(R.id.iv_home_quiz_cancel);
    mCountIv = (ImageView) contentView.findViewById(R.id.iv_home_quiz_count);
    mTitleTv = (TextView) contentView.findViewById(R.id.tv_home_quiz_title);
    mCenterLv = (ListView) contentView.findViewById(R.id.lv_home_quiz_center);
    mCenterV = contentView.findViewById(R.id.layout_home_quiz_center);
    mAnswerTv = (TextView) contentView.findViewById(R.id.tv_home_quiz_answer);
    mResultTv = (TextView) contentView.findViewById(R.id.tv_home_quiz_result);
    mKnowTv = (TextView) contentView.findViewById(R.id.tv_home_quiz_know);

    //统计有无关闭的有多少人
    HCStatistic.HomeQuizCloseType(type);
    if (type == 2) {
      //显示叉号，默认不显示
      mCancelIv.setVisibility(View.VISIBLE);
      mCancelIv.setOnClickListener(new HCViewClickListener() {
        @Override public void performViewClick(View v) {
          if (dialog != null && dialog.isShowing()) {
            //统计有多少人关闭了按键
            HCStatistic.HomeQuizCloseClick();
            dialog.dismiss();
          }
        }
      });
    }

    setQuizUI(quiz, count);
    dialog = new Dialog(activity, R.style.normal_dialog);
    dialog.setContentView(contentView);
    dialog.setCanceledOnTouchOutside(false);
    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
      @Override public void onDismiss(DialogInterface dialog) {
        HCSensorsUtil.questionsVsAnswers(isAnswer);
      }
    });
    dialog.show();
  }

  private void setQuizUI(final List<HomeDialogDataEntity> quiz, int i) {
    if (i > 1) return;
    HomeDialogDataEntity dataEntity = quiz.get(i);
    String question = dataEntity.getQuestion();
    List<String> options = dataEntity.getOption();
    final int rightOption = dataEntity.getOption_correct();
    final String answer = dataEntity.getAnswer();
    if (HCUtils.isListEmpty(options)) return;
    if (!TextUtils.isEmpty(question)) {
      mTitleTv.setText(question);
    }
    mKnowTv.setVisibility(View.GONE);
    mCenterV.setVisibility(View.GONE);
    mCenterLv.setVisibility(View.VISIBLE);
    HomeQuizAdapter adapter =
        new HomeQuizAdapter(activity, options, R.layout.lvitem_home_quiz_dialog);
    mCenterLv.setAdapter(adapter);
    mCenterLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        isAnswer = true;
        if (position == rightOption) {
          mAnswerTv.setText(R.string.hc_home_quiz_right);
          mAnswerTv.setTextColor(HCUtils.getResColor(R.color.color_green));
        } else {
          mAnswerTv.setText(R.string.hc_home_quiz_wrong);
          mAnswerTv.setTextColor(HCUtils.getResColor(R.color.hc_home_quiz_wrong_color));
        }
        if (!TextUtils.isEmpty(answer)) {
          mResultTv.setText(HCUtils.getResString(R.string.hc_home_quiz_answer, answer));
        }
        mKnowTv.setVisibility(View.VISIBLE);
        mCenterV.setVisibility(View.VISIBLE);
        mCenterLv.setVisibility(View.GONE);
      }
    });
    mKnowTv.setOnClickListener(new HCViewClickListener() {
      @Override public void performViewClick(View v) {
        count++;
        if (count == 1) {
          mCountIv.setImageResource(R.drawable.icon_quiz_two);
          setQuizUI(quiz, count);
        } else if (count > 1) {
          dialog.dismiss();
        }
      }
    });
  }
}
