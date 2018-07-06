package blocker.call.wallpaper.screen.caller.ringtones.callercolor.cpm;

import android.text.TextUtils;

import com.flurry.android.FlurryAgent;
import com.individual.sdk.AdType;
import com.individual.sdk.PubNativeAdContainer;
import com.millennialmedia.InlineAd;
import com.smaato.soma.AdDimension;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sandy on 18/5/4.
 */

public class CpmAdUtils {

    public static void flurryCpmLog(String msg, AdType adType) {
        Map<String, String> params = new HashMap<>();
        params.put("type", adType.toString());
        //params.put("where", loadType);
        FlurryAgent.logEvent(msg, params);
    }


    public static AdType getAdType(String prType) {
        if(TextUtils.isEmpty(prType)) {
            return null;
        }

        if(prType.startsWith(CpmAdPriority.AD_TYPE_IA)) {
            return AdType.TYPE_IA;
        }else if(prType.startsWith(CpmAdPriority.AD_TYPE_AS)) {
            return AdType.TYPE_AS;
        }else if(prType.startsWith(CpmAdPriority.AD_TYPE_SMT)) {
            return AdType.TYPE_SMT;
        }else if(prType.startsWith(CpmAdPriority.AD_TYPE_PN)) {
            return AdType.TYPE_PN;
        }else if(prType.startsWith(CpmAdPriority.AD_TYPE_MF)) {
            return AdType.TYPE_MF;
        }else if(prType.startsWith(CpmAdPriority.AD_TYPE_AOL)) {
            return AdType.TYPE_AOL;
        }

        return null;
    }

    // /////////////////
    // AdSize
    // /////////////////

    public static AdDimension getSmtSize(int sizeType) {
        switch (sizeType) {
            case 0:
                // 小图
                return AdDimension.DEFAULT;
            case 1:
                // 大图
                return AdDimension.MEDIUMRECTANGLE;

        }
        return AdDimension.DEFAULT;
    }

    public static int getPnSize(int sizeType) {
        switch (sizeType) {
            case 0:
                // 小图
                return PubNativeAdContainer.SIZE_BANNER;
            case 1:
                // 大图
                return PubNativeAdContainer.SIZE_MRECT;

        }
        return PubNativeAdContainer.SIZE_BANNER;
    }

    public static int[] getMfSize(int sizeType) {
        switch (sizeType) {
            case 0:
                // 小图
                return new int[]{320, 50};
            case 1:
                // 大图
                return new int[]{300, 250};

        }
        return new int[]{320, 50};
    }

    public static InlineAd.AdSize getAolSize(int sizeType) {
        switch (sizeType) {
            case 0:
                // 小图
                return InlineAd.AdSize.BANNER;
            case 1:
                // 大图
                return InlineAd.AdSize.MEDIUM_RECTANGLE;
        }
        return InlineAd.AdSize.BANNER;
    }

}
