package com.md.block.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.md.block.core.BlockManager;

public class PhoneStateReceiver extends BroadcastReceiver {

    private Context mContext = null;
    private String mCurrentNumber = "";
    private TelephonyManager mTele;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        mTele = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                mCurrentNumber = getResultData();

            } else if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                mCurrentNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                int callState = mTele.getCallState();
                switch (callState) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        executeBlock();

                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void executeBlock() {
        if (BlockManager.getInstance().blockCall(mCurrentNumber)) {

        }
    }

}
