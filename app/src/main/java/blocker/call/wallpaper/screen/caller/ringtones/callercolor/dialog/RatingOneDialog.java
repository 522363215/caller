package blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager.ServerManager;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.DeviceUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.JsonUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ToastUtils;

/**
 * Created by ChenR on 2017/6/30.
 */

public class RatingOneDialog extends BaseDialog {
    private View root;
    private EditText edt_suggest;

    private String score;

    public RatingOneDialog(Context context) {
        super(context);

        FlurryAgent.onStartSession(context);

        initView(context);
        listener();
    }

    private void listener() {
        root.findViewById(R.id.tv_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryAgent.logEvent("RatingOneDialog----sendMessageClick");
                Editable text = edt_suggest.getText();
                if (TextUtils.isEmpty(text)) {
                    ToastUtils.showToast(getContext(), getContext().getString(R.string.rate_dialog_nagative_toast));
                } else {
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_SENDTO);
//                    intent.setData(Uri.parse("mailto:" + ConstantUtils.DEVELOPER_EMAIL));
//                    intent.putExtra(Intent.EXTRA_SUBJECT, ConstantUtils.EMAIL_TITLE);
//                    intent.putExtra(Intent.EXTRA_TEXT, text.toString());
//                    getContext().startActivity(intent);
                    sendFeedBack(text);
                    dismiss();
                }
            }
        });
    }

    private void sendFeedBack(final CharSequence text) {
        PreferenceHelper.putLong(PreferenceHelper.PREF_KEY_LAST_FEEDBACK_TIME, System.currentTimeMillis());
        final String mail = getMail();
        String msg = resolveMessage(text);
        LogUtil.d("chenr", "message: " + msg);
        if (!TextUtils.isEmpty(msg)) {
            JSONObject object = JsonUtil.createSuggestionJson(msg, mail, score);
            ServerManager.getInstance().requestData(object, ConstantUtils.SERVER_API_PARAM, new ServerManager.CallBack() {
                @Override
                public void onRequest(boolean hasSuccess, String data) {
                    if (hasSuccess) {
                        Looper.prepare();
                        ToastUtils.showToast(getContext(), getContext().getString(R.string.rate_dialog_send_success_toast));
                        Looper.loop();
                    }
                }
            });
        } else {
            ToastUtils.showToast(getContext(), getContext().getString(R.string.rate_dialog_msg_illegal));
        }
    }

    /**
     *
     * 對輸入字符串做處理
     *
     * <!-: 非法字符
     * <!: 非法字符
     * insert ---> www_i
     * update ---> www_u
     * delete ---> www_d
     * select ---> www_s
     *
     * INSERT ---> WWW_I
     * UPDATE ---> WWW_U
     * DELETE ---> WWW_D
     * SELECT ---> WWW_S
     */
    private String resolveMessage(final CharSequence text) {
        String result = text.toString();

        result = result.replace("<!", "");
        result = result.replace("<!-", "");
        result = result.replace("INSERT", "WWW_I");
        result = result.replace("UPDATE", "WWW_U");
        result = result.replace("DELETE", "WWW_D");
        result = result.replace("SELECT", "WWW_S");
        result = result.replace("insert", "www_i");
        result = result.replace("update", "www_u");
        result = result.replace("delete", "www_d");
        result = result.replace("select", "www_s");

        return result.trim();
    }

    private String getMail() {
        String mail = "";
        AccountManager mAccountManager = (AccountManager) getContext().getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accounts = mAccountManager.getAccountsByType("com.google");

        if (accounts.length > 0) {
            Account mAccount = accounts[0];
            mail = mAccount.name;
        }
        return mail;
    }

    private void initView(Context context) {
        if (root == null) {
            root = View.inflate(context, R.layout.dialog_rating_one, null);

            int width = DeviceUtil.getScreenWidth() - Stringutil.dpToPx(context, 32);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);

            setContentView(root, params);

            edt_suggest = (EditText) root.findViewById(R.id.edt_suggest);
        }
    }

    public void setScore (int score) {
        this.score = String.valueOf(score);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        FlurryAgent.onEndSession(getContext());
    }
}
