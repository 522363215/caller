package blocker.call.wallpaper.screen.caller.ringtones.callercolor.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;


public class SaveingDialog extends BaseDialog {
    private Context mContext;

    public SaveingDialog(Context context) {
        super(context);
        mContext = context;
    }

    public SaveingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_saving_dialog);
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
