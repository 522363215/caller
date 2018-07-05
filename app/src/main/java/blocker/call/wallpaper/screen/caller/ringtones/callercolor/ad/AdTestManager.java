package blocker.call.wallpaper.screen.caller.ringtones.callercolor.ad;

import com.google.android.gms.ads.AdRequest;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.BuildConfig;

/**
 * Created by macbook on 16/5/10.
 */
public class AdTestManager {

    public static AdRequest.Builder getAdRequestBuilder() {
        AdRequest.Builder builder = new AdRequest.Builder();
        if (BuildConfig.DEBUG) {
            builder = addTestDevice(builder);
        }

        return builder;
    }

    private static AdRequest.Builder addTestDevice(AdRequest.Builder builder) {
        builder.addTestDevice("F1EA98DB10417D1CAA57FB84C10E65A8")
                .addTestDevice("448D25D6D98A759AD4E794239F3A1436")
                .addTestDevice("2D8833A83E5DF196ECAFB90FFC2C0699")
                .addTestDevice("707E27E4EB314B5739B58F7A1028232A")
                .addTestDevice("20133122A0C47CE503BCA5D4240C7D42")
                .addTestDevice("0AC88ECB15A1DEEB7BA9D3648B46CD72")
                .addTestDevice("A79C2A95292971A6E26E6C83E62E11B6")
                .addTestDevice("78437177B5A0953BD2AA6F4E1CE6D20C")
                .addTestDevice("B64B1B09C576C50ABC68ADAE9C122D0E")
                .addTestDevice("480406E7F57BED87E64BCAECEF2CA4ED")
                .addTestDevice("093CCC7735138F3BFC4E74E968759C8E")
                .addTestDevice("0143C99856C8FE5BFEC84527BC38F954")
                .addTestDevice("18253910C7CD9B6FEF1ED291DF8E080F")
                .addTestDevice("29CAAA67BD8E352BB267E4E40A2EA2E7")
                .addTestDevice("204F2EBC96F7633B4856A408C429CAFE")
                .addTestDevice("32BF2415E55B1A4BD9C21097F186892F")
                .addTestDevice("5A0F0961C1F6511BBAE2500C14559A5B")
                .addTestDevice("476308F779462E1A6308228388ADE09B")
                .addTestDevice("5867C143CF102F447CBD566F5BD03437")
                .addTestDevice("0639DB40D6CEC3BFDCB7522924DE8D43")
                .addTestDevice("915E1A0EC620A1861939846F1B4C9A7F")
                .addTestDevice("9D180E2953578B0FF9AFC20AF80D563C")
                .addTestDevice("1E0A209C905EF1D1E1B253266BBC1D0F")
                .addTestDevice("FCF6BE42D78183C88B69E9523615F11E")
                .addTestDevice("1EF6B28645005A42E3B208E29B7F747B")
                .addTestDevice("5A92EFC0A10C4119713CA9A8C4D0A46C")
                .addTestDevice("0804132C3E6EE35B97C1347C59FA859C")
                .addTestDevice("1760D98A6632E8F48D211E85C1E30DC7")
                .addTestDevice("DB58ACCC9D767D55C723306E83D94C1E")
                .addTestDevice("C77E879645A0B585F865C0537C93F98F")
                .addTestDevice("FF78E696930D1579723050C2741EB1EE")
                .addTestDevice("2737714CAED626CCB0A9CFA3A1E6B40B")
                .addTestDevice("D880F773BD80CA7E5CD2CCDAA8CEDD2C")
                .addTestDevice("C6A5E78B971B9C9221583CA0C02038BF")
                .addTestDevice("71AAB563768DEAB86EE212FAAF579D1F")
                .addTestDevice("21B19CFC1494BDFDE58B621D96E74B3C")
                .addTestDevice("0991A4704DB11FFDEB43496FC628CE15")
                .addTestDevice("5C67E25A2A2FAEC877AA166E1A6B8086")
                .addTestDevice("F87B47C8C6A24247AA8BA25DF801425A")
                .addTestDevice("E9A2326EADFC2AEC9C715D5AFB583B6A")
                .addTestDevice("A1EC1BF8BECEE1F808B26995569F989A")
                .addTestDevice("7261AB7AC80443488F504566AF15FCC9")
                .addTestDevice("3C7683D759A38C2E6A60F02522BC97D0")
                .addTestDevice("6C9A85EAFD92B3B50A9389D40FC5E345")
                .addTestDevice("5014A1B1E741BAA508909EC6614545AB")
                .addTestDevice("F23BEFAE74F9AD0DAC74568BD4605269")
                .addTestDevice("6B74A67A65A7A830B0A1F4B24FD87D3B")
                .addTestDevice("E7E389140059D99F7D76485EFC2F06AB")
                .addTestDevice("0FA7C8D69A7752E69740763B410D3C17")
                .addTestDevice("BB7E54F52827681B273C9A172FA25D2B")
                .addTestDevice("23FBD6210E949FD2EFAA2CE9C647009A")
                .addTestDevice("CAF73337BA24F5A80FC4F9A8E4501E82")
                .addTestDevice("aef9fc135d67895880b0788194fc4aec")
                .addTestDevice("00B35A30F52FAF43D13D3C8EECCA8ABA")
                .addTestDevice("B3AE016C2CDEE9BB403172BB8F3028EB");

        return builder;
    }
}
