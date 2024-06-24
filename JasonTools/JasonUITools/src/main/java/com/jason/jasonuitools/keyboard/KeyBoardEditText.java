package com.jason.jasonuitools.keyboard;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年08月08日
 */
public class KeyBoardEditText extends androidx.appcompat.widget.AppCompatEditText {

    public KeyBoardEditText(@NonNull Context context) {
        super(context);
        init();
    }

    public KeyBoardEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KeyBoardEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        this.setShowSoftInputOnFocus(false);
        //取消光标
        this.setCursorVisible(false);
        this.setFocusable(false);
        this.setFocusableInTouchMode(false);
        /* // 如果获取到焦点则弹出
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showKeyBoardDialog((EditText) v);
                }
            }
        });*/
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP)
                {
                    showKeyBoardDialog((EditText) v);
                }
                return true;
            }
        });

    }

    /**
     * 显示键盘
     *
     * @param view
     */
    public void showKeyBoardDialog(EditText view) {
        KeyBoardFragment keyBoardFragment = new KeyBoardFragment();
        AppCompatActivity context = (AppCompatActivity) view.getContext();
        CharSequence hint = view.getHint();
        if (hint != null && hint.length() > 0) {
            keyBoardFragment.setHintText(hint.toString());
        }
        String txt = view.getText().toString();
        if (txt != null && txt.length() > 0) {
            keyBoardFragment.setTextEcho(txt);
        }
        if (view.getInputType() == InputType.TYPE_CLASS_NUMBER) {
            keyBoardFragment.setKeyBoardModel(EKeyBoardModel.DIGITAL, false);
        }
        keyBoardFragment.setOnKeyBoardListener(new KeyBoardFragment.OnKeyBoardListener() {
            @Override
            public void onKeyBoard(String txt) {
                view.setText(txt);
                view.setSelection(txt.length());
            }
        });
        keyBoardFragment.show(context.getSupportFragmentManager(), KeyBoardFragment.TAG);
    }
}
