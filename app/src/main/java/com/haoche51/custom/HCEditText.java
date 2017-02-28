package com.haoche51.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

/****
 * 使用时需要在xml中设置drawableright的叉叉
 */
public class HCEditText extends EditText {
    private Drawable mRightDrawable;

    {
        setEditTextDrawable();
        addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable paramEditable) {
            }

            public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
            }

            public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
                HCEditText.this.setEditTextDrawable();
            }
        });
    }

    public HCEditText(Context paramContext) {
        super(paramContext);
    }

    public HCEditText(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public HCEditText(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    private void setEditTextDrawable() {
        if (getText().toString().length() == 0) {
            setCompoundDrawables(null, null, null, null);
        } else {
            if (hasFocus())
                setCompoundDrawables(null, null, this.mRightDrawable, null);
        }
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (!focused)
            setCompoundDrawables(null, null, null, null);

        if (focused && getText().toString().length() != 0)
            setCompoundDrawables(null, null, this.mRightDrawable, null);

        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        this.mRightDrawable = null;
    }

    /**
     * --------------------paddingRight --------------------XXXXX*****Width
     * -----------------intrinsicWidth
     */

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if ((this.mRightDrawable != null) && (motionEvent.getAction() == MotionEvent.ACTION_UP)) {
            int touchX = (int) motionEvent.getX();
            int deleteStartX = getWidth() - getPaddingRight() - mRightDrawable.getIntrinsicWidth() - 30;
            if (touchX > deleteStartX) {
                if (hasFocus())
                    setText("");
                motionEvent.setAction(MotionEvent.ACTION_CANCEL);
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    // 设置显示的图片资源
    public void setCompoundDrawables(Drawable lDrawable, Drawable tDrawable, Drawable rDrawable, Drawable bDrawable) {
        if (rDrawable != null)
            this.mRightDrawable = rDrawable;
        super.setCompoundDrawables(lDrawable, tDrawable, rDrawable, bDrawable);
    }
}
