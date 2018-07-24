package com.md.block.core.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.md.block.beans.BlockInfo;
import com.md.block.core.BlockManager;
import com.md.block.util.LogUtil;

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

    public static List<BlockInfo> getBlockContacts () {
        SharedPreferences pref = getSharedPreferences();
        if (pref != null) {
            String data = pref.getString(PREF_KEY_BLOCK_CONTACT_LIST, "");
            Gson gson = new Gson();
            Type type = new TypeToken<List<BlockInfo>>() {}.getType();
            return gson.fromJson(data, type);
        }
        return null;
    }

    public static boolean setBlockContacts (BlockInfo contact) {
        if (contact == null || TextUtils.isEmpty(contact.getNumber())) {
            return false;
        }

        List<BlockInfo> blockContacts = getBlockContacts();
        if (blockContacts == null) {
            blockContacts = new ArrayList<>();
        }

        if (!blockContacts.contains(contact)) {
            blockContacts.add(contact);
            SharedPreferences pref = getSharedPreferences();
            if (pref == null) {
                return false;
            }

            return pref.edit().putString(PREF_KEY_BLOCK_CONTACT_LIST, new Gson().toJson(blockContacts)).commit();
        } else
            return true;

    }

    public static void setBlockContacts (List<BlockInfo> blockContacts) {
        if (blockContacts == null) {
            return;
        }
        SharedPreferences pref = getSharedPreferences();
        if (pref == null) {
            return;
        }
        pref.edit().putString(PREF_KEY_BLOCK_CONTACT_LIST, new Gson().toJson(blockContacts)).commit();
    }

    public static boolean existInBlockContacts (String number) {
        boolean exist = false;
        if (TextUtils.isEmpty(number)) {
            return false;
        }

        List<BlockInfo> blockContactList = getBlockContacts();
        if (blockContactList != null && blockContactList.size() > 0) {
            for (BlockInfo contact : blockContactList) {
                if (PhoneNumberUtils.compare(contact.getNumber(), number)) {
                    exist = true;
                    break;
                }
            }
        }

        return exist;
    }

    public static boolean removeBlockContact (String number) {
        boolean isSuc = false;

        List<BlockInfo> contactList = getBlockContacts();
        if (contactList != null && contactList.size() > 0) {
            Iterator<BlockInfo> iterator = contactList.iterator();
            while (iterator.hasNext()) {
                BlockInfo next = iterator.next();
                if (PhoneNumberUtils.compare(number, next.getNumber())) {
                    iterator.remove();
                    setBlockContacts(contactList);
                    isSuc = true;
                    break;
                }
            }
        }

        return isSuc;
    }

    public static boolean removeBlockContact (BlockInfo contact) {
        boolean isSuc = false;

        List<BlockInfo> contactList = getBlockContacts();
        if (contactList != null && contactList.size() > 0) {
            Iterator<BlockInfo> iterator = contactList.iterator();
            while (iterator.hasNext()) {
                BlockInfo next = iterator.next();
                if (next.equals(contact)) {
                    iterator.remove();
                    isSuc = true;
                    break;
                }
            }
            if (isSuc) {
                setBlockContacts(contactList);
            }
        }

        return isSuc;
    }

    public static void clearBlockContacts () {
        SharedPreferences pref = getSharedPreferences();
        if (pref != null) {
            pref.edit().putString(PREF_KEY_BLOCK_CONTACT_LIST, "").commit();
        }
    }

    public static List<BlockInfo> getBlockHistory () {
        SharedPreferences pref = getSharedPreferences();

        if (pref == null) {
            return null;
        }

        Type type = new TypeToken<List<BlockInfo>>() {}.getType();
        List<BlockInfo> histories = new Gson().fromJson(pref.getString(PREF_KEY_BLOCK_HISTORY_LIST, ""), type);
        if (histories != null) {
            Collections.sort(histories, new Comparator<BlockInfo>() {
                @Override
                public int compare(BlockInfo o1, BlockInfo o2) {
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

    public static void setBlockHistory (BlockInfo history) {
        if (history == null) {
            return;
        }
        List<BlockInfo> blockInfo = getBlockHistory();
        if (blockInfo == null) {
            blockInfo = new ArrayList<>();
        }
        blockInfo.add(0, history);

        SharedPreferences pref = getSharedPreferences();
        if (pref == null) {
            return;
        }
        String historyJson = new Gson().toJson(blockInfo);
        pref.edit().putString(PREF_KEY_BLOCK_HISTORY_LIST, historyJson).commit();
    }

    public static boolean removeBlockHistory (BlockInfo history) {
        boolean isSuc = false;

        if (history != null) {
            SharedPreferences pref = getSharedPreferences();
            if (pref != null) {
                String data = pref.getString(PREF_KEY_BLOCK_HISTORY_LIST, "");
                Gson gson = new Gson();
                List<BlockInfo> saveHistory = gson.fromJson(data, new TypeToken<List<BlockInfo>>(){}.getType());
                if (saveHistory != null && saveHistory.size() > 0) {
                    Iterator<BlockInfo> iterator = saveHistory.iterator();
                    while (iterator.hasNext()) {
                        BlockInfo next = iterator.next();
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
