package com.md.block.core.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.md.block.beans.BlockInfo;
import com.md.block.callback.PhoneStateChangeCallback;
import com.md.block.core.BlockManager;
import com.md.block.core.local.BlockLocal;
import com.md.block.util.LogUtil;
import com.md.block.util.NumberUtil;

import java.util.List;

public class PhoneStateReceiver extends BroadcastReceiver {

    private Context mContext = null;
    private TelephonyManager mTele;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        mTele = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        List<PhoneStateChangeCallback> mPhoneStateChangeCallbackList = BlockManager.getInstance().getPhoneStateListenerList();
        String mCurrentNumber = "";

        try {
            String action = intent.getAction();

            if (action == null) action = "";
            if (action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                mCurrentNumber = getResultData();

                BlockLocal.setPreferencesData("number_is_block_status", false);
                BlockLocal.setPreferencesData("lm_current_phone_number", mCurrentNumber);

                BlockLocal.setPreferencesData("lm_is_call_coming", true);

                if (mPhoneStateChangeCallbackList != null) {
                    for (PhoneStateChangeCallback callback : mPhoneStateChangeCallbackList) {
                        if (callback != null) {
                            callback.onPhoneOutCall(mCurrentNumber);
                        }
                    }
                }
            } else if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                mCurrentNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                int callState = mTele.getCallState();
                switch (callState) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        String currentNumber = BlockLocal.getPreferencesData("", "");
                        currentNumber = currentNumber == null ? "" : currentNumber;
                        if (!TextUtils.isEmpty(currentNumber)) {
                            BlockLocal.setPreferencesData("last_phone_number", currentNumber);
                        }
                        BlockLocal.setPreferencesData("current_in_coming_call_number", "");

                        if (mPhoneStateChangeCallbackList != null) {
                            for (PhoneStateChangeCallback callback : mPhoneStateChangeCallbackList) {
                                if (callback != null) {
                                    callback.onPhoneIdle(mCurrentNumber);
                                }
                            }
                        }
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        BlockLocal.setPreferencesData("number_is_block_status", false);
                        BlockLocal.setPreferencesData("current_in_coming_call_number", mCurrentNumber);

                        executeBlock(mCurrentNumber);

                        if (mPhoneStateChangeCallbackList != null) {
                            for (PhoneStateChangeCallback callback : mPhoneStateChangeCallbackList) {
                                if (callback != null) {
                                    callback.onPhoneRinging(mCurrentNumber);
                                }
                            }
                        }

                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if (mPhoneStateChangeCallbackList != null) {
                            for (PhoneStateChangeCallback callback : mPhoneStateChangeCallbackList) {
                                if (callback != null) {
                                    callback.onPhoneOffHook(mCurrentNumber);
                                }
                            }
                        }
                        break;
                }

                Integer lastPhoneState = BlockLocal.getPreferencesData("lm_last_call_state", -1);
                if (lastPhoneState != null) {
                    if (lastPhoneState == TelephonyManager.CALL_STATE_RINGING && callState == TelephonyManager.CALL_STATE_IDLE) {
                        Boolean isBlock = BlockLocal.getPreferencesData("number_is_block_status", false);
                        isBlock = isBlock == null ? false : isBlock;
                        if (mPhoneStateChangeCallbackList != null) {
                            for (PhoneStateChangeCallback callback : mPhoneStateChangeCallbackList) {
                                if (callback != null) {
                                    if (isBlock) {
                                        callback.onPhoneBlock(mCurrentNumber);
                                    } else {
                                        callback.onPhoneReject(mCurrentNumber);
                                    }
                                }
                            }
                        }
                    } else if (lastPhoneState == TelephonyManager.CALL_STATE_OFFHOOK &&
                            callState == TelephonyManager.CALL_STATE_IDLE) {
                        if (mPhoneStateChangeCallbackList != null) {
                            for (PhoneStateChangeCallback callback : mPhoneStateChangeCallbackList) {
                                if (callback != null) {
                                    callback.onPhoneHangUp(mCurrentNumber);
                                }
                            }
                        }
                    } else if (lastPhoneState  == TelephonyManager.CALL_STATE_RINGING &&
                            callState == TelephonyManager.CALL_STATE_OFFHOOK) {
                        BlockLocal.setPreferencesData("lm_is_call_coming", true);
                        if (mPhoneStateChangeCallbackList != null) {
                            for (PhoneStateChangeCallback callback : mPhoneStateChangeCallbackList) {
                                if (callback != null) {
                                    callback.onPhoneAnswer(mCurrentNumber);
                                }
                            }
                        }
                    }
                }

                BlockLocal.setPreferencesData("lm_last_call_state", callState);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void executeBlock(String number) {
        if (BlockLocal.getBlockSwitchState() && BlockManager.getInstance().blockCall(number)) {
            BlockLocal.setPreferencesData("number_is_block_status", true);

            LogUtil.d("chenr", "block success.");

            BlockInfo history = new BlockInfo();
            history.setNumber(number);
            history.setName(getContactNameForNumber(number));
            history.setBlockTime(System.currentTimeMillis());

            BlockLocal.setBlockHistory(history);
        }
    }

    public String getContactNameForNumber(String number) {
//        number = getFormatNumber(number);
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
        }
        String name = "";
        String selection = "REPLACE(REPLACE(REPLACE(REPLACE(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ",')','')" + ",' ','')" + ",'(','')" + ",'-','') Like ?";
        Cursor query = null;
        try {
            query = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    },
                    selection,
                    new String[]{"%" + number}, null);

            if (query != null && query.moveToFirst()) {
                do {
                    String cursorNumber = NumberUtil.getNumberByPattern(query.getString(1));
                    if (Math.abs(cursorNumber.length() - number.length()) <= 6) {
                        name = query.getString(query.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        break;
                    }
                    break;
                } while (query.moveToNext());
            }
        } catch (Exception e) {

        } finally {
            if (query != null) {
                query.close();
            }
        }
        if (name == null) {
            name = "";
        }
        return name;
    }

}
