package blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message;

import java.util.ArrayList;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean.ContactInfo;

/**
 * Created by admin on 2017/1/11.
 */

public class EventQueryContact {
    ArrayList<ContactInfo> mContacts;

    public EventQueryContact(ArrayList<ContactInfo> mContacts) {
        this.mContacts = mContacts;
    }


    public ArrayList<ContactInfo> getmContacts() {
        return mContacts;
    }

}
