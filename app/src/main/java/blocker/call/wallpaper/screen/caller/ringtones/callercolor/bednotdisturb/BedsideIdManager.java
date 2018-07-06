package blocker.call.wallpaper.screen.caller.ringtones.callercolor.bednotdisturb;


import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;


/**
 * 管理一个渠道的id集
 * Created by Sandy on 18/5/4.
 */

public class BedsideIdManager {

    private final List<BedsideIdBean> idList = new ArrayList<>();

    public BedsideIdManager(JSONArray array) {
        if (array != null) {
            int length = array.length();
            for (int i = 0; i < length; i++) {
                try {
                    String[] idConfig = array.getString(i).trim().split(":");
                    if (idConfig.length > 2) {
                        idList.add(new BedsideIdBean(
                                idConfig[0],
                                Integer.parseInt(idConfig[1]),
                                Integer.parseInt(idConfig[2])
                        ));
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 是否所有id今天都已经请求满
     *
     * @return
     */
    public boolean isAllMaxLoadToday() {
        if (idList == null || idList.size() <= 0) {
            return true;
        }
        for (BedsideIdBean bean : idList) {
            if (!bean.isMaxToday()) {
                return false;
            }
        }
        return true;
    }

    public BedsideIdBean genarateNextId() {
        if (idList == null || idList.size() <= 0) {
            return null;
        }

        // 先将valid的对象过滤出来
        List<BedsideIdBean> validList = new ArrayList<>();
        int total = 0;
        for (BedsideIdBean bean : idList) {
            if (!bean.isMaxToday()) {
                validList.add(bean);
                total += bean.getMaxLoadCount();
                //LogUtils.d(Constants.TAG, "getNextId, filterValidBean:" + bean.toString());
            }
        }

        // 没有可请求的id了
        if (total == 0 || validList.size() == 0) {
            return null;
        }

        // 从剩余valid总数中取一个随机数，随机一个id出来
        int random = getRandomRefrZone(total);
        int low = 0;
        int up = 0;
        for (BedsideIdBean bean : validList) {
            up = low + bean.getMaxLoadCount();
            if (low <= random && random < up) {
                return bean;
            }
            low = up;
        }

        return null;
    }

    private int getRandomRefrZone(int maxCount) {
        try {
            int randomZone = new Random().nextInt(maxCount);
            return randomZone;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        if (BuildConfig.DEBUG) {
            String log = "list:";
            for (BedsideIdBean bean : idList) {
                log += "\n" + bean.toString();
            }
            return log;
        }
        return super.toString();
    }
}
