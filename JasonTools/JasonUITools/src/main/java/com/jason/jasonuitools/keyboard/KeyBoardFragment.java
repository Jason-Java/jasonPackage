package com.jason.jasonuitools.keyboard;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jason.jasonuitools.R;
import com.jason.jasonuitools.fragment.BaseDialogFragment;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年08月03日
 */
public class KeyBoardFragment extends BaseDialogFragment {
    public static final String TAG = "KeyBoardFragmentTAG";
    private StringBuilder txtBuilder = new StringBuilder();
    private Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 键盘模式 默认 字符模式
     */
    private EKeyBoardModel keyBoardModel = EKeyBoardModel.LETTER;
    //是否是绝对模式
    private boolean absoluteModel = false;
    //输入框提示文本
    private String hintText = "";
    //输入框回显文本
    private String textEcho = "";
    private OnKeyBoardListener onKeyBoardListener;

    private EditText tvInput;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable());
        window.getDecorView().setPadding(0,0,0,0);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width=WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height=WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.BOTTOM;
        window.setAttributes(layoutParams);        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View getLayoutView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.jason_keyboard_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        startTimer();
//        getDialog().getWindow().addFlags();

        /*Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.width=WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height=WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.END;
            window.setAttributes(layoutParams);
        }*/
    }

    /**
     * 设置键盘模式
     *
     * @param model         键盘模式 {@link EKeyBoardModel}
     * @param absoluteModel 是否是绝对模式 绝对模式之下不允许切换键盘模式
     */
    public void setKeyBoardModel(EKeyBoardModel model, boolean absoluteModel) {
        this.keyBoardModel = model;
        this.absoluteModel = absoluteModel;
    }

    public void setOnKeyBoardListener(OnKeyBoardListener onKeyBoardListener) {
        this.onKeyBoardListener = onKeyBoardListener;
    }


    /**
     * 设置提示文字
     *
     * @param hintText
     */
    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    /**
     * 设置输入框文字回显
     *
     * @param text
     */
    public void setTextEcho(String text) {
        this.textEcho = text;
    }

    @Override
    protected void iniData() {
        //数字键盘模式
        keyModelConversion(this.keyBoardModel);
        tvInput = view.findViewById(R.id.tvInput);
        tvInput.setHint(hintText);
        tvInput.setText(textEcho);
        tvInput.setSelection(textEcho.length());
        txtBuilder.append(textEcho);
        tvInput.setShowSoftInputOnFocus(false);
    }


    @Override
    protected void iniEvent() {
        //获取键盘按键控件
        getChildView((ViewGroup) view);

        //删除字符
        view.findViewById(R.id.keyboard_delete).setOnClickListener(deleteTxtClickListener);
        //删除字符
        view.findViewById(R.id.keyboardDeleteLetter).setOnClickListener(deleteTxtClickListener);
        //删除字符
        view.findViewById(R.id.keyboardDeleteSymbol).setOnClickListener(deleteTxtClickListener);

        //删除字符
        view.findViewById(R.id.keyboard_delete).setOnTouchListener(deleteTxtTouchListener);
        //删除字符
        view.findViewById(R.id.keyboardDeleteLetter).setOnTouchListener(deleteTxtTouchListener);
        //删除字符
        view.findViewById(R.id.keyboardDeleteSymbol).setOnTouchListener(deleteTxtTouchListener);

        if (!absoluteModel) {
            // 字母键盘切换到数字键盘
            view.findViewById(R.id.keyboardToDigital).setOnClickListener(v -> {
                keyModelConversion(EKeyBoardModel.DIGITAL);
            });
            // 数字键盘切换到字母键盘
            view.findViewById(R.id.keyboardToLetter).setOnClickListener(v -> {
                keyModelConversion(EKeyBoardModel.LETTER);
            });
            // 字母键盘切换到符号键盘
            view.findViewById(R.id.keyboardLetterToSymbol).setOnClickListener(v -> {
                keyModelConversion(EKeyBoardModel.SYMBOL);
            });
            //符号键盘切换到字母键盘
            view.findViewById(R.id.keyboardSymbolToLetter).setOnClickListener(v -> {
                keyModelConversion(EKeyBoardModel.LETTER);
            });
            //符号键盘切换到数字键盘
            view.findViewById(R.id.keyboardSymbolToDigital).setOnClickListener(v -> {
                keyModelConversion(EKeyBoardModel.DIGITAL);
            });
        }
        // 切换大小写
        view.findViewById(R.id.keyboard_up).setOnClickListener(new View.OnClickListener() {
            boolean isUpperCase = false;

            @Override
            public void onClick(View v) {
                toggleCase(view.findViewById(R.id.llLetterKeyboard), isUpperCase = !isUpperCase);
                if (isUpperCase) {
                    v.setBackgroundResource(R.drawable.jason_keyboard_uppercase_button_selector);
                } else {
                    v.setBackgroundResource(R.drawable.jason_keyboard_lowercase_button_selector);
                }
            }
        });

        //确认提交
        view.findViewById(R.id.keyboard_confirm).setOnClickListener(confirmClickListener);
        //确认提交
        view.findViewById(R.id.keyboardConfirmLetter).setOnClickListener(confirmClickListener);
        //确认提交
        view.findViewById(R.id.keyboardConfirmSymbol).setOnClickListener(confirmClickListener);
    }

    /**
     * 获取键盘按键控件
     *
     * @param parentView
     */
    public void getChildView(ViewGroup parentView) {
        int childCount = parentView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parentView.getChildAt(i);
            if (childView instanceof ViewGroup) {
                getChildView((ViewGroup) childView);
            } else if (childView instanceof TextView) {
                if (childView.getId() == R.id.tvInput) {
                    continue;
                }
                childView.setOnClickListener(this::getViewText);
            }
        }
    }

    /**
     * 获取按键字符
     *
     * @param view
     */
    public void getViewText(View view) {
        cancelTimer();
        startTimer();
        String str = ((TextView) view).getText().toString();
        if (str == null || str.length() <= 0) {
            return;
        }
        int start = tvInput.getSelectionStart();

        if (str.equals("space") || str.equals("SPACE")) {
            txtBuilder.insert(start, " ");
        } else {
            txtBuilder.insert(start, str);
        }
        tvInput.setText(txtBuilder.toString());
        tvInput.setSelection(start + 1);
    }

    /**
     * 单击删除字符，每次只删除一个
     */
    private View.OnClickListener deleteTxtClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            deleteTxt(1);
        }
    };


    private View.OnTouchListener deleteTxtTouchListener = new View.OnTouchListener() {
        private long oldTime = 0;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                handler.postDelayed(deleteTxtRunnable, 1000 * 1);
                oldTime = System.currentTimeMillis();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                handler.removeCallbacks(deleteTxtRunnable);
                if (System.currentTimeMillis() - oldTime < 1000) {
                    return false;
                } else {
                    return true;
                }
            }
            return false;
        }
    };

    private Runnable deleteTxtRunnable = new Runnable() {
        @Override
        public void run() {
            deleteTxt(3);
            handler.postDelayed(this, 1000 * 1);
        }
    };

    /**
     * 删除字符
     *
     * @param length 删除长度
     */
    public void deleteTxt(int length) {
        int start = tvInput.getSelectionStart();
        int end = tvInput.getSelectionEnd();
        if (start == end && start == 0) return;
        if (txtBuilder.length() < length) {
            length = txtBuilder.length();
        }
        if (start == end) {
            txtBuilder.delete(start - length, start);
        } else {
            txtBuilder.delete(start, end);
        }
        tvInput.setText(txtBuilder.toString());
        tvInput.setSelection((start - length) == 0 ? 0 : (start - length));
    }

    /**
     * 大小写切换
     *
     * @param viewGroup
     * @param isUpperCase true 大写 false 小写
     */
    public void toggleCase(ViewGroup viewGroup, boolean isUpperCase) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = viewGroup.getChildAt(i);
            if (childView instanceof ViewGroup) {
                toggleCase((ViewGroup) childView, isUpperCase);
            }
            if (!(childView instanceof TextView)) continue;
            TextView childTextView = (TextView) childView;
            String txt = childTextView.getText().toString();
            if (txt == null || txt.equals("")) continue;
            childTextView.setText(isUpperCase ? txt.toUpperCase() : txt.toLowerCase());
        }
    }

    /**
     * 键盘模式转换
     *
     * @param keyBoardModel 键盘模式 EKeyBoardModel.DIGITAL 数字键盘  EKeyBoardModel.LETTER 字母键盘
     */
    private void keyModelConversion(EKeyBoardModel keyBoardModel) {
        if (keyBoardModel == EKeyBoardModel.DIGITAL) {
            view.findViewById(R.id.llLetterKeyboard).setVisibility(View.GONE);
            view.findViewById(R.id.llSymbolKeyboard).setVisibility(View.GONE);
            view.findViewById(R.id.llDigitKeyboard).setVisibility(View.VISIBLE);
        } else if (keyBoardModel == EKeyBoardModel.LETTER) {
            view.findViewById(R.id.llLetterKeyboard).setVisibility(View.VISIBLE);
            view.findViewById(R.id.llDigitKeyboard).setVisibility(View.GONE);
            view.findViewById(R.id.llSymbolKeyboard).setVisibility(View.GONE);
        } else if (keyBoardModel == EKeyBoardModel.SYMBOL) {
            view.findViewById(R.id.llLetterKeyboard).setVisibility(View.GONE);
            view.findViewById(R.id.llDigitKeyboard).setVisibility(View.GONE);
            view.findViewById(R.id.llSymbolKeyboard).setVisibility(View.VISIBLE);
        }
    }

    /**
     * 确认提交点击事件
     */
    private View.OnClickListener confirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            confirmTxt();
        }
    };

    /**
     * 提交Txt
     */
    private void confirmTxt() {
        if (onKeyBoardListener != null) {
            onKeyBoardListener.onKeyBoard(txtBuilder.toString());
            dismiss();
        }
    }

    public interface OnKeyBoardListener {
        void onKeyBoard(String txt);
    }
}
