package blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean;

/**
 * Created by ChenR on 2018/7/24.
 */

public class NotifyInfo {

    private int NOTIFY_ID = 0;

    private String title = "";
    private String content = "";
    public String arg1 = "";
    public String arg2 = "";

    public int getNotifyId() {
        return NOTIFY_ID;
    }

    /**
     * @param notifyId 使用NotifyType接口所定义的id类型;
     *                     {@link NotifyId}
     */
    public void setNotifyId(int notifyId) {
        this.NOTIFY_ID = notifyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public interface NotifyId {
        // 拦截电话;
        int NOTIFY_BLOCK_CALL = 1;
        // 新的来电秀;
        int NOTIFY_NEW_FLASH = 2;
    }

}
