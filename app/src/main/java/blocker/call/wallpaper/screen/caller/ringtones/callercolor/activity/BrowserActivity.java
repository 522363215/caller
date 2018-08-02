package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.FunctionUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.LJWebView;

public class BrowserActivity extends BaseActivity {
    private LJWebView mLJWebView = null;
    private String mUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_PROGRESS);// 让进度条显示在标题栏上
        super.onCreate(savedInstanceState);

        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        mUrl = ConstantUtils.URL_PRIVACY;
        actionBar.setTitle(getResources().getString(R.string.about_privacy));
        actionBar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        //ljweb
        mLJWebView = (LJWebView) findViewById(R.id.webview);
        mLJWebView.setBarHeight(8);
        mLJWebView.setClickable(true);

        mLJWebView.setUseWideViewPort(true);
        mLJWebView.setSupportZoom(true);
        mLJWebView.setBuiltInZoomControls(true);
        mLJWebView.setJavaScriptEnabled(true);
        mLJWebView.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mLJWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Uri.parse(url).getScheme().equals("mailto")) {
                    FunctionUtil.sendMail(url.substring(url.indexOf("mailto:") + "mailto:".length()));
                }
                else {
                    view.loadUrl(url);
                }
                return true;
            }
        });

        mLJWebView.setEventCallback(new LJWebView.WebEventCallback() {
            @Override
            public void onReceiveTitle(String title) {
                //findViewById(TextView.class, R.id.tv_title).setText(title);
            }
        });

        mLJWebView.loadUrl(mUrl);
    }

    @Override
    protected void translucentStatusBar() {
        CommonUtils.translucentStatusBar(this);
    }

    @Override
    protected int getLayoutRootId() {
        return R.layout.activity_browser;
    }

}
