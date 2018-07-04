package blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

/**
 * Created by ChenR on 2017/1/5.
 */

public class BaseDialog extends Dialog{

    public OnCancelClickListener mOnCancelListener;
    protected OKClickListener mOKListener;

    public BaseDialog(Context context) {
        super(context);
        setTitleView();
        setWindowBackground();
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
        setTitleView();
        setWindowBackground();
    }

    protected BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setCancelMode();
        setTitleView();
        setWindowBackground();
    }

    private void setCancelMode() {
        // 设置弹窗外点击 --> 弹窗消失;
        setCanceledOnTouchOutside(true);
    }

    private void setTitleView() {
        // 设置自定义弹窗视图下没有title视图;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    // 设置弹窗背景透明;
    private void setWindowBackground() {
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {

        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {

        }
    }

    public void setOnCancelClickListener(OnCancelClickListener mListener) {
        this.mOnCancelListener = mListener;
    }

    public void setOkClickListener(OKClickListener listener) {
        this.mOKListener = listener;
    }

    public interface OKClickListener {
        void Ok();
    }

    public interface OnCancelClickListener {
        void cancel();
    }
}
