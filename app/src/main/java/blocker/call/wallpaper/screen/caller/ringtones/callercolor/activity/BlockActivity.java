package blocker.call.wallpaper.screen.caller.ringtones.callercolor.activity;

import android.os.Bundle;
import android.view.View;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.view.ActionBar;

/**
 * Created by ChenR on 2018/7/5.
 */

public class BlockActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);

        listener();
    }

    private void listener() {
        ((ActionBar) findViewById(R.id.action_bar)).setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}
