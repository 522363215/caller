package blocker.call.wallpaper.screen.caller.ringtones.callercolor.cpm;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Sandy on 18/4/14.
 */

public class TestCpmId {

    final List<IdBean> idList = new ArrayList<>();
    int totalWeight = 0;

    // ["181999:1:pr_ia"]
    public TestCpmId(String arrayStr) throws JSONException {
        JSONArray array = new JSONArray(arrayStr);
        if(array != null) {
            int length = array.length();
            for(int i=0; i<length; i++) {
                String item = array.getString(i);
                String[] sets = item.split(":");
                int weight = Integer.parseInt(sets[1]);
                IdBean bean = new IdBean(totalWeight, totalWeight + weight, sets[0], sets[2]);
                totalWeight += weight;
                idList.add(bean);
            }
        }
    }

    public IdBean generateId() {
        if(totalWeight > 0) {
            int randomNum = new Random().nextInt(totalWeight);
            for(IdBean bean:idList) {
                if(bean.isInLimited(randomNum)) {
                    return bean;
                }
            }
        }
        return null;
    }

    public boolean isValid() {
        return idList.size() > 0 && totalWeight > 0;
    }


}
