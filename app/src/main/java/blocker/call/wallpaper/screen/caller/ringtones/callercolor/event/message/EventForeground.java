package blocker.call.wallpaper.screen.caller.ringtones.callercolor.event.message;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cattom on 2/22/2016.
 */
public class EventForeground implements Parcelable {
    public boolean m_bIsForeground = false;


    public EventForeground(boolean bForeground) {
        m_bIsForeground = bForeground;
    }

    protected EventForeground(Parcel in) {
        m_bIsForeground = in.readByte() != 0;
    }

    public static final Creator<EventForeground> CREATOR = new Creator<EventForeground>() {
        @Override
        public EventForeground createFromParcel(Parcel in) {
            return new EventForeground(in);
        }

        @Override
        public EventForeground[] newArray(int size) {
            return new EventForeground[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (m_bIsForeground ? 1 : 0));
    }
}
