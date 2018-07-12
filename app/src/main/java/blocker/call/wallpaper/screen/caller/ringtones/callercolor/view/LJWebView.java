package blocker.call.wallpaper.screen.caller.ringtones.callercolor.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;


/**
 * Created by luowp on 2016/12/15.
 */
public class LJWebView extends RelativeLayout {

    public static int Circle = 0x01;
    public static int Horizontal = 0x02;

    private Context mContext;

    private WebEventCallback mCallback;
    private WebView mWebView = null;  //
    private ProgressBar mProgressBar = null;  //水平进度条
    private int mBarHeight = 8;  //水平进度条的高
    private boolean mIsAdd = false;  //判断是否已经加入进度条
    private int mProgressStyle = Horizontal;  //进度条样式,Circle表示为圆形，Horizontal表示为水平


    public LJWebView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public LJWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public LJWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    private void init(){
        mWebView = new WebView(mContext);
        this.addView(mWebView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    if (mProgressStyle == Horizontal) {
                        if (mProgressBar != null)
                            mProgressBar.setVisibility(View.GONE);
                    }
                } else {
                    if (!mIsAdd) {
                        if (mProgressStyle == Horizontal) {
                            mProgressBar = (ProgressBar) LayoutInflater.from(mContext).inflate(R.layout.widge_webview_progress_horizontal, null);
                            mProgressBar.setMax(100);
                            mProgressBar.setProgress(0);
                            LJWebView.this.addView(mProgressBar, LayoutParams.FILL_PARENT, mBarHeight);
                        }
                        mIsAdd = true;
                    }

                    if (mProgressStyle == Horizontal) {
                        if (mProgressBar != null) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            mProgressBar.setProgress(newProgress);
                        }
                    }
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (mCallback != null) {
                    mCallback.onReceiveTitle(title);
                }
            }
        });
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setLoadWithOverviewMode(true);
    }

    public void setBarHeight(int height){
        mBarHeight = height;
    }

    public void setProgressStyle(int style){
        mProgressStyle = style;
    }

    public void setEventCallback(WebEventCallback callback) {
        mCallback = callback;
    }

    public void setClickable(boolean value){
        mWebView.setClickable(value);
    }

    public void setUseWideViewPort(boolean value){
        mWebView.getSettings().setUseWideViewPort(value);
    }

    public void setSupportZoom(boolean value){
        mWebView.getSettings().setSupportZoom(value);
    }

    public void setBuiltInZoomControls(boolean value){
        mWebView.getSettings().setBuiltInZoomControls(value);
    }

    public void setJavaScriptEnabled(boolean value){
        mWebView.getSettings().setJavaScriptEnabled(value);
    }

    public void setCacheMode(int value){
        mWebView.getSettings().setCacheMode(value);
    }

    public void setWebViewClient(WebViewClient value){
        mWebView.setWebViewClient(value);
    }

    public void loadUrl(String url){
        mWebView.loadUrl(url);
    }

    public interface WebEventCallback {
        void onReceiveTitle(String title);
    }
}
