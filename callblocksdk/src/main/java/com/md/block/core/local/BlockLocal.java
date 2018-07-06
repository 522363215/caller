package com.md.block.core.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.md.block.beans.BlockHistory;
import com.md.block.core.BlockManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by ChenR on 2018/7/4.
 */

public class BlockLocal {

    private static SharedPreferences mPreferences = null;

    private static final String BLOCK_SDK_SAVE_FILE = "caller_block_sdk_file";

    private static final String PREF_KEY_BLOCK_CONTACT_LIST = "caller_block_sdk_pref_key_block_contact_list";

    private static final String PREF_KEY_BLOCK_HISTORY_LIST = "caller_block_sdk_pref_sdk_block_history_list";

    private static final String PREF_KEY_BLOCK_SWITCH_STATE = "caller_block_sdk_pref_key_block_switch_state";

    public static boolean setBlockSwitchState (boolean isOpenBlock) {
        SharedPreferences pref = getSharedPreferences();
        if (pref == null) {
            return false;
        }

        return pref.edit().putBoolean(PREF_KEY_BLOCK_SWITCH_STATE, isOpenBlock).commit();
    }

    public static boolean getBlockSwitchState () {
        SharedPreferences pref = getSharedPreferences();
        return pref != null && pref.getBoolean(PREF_KEY_BLOCK_SWITCH_STATE, false);
    }

    public static Set<String> getBlockContacts () {
        SharedPreferences pref = getSharedPreferences();
        return pref == null ? null : pref.getStringSet(PREF_KEY_BLOCK_CONTACT_LIST, null);
    }

    public static boolean setBlockContacts (String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }

        Set<String> blockContacts = getBlockContacts();
        if (blockContacts == null) {
            blockContacts = new HashSet<>();
        }

        if (!blockContacts.contains(number)) {
            boolean add = blockContacts.add(number);
            SharedPreferences pref = getSharedPreferences();
            if (pref == null) {
                return false;
            }

            pref.edit().putStringSet(PREF_KEY_BLOCK_CONTACT_LIST, blockContacts).apply();
            return add;
        } else
            return true;

    }

    public static void setBlockContacts (List<String> blockContacts) {
        if (blockContacts == null || blockContacts.isEmpty()) {
            return;
        }

        for (String number : blockContacts) {
            setBlockContacts(number);
        }
    }

    public static boolean removeBlockContact (String number) {
        boolean isSuc = false;

        Set<String> contactSet = getBlockContacts();
        if (contactSet != null && contactSet.size() > 0) {
            Iterator<String> iterator = contactSet.iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (PhoneNumberUtils.compare(number, next)) {
                    iterator.remove();
                    isSuc = true;
                    break;
                }
            }
        }

        return isSuc;
    }

    public static void clearBlockContacts () {
        SharedPreferences pref = getSharedPreferences();
        if (pref != null) {
            pref.edit().putStringSet(PREF_KEY_BLOCK_CONTACT_LIST, null).commit();
        }
    }

    public static List<BlockHistory> getBlockHistory () {
        SharedPreferences pref = getSharedPreferences();

        if (pref == null) {
            return null;
        }

        Type type = new TypeToken<List<BlockHistory>>() {}.getType();
        List<BlockHistory> histories = new Gson().fromJson(pref.getString(PREF_KEY_BLOCK_HISTORY_LIST, ""), type);
        if (histories != null) {
            Collections.sort(histories, new Comparator<BlockHistory>() {
                @Override
                public int compare(BlockHistory o1, BlockHistory o2) {
                    if (o1.getBlockTime() < o2.getBlockTime()) {
                        return 1;
                    } else if (o1.getBlockTime() > o2.getBlockTime()) {
                        return -1;
                    } else
                        return 0;
                }
            });
        }
        return histories;
    }

    public static void setBlockHistory (BlockHistory history) {
        if (history == null) {
            return;
        }

        List<BlockHistory> blockHistory = getBlockHistory();
        if (blockHistory == null) {
            blockHistory = new ArrayList<>();
        }
        blockHistory.add(0, history);

        SharedPreferences pref = getSharedPreferences();
        if (pref != null) {
            return;
        }
        String historyJson = new Gson().toJson(blockHistory);
        SharedPreferences.Editor edit = pref.edit();
        if (edit != null) {
            edit.putString(PREF_KEY_BLOCK_HISTORY_LIST, historyJson).apply();
        }
    }

    public static boolean removeBlockHistory (BlockHistory history) {
        boolean isSuc = false;

        if (history != null) {
            SharedPreferences pref = getSharedPreferences();
            if (pref != null) {
                String data = pref.getString(PREF_KEY_BLOCK_HISTORY_LIST, "");
                Gson gson = new Gson();
                List<BlockHistory> saveHistory = gson.fromJson(data, new TypeToken<List<BlockHistory>>(){}.getType());
                if (saveHistory != null && saveHistory.size() > 0) {
                    Iterator<BlockHistory> iterator = saveHistory.iterator();
                    while (iterator.hasNext()) {
                        BlockHistory next = iterator.next();
                        if (next.getBlockTime() == history.getBlockTime()) {
                            isSuc = true;
                            iterator.remove();
                            break;
                        }
                    }

                    if (isSuc) {
                        String json = gson.toJson(saveHistory);
                        pref.edit().putString(PREF_KEY_BLOCK_HISTORY_LIST, json).commit();
                    }
                }
            }
        }

        return isSuc;
    }

    public static void clearBlockHistory () {
        SharedPreferences pref = getSharedPreferences();
        if (pref != null) {
            pref.edit().putString(PREF_KEY_BLOCK_HISTORY_LIST, "").commit();
        }
    }

    private static SharedPreferences getSharedPreferences() {
        if (mPreferences != null) {
            return mPreferences;
        }

        Context context = BlockManager.getInstance().getAppContext();
        if (context == null) {
            return null;
        }

        mPreferences = context.getSharedPreferences(BLOCK_SDK_SAVE_FILE, Context.MODE_PRIVATE);
        return mPreferences;
    }

    public static <T> void setPreferencesData(String key, T value) {
        SharedPreferences pref = getSharedPreferences();

        if (pref != null) {
            if (value instanceof String) {
                pref.edit().putString(key, String.valueOf(value)).commit();
            } else if (value instanceof Boolean) {
                pref.edit().putBoolean(key, (Boolean) value).commit();
            } else if (value instanceof Integer) {
                pref.edit().putInt(key, (Integer) value).commit();
            } else if (value instanceof Float) {
                pref.edit().putFloat(key, (Float) value).commit();
            } else if (value instanceof Long) {
                pref.edit().putLong(key, (Long) value).commit();
            }
        }
    }

    public static <T> T getPreferencesData (String key, T defaultValue) {
        SharedPreferences pref = getSharedPreferences();

        if (pref != null) {
            if (defaultValue instanceof String) {
                return (T) pref.getString(key, String.valueOf(defaultValue));
            } else if (defaultValue instanceof Boolean) {
                return (T) Boolean.valueOf(pref.getBoolean(key, (Boolean) defaultValue));
            } else if (defaultValue instanceof Integer) {
                return (T) Integer.valueOf(pref.getInt(key, (Integer) defaultValue));
            } else if (defaultValue instanceof Float) {
                return (T) Float.valueOf(pref.getFloat(key, (Long) defaultValue));
            } else if (defaultValue instanceof Long) {
                return (T) Long.valueOf(pref.getLong(key, (Long) defaultValue));
            }
        }
        return null;
    }

}
