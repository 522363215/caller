package blocker.call.wallpaper.screen.caller.ringtones.callercolor.manager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.ApplicationEx;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.CallLogInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.ContactInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.NumberInfo;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventContactChange;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message.EventQueryContact;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.PreferenceHelper;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.CommonUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.ConstantUtils;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.FileUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.IconUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.LogUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.NumberUtil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Stringutil;
import blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils.Utils;

/**
 * Created by zhq on 2017/1/10.
 */

public class ContactManager {
    private static final String TAG = "ContactManager";
    private static ContactManager sInstance;
    private Context mContext;

    private ArrayList<ContactInfo> mContacts = new ArrayList<>();
    public Map<String, NumberInfo> mContactCache = new HashMap<>();
    private ArrayList<NumberInfo> mNumberInfos = new ArrayList<>();

    public boolean isContactInitComplete() {
        return isContactInitComplete;
    }

    private boolean isContactInitComplete;
    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);

    private ContactManager() {
        mContext = ApplicationEx.getInstance().getBaseContext();
    }

    public static ContactManager getInstance() {
        if (sInstance == null) {
            synchronized (ContactManager.class) {
                sInstance = new ContactManager();
                return sInstance;
            }
        }
        return sInstance;
    }

    /**
     * 获取联系人信息的集合
     */
    public synchronized void loadContact() {
        PreferenceHelper.putBoolean(PreferenceHelper.PREF_CONTACT_IS_QUERY_COMPLETE, false);
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = null;
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        final String[] PROJECTS = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                ContactsContract.Contacts.SORT_KEY_PRIMARY,
                ContactsContract.Contacts.LOOKUP_KEY,
        };


        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //如果android操作系统版本4.4或4.4以上就要用phonebook_label而不是sort_key字段
//        if (android.os.Build.VERSION.SDK_INT >= 19) {
//            PROJECTS[4] = "phonebook_label";
//            cursor = resolver.query(uri, PROJECTS, null, null, "phonebook_label");
//        } else {
//
//        }
        cursor = resolver.query(uri, PROJECTS, null, null, ContactsContract.Contacts.SORT_KEY_PRIMARY);
        mContacts.clear();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    try {
                        long id = cursor.getLong(0);
                        String name = cursor.getString(1);
                        String photoId = cursor.getString(2);
                        int phoneCount = cursor.getInt(3);
                        String sortKey = cursor.getString(4);
                        String lookupKey = cursor.getString(5);

                        ContactInfo contact = new ContactInfo();
                        contact.id = id;
                        contact.contactName = name;
                        contact.userAddName = null;
                        contact.photoId = photoId;
                        contact.isContact = true;
                        contact.phoneCount = phoneCount;
//                        contact.sortKey = Stringutil.getSortKey(ContactUtil.getPingYin(name));
                        LogUtil.d("ContactManager", "loadContact name:" + contact.contactName + ",pinyin:" + contact.sortKey);
//                        if (android.os.Build.VERSION.SDK_INT >= 19) {
//                            contact.sortKey = TextUtils.isEmpty(sortKey) ? "#" : sortKey;
//                            LogUtil.d("ContactManager", "loadContact name:" + contact.contactName + ",sortKey:" + sortKey);
//                        } else {
//                            contact.sortKey = Stringutil.getSortKey(sortKey);
//                            LogUtil.d("ContactManager", "loadContact name:" + contact.contactName + ",sortKey:" + sortKey);
//                        }
                        contact.lookupKey = lookupKey;
                        if ("0".equals(photoId) || photoId == null) {
                            contact.isHavePhoto = false;
                        } else {
                            contact.isHavePhoto = true;
                        }
                        mContacts.add(contact);
                        LogUtil.d("ContactManagerdadada", "Contact:" + contact.contactName + "," + contact.id);
                    } catch (Exception e) {
                        LogUtil.e("contactManager", "queryContactCursor:" + e.getMessage());
                    }
                } while (cursor.moveToNext());
            }

            ContactSortComparator contactSortComparator = new ContactSortComparator();
            try {
                Collections.sort(mContacts, contactSortComparator);
            } catch (Exception e) {
                LogUtil.e("contactmanager", "contactSortComparator e:" + e.getMessage());
            }

            PreferenceHelper.putBoolean(PreferenceHelper.PREF_CONTACT_IS_QUERY_COMPLETE, true);
            EventBus.getDefault().post(new EventQueryContact(mContacts));
            if (cursor != null)
                cursor.close();
        }
    }

    public ArrayList<ContactInfo> getAllContact() {
        LogUtil.d("PhoneDetailsActivity", "联系人个数：" + mContacts.size() + "");
        return mContacts;
    }

    public long getContactIdForNumber(String number) {
        long id = -1;
        Cursor phoneCursor = null;
        String nationalNumber = NumberUtil.getFormatNumber(number);
        try {
            phoneCursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER},
                    "REPLACE(REPLACE(REPLACE(REPLACE(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ",')','')" + ",' ','')" + ",'(','')" + ",'-','') Like ?",
                    new String[]{"%" + nationalNumber}, null);
            if (phoneCursor != null && phoneCursor.moveToFirst()) {
                do {
                    String cursorNumber = NumberUtil.getNumberByPattern(phoneCursor.getString(1));
                    if (Math.abs(cursorNumber.length() - nationalNumber.length()) <= 5) {
                        id = phoneCursor.getLong(0);
                        break;
                    }
                } while (phoneCursor.moveToNext());
            }
        } catch (Exception e) {
            LogUtil.e("contactManager", "phoneCursor:" + e.getMessage());
        } finally {
            if (phoneCursor != null)
                phoneCursor.close();
        }
        return id;
    }

    /**
     * 根据联系人ID或电话号码获取号码详情界面的信息
     * if ID == -1 则为非联系人，使用number
     * if ID !=-1 则为联系人，使用ID，不使用number
     */
    public ContactInfo getPhoneDetailInfoForIdOrNumber(long id, String number) {
        LogUtil.d("PhoneDetailsActivity", "联系人ID：" + id);
        if (id != -1) {
            //联系人界面跳转过来
            Cursor cursor = null;
            ContactInfo contactInfo = new ContactInfo();
            try {
                ContentResolver resolver = mContext.getContentResolver();
                Uri uri = ContactsContract.Contacts.CONTENT_URI;
                final String[] PROJECTS = new String[]{
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts.PHOTO_ID,
                        ContactsContract.Contacts.HAS_PHONE_NUMBER,
                        ContactsContract.Contacts.LOOKUP_KEY,
                };
                cursor = resolver.query(uri, PROJECTS, ContactsContract.Contacts._ID + "=?", new String[]{String.valueOf(id)}, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        contactInfo.id = cursor.getLong(0);
                        contactInfo.contactName = cursor.getString(1);
                        contactInfo.userAddName = null;
                        contactInfo.photoId = cursor.getString(2);
                        contactInfo.phoneCount = cursor.getInt(3);
                        contactInfo.lookupKey = cursor.getString(4);
                        LogUtil.d(TAG, "phoneDetailInfo,photoID:" + contactInfo.photoId + ",photoUri:" + contactInfo.photoUri);
                        if ("0".equals(contactInfo.photoId) || contactInfo.photoId == null) {
                            contactInfo.isHavePhoto = false;
                        } else {
                            contactInfo.isHavePhoto = true;
                        }
                        contactInfo.isContact = true;
                        break;
                    } while (cursor.moveToNext());
                }
                if (contactInfo.phoneCount > 0) {
                    List<NumberInfo> numberList = getNumberListForContactId(id);
                    List<CallLogInfo> callLogsList = getCallLogsForNumberList(numberList);
                    contactInfo.numberInfoList = numberList;
                    contactInfo.callLogInfoListForContact = callLogsList;
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "getContactInfoInPhoneDetailsActivity  e:" + e.getMessage());
            } finally {
                if (cursor != null) cursor.close();
            }
            return contactInfo;
        } else if (!TextUtils.isEmpty(number)) {
            //非联系人界面跳转过来
            ContactInfo phoneDetailInfo = new ContactInfo();
            NumberInfo numberInfo = getContact(number);
            if (numberInfo != null) {
                LogUtil.d(TAG, "edit contact get id:" + numberInfo.id + ",lookupkey:" + numberInfo.lookupKey);
                phoneDetailInfo.id = numberInfo.id;
                phoneDetailInfo.lookupKey = numberInfo.lookupKey;
                phoneDetailInfo.contactName = numberInfo.name;
                phoneDetailInfo.userAddName = null;
                phoneDetailInfo.isContact = true;
                phoneDetailInfo.photoId = numberInfo.photoId;
                if ("0".equals(phoneDetailInfo.photoId) || phoneDetailInfo.photoId == null) {
                    phoneDetailInfo.isHavePhoto = false;
                } else {
                    phoneDetailInfo.isHavePhoto = true;
                }

            } else {
                numberInfo = new NumberInfo();
                phoneDetailInfo.userAddName = "";
                phoneDetailInfo.contactName = null;
                phoneDetailInfo.isHavePhoto = false;
                phoneDetailInfo.isContact = false;
            }

            numberInfo.location = NumberUtil.getNumberLocation(number);
            phoneDetailInfo.phoneCount = 1;

            List<NumberInfo> numberList = new ArrayList<>();
            numberInfo.number = number;
            numberList.add(numberInfo);
            phoneDetailInfo.numberInfoList = numberList;

            List<CallLogInfo> callLogsList = getCallLogsForNumberList(numberList);
            phoneDetailInfo.callLogInfoListForContact = callLogsList;

            return phoneDetailInfo;
        }
        return null;
    }

    /**
     * 根据该联系人下的所有电话号码获取该联系人的所有通话记录
     */
    private ArrayList<CallLogInfo> getCallLogsForNumberList(List<NumberInfo> numberList) {
        ArrayList<CallLogInfo> calllogsListForContact = new ArrayList<>();
        for (NumberInfo info : numberList) {
            String nationalNumber = NumberUtil.getNumberByPattern(info.number);
            LogUtil.d("CallLogManager", TAG);
            String[] project = null;
            Uri uri = CallLog.Calls.CONTENT_URI;
            project = new String[]{
                    CallLog.Calls.DATE,//日期
                    CallLog.Calls.DURATION,//通话时长
                    CallLog.Calls.TYPE,//通话类型
            };
            //查询的条件(半年以前的毫秒数)
            long queryDate = System.currentTimeMillis() - (6 * 30 * 24 * 3600 * 1000l);
            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                    return null;
                }
            }
            Cursor cursor = null;
            String selection = CallLog.Calls.DATE + "> ? AND REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(" + CallLog.Calls.NUMBER + ",')','')" + ",' ','')" + ",'(','')" + ",'-','') " + ",'–','') Like ?";
            try {
                cursor = mContext.getContentResolver().query(uri, project, selection, new String[]{String.valueOf(queryDate), "%" + nationalNumber}, CallLog.Calls.DEFAULT_SORT_ORDER);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        CallLogInfo callLog = new CallLogInfo();
                        callLog.callNumber = info.number;
                        callLog.date = cursor.getLong(0);
                        callLog.callType = cursor.getInt(2);
                        callLog.callDuration = cursor.getLong(1);
                        calllogsListForContact.add(callLog);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        Collections.sort(calllogsListForContact, new CalllogSortComparator());
        return calllogsListForContact;
    }

    private class CalllogSortComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            CallLogInfo info1 = (CallLogInfo) o1;
            CallLogInfo info2 = (CallLogInfo) o2;
            if (info1.date < info2.date) {
                return 1;
            }
            return -1;
        }
    }

    private class ContactSortComparator implements Comparator<ContactInfo> {
        @Override
        public int compare(ContactInfo info1, ContactInfo info2) {
            if (Stringutil.getASCII(info1.sortKey) < Stringutil.getASCII(info2.sortKey)) {
                return -1;
            } else if (Stringutil.getASCII(info1.sortKey) > Stringutil.getASCII(info2.sortKey)) {
                return 1;
            } else {
                return 0;
            }
//             else if (Stringutil.getASCII(info1.sortKey) == Stringutil.getASCII(info2.sortKey)) {
//                if (("Z".toUpperCase().charAt(0) + 1) == Stringutil.getASCII(info1.sortKey)) {
//                    if (!TextUtils.isEmpty(info1.contactName) && !TextUtils.isEmpty(info2.contactName)) {
//                        char[] name1 = info1.contactName.toCharArray();
//                        char[] name2 = info2.contactName.toCharArray();
//                        if (name1.length > name2.length) {
//                            for (int i = 0; i < name2.length; i++) {
//                                char a1 = name1[i];
//                                char a2 = name2[i];
//                                LogUtil.d("contactMaager", "ContactSortComparator #  a1:" + a1 + ",a2:" + a2);
//                                if (Character.isDigit(a1) && Character.isDigit(a2)) {
//                                    if (a1 < a2) {
//                                        return -1;
//                                    }
//                                }
//                            }
//                        } else {
//                            for (int i = 0; i < name1.length; i++) {
//                                char a1 = name1[i];
//                                char a2 = name2[i];
//                                LogUtil.d("contactMaager", "ContactSortComparator #  a1:" + a1 + ",a2:" + a2);
//                                if (Character.isDigit(a1) && Character.isDigit(a2)) {
//                                    if (a1 < a2) {
//                                        return -1;
//                                    }
//                                }
//                            }
//                        }
//                    }
//                } else if (("A".toUpperCase().charAt(0) - 1) == Stringutil.getASCII(info1.sortKey)) {
//
//                }
//            }
        }
    }

    private class NumberInfoSortComparator implements Comparator<NumberInfo> {
        @Override
        public int compare(NumberInfo info1, NumberInfo info2) {
            if (Stringutil.getASCII(info1.sortKey) < Stringutil.getASCII(info2.sortKey)) {
                return -1;
            } else if (Stringutil.getASCII(info1.sortKey) > Stringutil.getASCII(info2.sortKey)) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public ArrayList<NumberInfo> getNumberListForContactId(long id) {
        Cursor phoneCursor = null;
        ArrayList<NumberInfo> numberList = new ArrayList<>();
        try {
            phoneCursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
            numberList.clear();
            if (phoneCursor != null && phoneCursor.moveToFirst()) {
                do {
                    //遍历所有的联系人下面所有的电话号码
                    String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    int phoneNumberType = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    NumberInfo info = new NumberInfo();
                    info.number = phoneNumber;
                    info.numberType = phoneNumberType;
                    info.location = NumberUtil.getNumberLocation(info.number);
                    //获取number的归属地
                    numberList.add(info);
                } while (phoneCursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (phoneCursor != null)
                phoneCursor.close();
        }
        Set<NumberInfo> numberInfoSet = new HashSet<>();
        numberInfoSet.addAll(numberList);
        numberList.clear();
        numberList.addAll(numberInfoSet);
        return numberList;
    }

    public Bitmap getContactPhoto(String photoId) {
        String[] projection = new String[]{ContactsContract.Data.DATA15};
        String selection = ContactsContract.Data._ID + " = " + photoId;
        Cursor cur = null;
        try {
            cur = mContext.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI, projection, selection, null, null);
            if (cur != null && cur.moveToFirst()) {
                byte[] contactIcon = cur.getBlob(0);
                if (contactIcon == null) {
                    return null;
                } else {
                    Bitmap map = BitmapFactory.decodeByteArray(contactIcon, 0, contactIcon.length);
                    return map;
                }
            }
        } catch (Exception e) {
            LogUtil.e("contactManager", "cur:" + e.getMessage());
        } finally {
            if (cur != null)
                cur.close();
        }

        return null;
    }

    public String getContactPhotoId(String number) {
        String photoId = null;
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = null;
        String nationalNumber = NumberUtil.getFormatNumber(number);
        try {
            cursor = mContext.getContentResolver().query(
                    uri,
                    new String[]{ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                            ContactsContract.CommonDataKinds.Phone.NUMBER},
                    "REPLACE(REPLACE(REPLACE(REPLACE(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ",')','')" + ",' ','')" + ",'(','')" + ",'-','') Like ?",
                    new String[]{"%" + nationalNumber},
                    null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String cursorNumber = NumberUtil.getNumberByPattern(cursor.getString(1));
                        if (Math.abs(cursorNumber.length() - nationalNumber.length()) <= 5) {
                            photoId = cursor.getString(0);
                            break;
                        }
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return photoId;
    }

    /**
     * 根据号码获取联系人信息
     */
    public NumberInfo getContact(final String number) {
        NumberInfo numberInfo = null;
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = null;
        String nationalNumber = NumberUtil.getFormatNumber(number) == null ? "" : NumberUtil.getFormatNumber(number).replace(" ", "").replace("–", "").replace("-", "").replace("(", "").replace(")", "");
        try {
            String condition = "REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ",')','')" + ",' ','')" + ",'(','')" + ",'–','')" + ",'-','') Like ?";
            cursor = mContext.getContentResolver().query(
                    uri,
                    new String[]{
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                            ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                    },
                    condition,
                    new String[]{"%" + nationalNumber},
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    LogUtil.d(TAG, "number  asad number:" + number + ",name:" + cursor.getString(0));
                    String cursorNumber = NumberUtil.getNumberByPattern(cursor.getString(2));
                    if (Math.abs(cursorNumber.length() - nationalNumber.length()) <= 5) {
                        String name = cursor.getString(0);
                        String photoId = cursor.getString(1);
                        String lookupKey = cursor.getString(3);
                        long contactId = cursor.getLong(5);
                        numberInfo = new NumberInfo();
                        numberInfo.id = contactId;
                        numberInfo.name = name;
                        numberInfo.number = number;
                        numberInfo.photoId = photoId;
                        numberInfo.lookupKey = lookupKey;
                        if ("0".equals(photoId) || photoId == null) {
                            numberInfo.isHavePhoto = false;
                        } else {
                            numberInfo.isHavePhoto = true;
                        }
                        LogUtil.d(TAG, "number  number:" + number + ",name:" + name + ",cusorNumber:" + cursor.getString(2));
                        break;
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return numberInfo;
    }

//    public boolean isContact(String number) {
//        boolean isContact = false;
//        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//        Cursor cursor = null;
//        String nationalNumber = NumberUtil.getFormatNumber(number) == null ? "" : NumberUtil.getFormatNumber(number).replace(" ", "");
//        try {
//            //LogUtil.d("updatecalllog","number:"+number+",nationalNumber:"+nationalNumber);
//            cursor = mContext.getContentResolver().query(uri, null,
//                    "REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ",')','')" + ",' ','')" + ",'(','')" + ",'–','')" + ",'-','') Like ?", new String[]{"%" + nationalNumber}, null);
//            if (cursor != null && cursor.moveToFirst()) {
//                do {
//                    String cursorNumber = NumberUtil.getNumberByPattern(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//                    if (Math.abs(cursorNumber.length() - nationalNumber.length()) <= 5) {
//                        isContact = true;
//                        break;
//                    }
//                } while (cursor.moveToNext());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//        return isContact;
//    }

    public String getContactNameForNumber(String number) {
        number = NumberUtil.getFormatNumber(number);
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                LogUtil.e("contactManager", "getNameByNumber: Request permission to query Phone.NUMBER");
                return null;
            }
        }
        String name = null;
        String selection = "REPLACE(REPLACE(REPLACE(REPLACE(" + ContactsContract.CommonDataKinds.Phone.NUMBER + ",')','')" + ",' ','')" + ",'(','')" + ",'-','') Like ?";
        Cursor query = null;
        try {
            query = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    },
                    selection,
                    new String[]{"%" + number},
                    null);

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
            LogUtil.e("contactManager", "query contactName by numbernocc: " + e.getMessage());
        } finally {
            if (query != null) {
                query.close();
            }
        }
        if (name == null) {
            name = "";
        }
        LogUtil.d(TAG, "getContactNameForNumber number:" + number + ",contactName:" + name);
        return name;
    }

//    private ContentObserver newContactContentObserver = null;
//    public void registerContactContentObserver(final ContactChangedCallBack callBack) {
//        unregisterContactContentObserver();
//        mContext.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true,
//                newContactContentObserver = new ContentObserver(new Handler()) {
//                    @Override
//                    public void onChange(boolean selfChange, Uri uri) {
//                        super.onChange(selfChange, uri);
//                        LogUtil.d(TAG, "contact change");
//                        if (callBack != null) {
//                            callBack.onContactChange(selfChange, uri);
//                        }
//                    }
//                });
//    }
//    public synchronized void unregisterContactContentObserver() {
//        try {
//            if (newContactContentObserver != null) {
//                mContext.getContentResolver().unregisterContentObserver(newContactContentObserver);
//            }
//            if (newContactContentObserver != null) {
//                mContext.getContentResolver().unregisterContentObserver(newContactContentObserver);
//            }
//        } catch (Exception e) {
//            Log.e("ContactManager", "unregisterObserver fail");
//        }
//    }
//    public interface ContactChangedCallBack {
//        void onContactChange(boolean selfChange, Uri changedUri);
//    }

    public void init() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                LogUtil.d(TAG, "contacts init number dadad :");
                NumberInfo numberInfo = null;
                Cursor cursor = null;
                mContactCache.clear();
                mNumberInfos.clear();
                String sort = null;
                isContactInitComplete = false;
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                try {
                    cursor = mContext.getContentResolver().query(
                            uri,
                            new String[]{
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                    ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                    ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY
                            },
                            null,
                            null,
                            sort);
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            String name = cursor.getString(0);
                            String photoId = cursor.getString(1);
                            String number = cursor.getString(2);
                            String lookupKey = cursor.getString(3);
                            long contactId = cursor.getLong(4);
                            numberInfo = new NumberInfo();
                            numberInfo.id = contactId;
                            numberInfo.name = name;
                            numberInfo.number = number;
                            numberInfo.photoId = photoId;
                            numberInfo.lookupKey = lookupKey;
//                            numberInfo.sortKey = Stringutil.getSortKey(ContactUtil.getPingYin(name));
                            mContactCache.put(NumberUtil.getFormatNumberForDb(number), numberInfo);
                            mNumberInfos.add(numberInfo);
                            LogUtil.d(TAG, "contacts init number:" + NumberUtil.getFormatNumberForDb(number) + ",numberInfo:" + numberInfo);
                        } while (cursor.moveToNext());
                    }
                    isContactInitComplete = true;
                    NumberInfoSortComparator numberinfoSortComparator = new NumberInfoSortComparator();
                    try {
                        Collections.sort(mNumberInfos, numberinfoSortComparator);
                    } catch (Exception e) {
                        LogUtil.e("contactmanager", "numberinfoSortComparator e:" + e.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            }
        }.start();
    }


    public NumberInfo getContactInContactCache(String number) {
        NumberInfo numberInfo = mContactCache.get(NumberUtil.getFormatNumberForDb(number));
        return numberInfo;
    }

    public List<NumberInfo> getNumberInfos() {
        return mNumberInfos;
    }

    ExecutorService mFixedThreadPool = Executors.newFixedThreadPool(1);
    private boolean mIsUpdateContactData;
    private ContentObserver mContactsContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        public void onChange(boolean selfChange) {
            boolean isContactChanged = isContactChanged();
            LogUtil.d("mContactsContentObserver", "contact change isContactChanged:" + isContactChanged);
            if (!isContactChanged) return;
            LogUtil.d("mContactsContentObserver", "contact changed");
            initContactIdAndVersion();
            EventBus.getDefault().post(new EventContactChange());
            ContactManager.getInstance().init();
            LogUtil.d("contactmanager", "NumberScanManager init");
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    loadContact();
                }
            });
        }
    };

    public void registerContentObserver() {
        unregisterContentObserver();
        mContext.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true,
                mContactsContentObserver);
    }

    public synchronized void unregisterContentObserver() {
        try {
            if (mContactsContentObserver != null) {
                mContext.getContentResolver().unregisterContentObserver(mContactsContentObserver);
            }
        } catch (Exception e) {
            Log.e("ContactManager", "unregisterObserver fail");
        }
    }

    public NumberInfo getSearchContactDate(Cursor cursor) {
        NumberInfo numberInfo = new NumberInfo();
        final int contactIdPos = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
        final int namePos = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        final int numberPos = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        final int phonotoIdPos = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);

        numberInfo.id = cursor.getLong(contactIdPos);

        numberInfo.name = cursor.getString(namePos);

        numberInfo.number = cursor.getString(numberPos);

        numberInfo.photoId = cursor.getString(phonotoIdPos);

        LogUtil.d(TAG, "search getSearchContactDate name:" + numberInfo.name + ",number:" + numberInfo.number);
        return numberInfo;
    }


    public void initContactIdAndVersion() {
        CommonUtils.wrappedSubmit(fixedThreadPool, new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hashMap = new HashMap<>();
                ContentResolver _contentResolver = mContext.getContentResolver();
                Cursor cursor = _contentResolver.query(
                        ContactsContract.RawContacts.CONTENT_URI, null, null, null,
                        null);
                if (cursor == null) return;
                while (cursor.moveToNext()) {
                    String contactID = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.RawContacts._ID));
                    String contactVersion = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.RawContacts.VERSION));

                    hashMap.put(contactID, contactVersion);
                }
                PreferenceHelper.putHashMapData(PreferenceHelper.PREF_CONTACT_VERSION_FOR_ID_MAP, hashMap);
                cursor.close();
            }
        });
    }

    /**
     * 判断联系人是否有变化
     */
    public boolean isContactChanged() {
        boolean theReturn = false;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        HashMap<String, String> hashMap = PreferenceHelper.getHashMapData(PreferenceHelper.PREF_CONTACT_VERSION_FOR_ID_MAP, String.class);
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(
                    ContactsContract.RawContacts.CONTENT_URI, null, null, null,
                    null);
        } catch (Exception e) {
            LogUtil.e(TAG, "isContactChanged e:" + e.getMessage());
        }
        if (cursor == null) return theReturn;
        if (hashMap.size() < cursor.getCount() || hashMap.size() > cursor.getCount()) {
            theReturn = true;
        } else {
            while (cursor.moveToNext()) {
                String contactID = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.RawContacts._ID));
                String contactVersion = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.RawContacts.VERSION));
                if (hashMap.containsKey(contactID)) {
                    String version = hashMap.get(contactID);
                    if (!version.equals(contactVersion)) {
                        theReturn = true;
                        break;
                    }
                } else {
                    theReturn = true;
                    break;
                }
            }
        }
        cursor.close();
        return theReturn;
    }
}
