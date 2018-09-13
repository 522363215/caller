package blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;


public class SavingDialog extends BaseDialog {
    private Context mContext;

    public SavingDialog(Context context) {
        super(context);
        mContext = context;
    }

    public SavingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_saving_dialog);
        setCanceledOnTouchOutside(false);
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
                && !event.isCanceled()) {
            //onBackPressed();
            return true;
        }
        return false;
    }
}
