package blocker.call.wallpaper.screen.caller.ringtones.callercolor.helper.rate;

import android.net.Uri;

public final class UriHelper {

   private static final String GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=";

   private UriHelper() {
   }

   public static Uri getGooglePlay(String packageName) {
       return packageName == null ? null : Uri
               .parse(GOOGLE_PLAY + packageName);
   }

}