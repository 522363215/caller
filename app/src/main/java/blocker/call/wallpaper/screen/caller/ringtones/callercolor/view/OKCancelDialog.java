package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;


/**
 * Created by zhq on 2017/2/6.
 */

public class OKCancelDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private TextView mTvTitle;
    private LinearLayout mLayoutContent;
    private TextView mTvContent;
    private CheckBox mCbSpm;
    private LinearLayout mLayoutOK;
    private LinearLayout mLayoutCancel;
    private TextView mTvOK;
    private TextView mTvCancel;
    private OKClickListener mListener;
    private OnCancelClickListener mOnCancelListener;
    private LinearLayout mLayoutTitle;
    private LinearLayout mLayoutOkCancel;


    public OKCancelDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.getWindow().setWindowAnimations(R.style.dialog_animation);
        this.mContext = context;
    }

    public OKCancelDialog(Context context, boolean isAnimation) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        if(isAnimation) {
            this.getWindow().setWindowAnimations(R.style.dialog_animation);
        }
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ok_cancel_dialog);
        mLayoutTitle = (LinearLayout) findViewById(R.id.layout_title);
        mTvTitle = (TextView) findViewById(R.id.tv_title);

        mLayoutContent = (LinearLayout) findViewById(R.id.layout_content);
        mTvContent = (TextView) findViewById(R.id.tv_content);
        mCbSpm = (CheckBox) findViewById(R.id.cb_report_call_as_spam);

        mLayoutOkCancel = (LinearLayout) findViewById(R.id.layout_ok_cancel);
        mLayoutOK = (LinearLayout) findViewById(R.id.layout_ok);
        mLayoutCancel = (LinearLayout) findViewById(R.id.layout_cancel);
        mTvOK = (TextView) findViewById(R.id.tv_ok);
        mTvCancel = (TextView) findViewById(R.id.tv_cancel);

        mLayoutCancel.setOnClickListener(this);
        mLayoutOK.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.layout_ok:
                dismiss();
                if (mListener != null) {
                    mListener.Ok();
                }
                break;
            case R.id.layout_cancel:
                if (mOnCancelListener != null) {
                    mOnCancelListener.cancel();
                }
                dismiss();
                break;
        }
    }

    public void setOnCancelClickListener(OnCancelClickListener mListener) {
        this.mOnCancelListener = mListener;
    }

    public interface OKClickListener {
        void Ok();
    }

    public interface OnCancelClickListener {
        void cancel();
    }

    @Override
    public void show() {
        super.show();
        WindowManager windowManager = ((Activity) mContext).getWindowManager();
        Display d = windowManager.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
//        p.height = (int) (d.getHeight() * 0.3);   //高度设置为屏幕的0.3
        p.width = (int) (d.getWidth() * 0.95);    //宽度设置为屏幕的0.5
        getWindow().setAttributes(p);     //设置生效

    }

    public void setOkClickListener(OKClickListener listener) {
        this.mListener = listener;
    }

    public void setTvTitle(String str) {
        if (mLayoutTitle != null)
            mLayoutTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(str);
    }

    public void setTvTitle(int strResId) {
        if (mLayoutTitle != null)
            mLayoutTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(strResId);
    }

    public void setTvTitle(int strResId, String name) {
        if (mLayoutTitle != null)
            mLayoutTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(Html.fromHtml(String.format(mContext.getResources().getString(strResId), name)));
    }

    public void setContent(String content, boolean isShowContent, boolean isShowCheckBox) {
        mLayoutContent.setVisibility(View.VISIBLE);
        if (!isShowContent) {
            mLayoutContent.setVisibility(View.GONE);
        } else {
            mLayoutContent.setVisibility(View.VISIBLE);
            mTvContent.setText(content);
            if (!isShowCheckBox) {
                mCbSpm.setVisibility(View.GONE);
            } else {
                mCbSpm.setVisibility(View.VISIBLE);
                mCbSpm.setChecked(true);
            }
        }
    }

    public void setOKCancel(String okStr, String cancelStr) {
        mLayoutOkCancel.setVisibility(View.VISIBLE);
        mTvOK.setText(okStr);
        mTvCancel.setText(cancelStr);
    }

    public void setOKCancel(int okResId, int cancelResId) {
        mLayoutOkCancel.setVisibility(View.VISIBLE);
        mTvOK.setText(okResId);
        mTvCancel.setText(cancelResId);
    }

    public void setCheckboxContent(String str, boolean isShowContent) {
        mCbSpm.setText(str);
        if (!isShowContent) {
            mTvContent.setVisibility(View.GONE);
        }
    }

    public void setCheckboxContent(int resId, boolean isShowContent) {
        if (!isShowContent) {
            mTvContent.setVisibility(View.GONE);
        }
        mCbSpm.setText(resId);
    }

    public boolean getCheckBoxState() {
        return mCbSpm.isChecked();
    }

}
