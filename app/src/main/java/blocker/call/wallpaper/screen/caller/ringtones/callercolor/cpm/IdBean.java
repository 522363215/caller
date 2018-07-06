package blocker.call.wallpaper.screen.caller.ringtones.callercolor.cpm;

/**
 * Created by Sandy on 18/4/14.
 */

public class IdBean {

    public static final String PR_IA = "pr_ia";
    public static final String PR_AS = "pr_as";

    private int low = 0;
    private int up = 0;
    private String id;
    private String AdType;

    public IdBean(int low, int up, String id, String adType) {
        this.low = low;
        this.up = up;
        this.id = id;
        AdType = adType;
    }

    public String getId() {
        return id;
    }

    public String getAdType() {
        return AdType;
    }

    public boolean isInLimited(int num) {
        return num >= low && num < up;
    }

}
