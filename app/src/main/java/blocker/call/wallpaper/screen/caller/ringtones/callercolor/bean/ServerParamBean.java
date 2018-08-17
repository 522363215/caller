package blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 服务端控制参数Bean
 */

public class ServerParamBean implements Serializable {

    /**
     * status : {"code":0,"time":"6.4361095428467ms","msg":""}
     * data : {"switch_spam_server":"0","switch_use_spam_third":"0","refresh_tag_interval":"24 * 60 * 60 * 1000","refresh_tag_no_tag_interval":"2 * 60 * 60 * 1000","refresh_update_spam_local_interval":"24 * 60 * 60 * 1000","bench_spam_tag_count":"40","bench_show_spam_tag_count":"20", "url_spam_third":"https://www.show-caller.com/"}
     */

    private StatusBean status;

    public DataBean getDataBean() {
        return data;
    }

    public void setDataBean(DataBean data) {
        this.data = data;
    }

    public DataBean data;

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }



    public class StatusBean implements Serializable{
        /**
         * code : 0
         * time : 6.4361095428467ms
         * msg :
         */

        private int code = -3;
        private String time;
        private String msg;
        private String timestamp;
        private String millisecond;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getMillisecond() {
            return millisecond;
        }

        public void setMillisecond(String millisecond) {
            this.millisecond = millisecond;
        }
    }

    public class DataBean implements Serializable{
        public int switch_spam_server = 0; //是否从服务端获取spam tag总开关， 默认0打开
        public int switch_use_spam_third = 0; //是否从第三方获取tag开关， 默认0打开
        public long refresh_tag_interval = 24 * 60 * 60 * 1000; //有tag时刷新tag的间隔时间, 毫秒， 1 day default
        public long refresh_tag_no_tag_interval = 2 * 60 * 60 * 1000; //没有tag时刷新tag的间隔时间，毫秒，2 hours default, no tag or name yet
        public long update_spam_local_interval = 24 * 60 * 60 * 1000; //更新spamdb的间隔时间，毫秒，1 day default, update spam db interval
        public int bench_spam_tag_count = 40; //tag数量认定是spam的标准， 默认40
        public int top_spam_count_from_server = 200; //top spam count from server， 默认200
        public int bench_show_spam_tag_count = 20; //tag数量show spam dialog but not block， 默认20
        public String url_spam_third = ""; //获取spam第三方url, 默认https://www.show-caller.com/, https://www.shouldianswer.com/
        public String url_search_cc = ""; //号码搜索url， 默认为空

        public int switch_full_after_call = 0; //电话后全屏, 默认0开启
        public int switch_full_after_call_delays = 0; //电话后全屏, 关闭后， 预埋打开天数, 默认0， 不打开
        public int switch_flash_call_notify = 0; //老用户收到新的电话闪屏通知, 默认关闭

        //ads
        public String not_show_ads_splash_list = ""; //不显示启动页广告的国家列表 //US,
        public String not_show_interstitial = ""; //不显示插屏页广告的国家列表
        public String show_interstitial_position = ""; //显示插屏的位置开关
        public int is_show_interstitial = 0; //是否显示插屏广告总开关. default is 0 , show

        //du ads , baidu ads
        public int du_ad_master_switch = 0; //

        public int du_ad_result_switch = 0;
        public String du_ad_result_countries = "";
        public int du_ad_result_limit = 98;

        public int du_ad_call_flash_switch = 0;
        public String du_ad_call_flash_countries = "";
        public int du_ad_call_flash_limit = 98;

        public int du_ad_startup_switch = 0;
        public String du_ad_startup_countries = "";
        public int du_ad_startup_limit = 98;

        // du end

        //big fb ads white spaces clickable
        public int fb_big_white_spaces_clickable = 0; //fb 大图广告空白区域是否可以点击, 默认0 不可点击
        public String big_fb_clickable_position = ""; //fb 位置 (p_big_splash, end p_big_end_call)大图广告空白区域可以点击
        public String big_fb_clickable_countries = ""; //fb 国家(US, CA), 图广告空白区域可以点击

        //非联系人是否显示名字, 默认0显示, 1 全部关闭， 2 全部打开
        public int is_show_user_smart_id = 0;

        //common_spam_block
        public int switch_common_spam_block = 0; //0, 默认值 不改变本地设置， 1, 全部关闭,

        public int set_endcall_msg = 0; //电话结束页和短信页显示开关, 0 不作任何操作， 1 全部关闭， 2 全部打开

        public int is_show_ad_endcall = 1; //电话结束页和短信页广告显示开关, 0显示, 1不显示

        public int is_use_sms_result = 1; //是否使用短信清理结果页独立fb id , 默认 1 使用， 0为使用合并id, 99 new fb id
        public String new_use_sms_result_id = ""; //99 - 对应的新fb id
        public int is_use_spam_update_result = 0; //是否使用号码升级库结果页独立fb id , 默认 0 不使用， 使用合并id
        public String new_spam_update_fb_id = ""; //99 对应的新fb id
        public int is_use_calllog_list = 0; //是否通话记录列表页独立fb id , 1 使用独立id, 0 使用合并id, 99 定制
        public String new_custom_calllog_fb_id = "";  //99 - 对应的新fb id
        public int is_use_contact_list = 1; //是否联系人列表列表页独立fb id , 默认 1 使用, 2 合并来电秀设置页id
        public String new_custom_contact_fb_id = ""; //99 - 对应的新fb id
        public int is_use_splash = 0; //是否使用splash页独立fb id , 默认 0 使用合并联系人页面id， 1 使用独立id, 2 使用endcall id, 99 new id

        public String new_splash_fb_id = ""; //99 - 对应的新fb id

        public int is_use_call_gif_list = 1; //来电秀在线gif下载列表是否独立fb id , 1 使用独立id, 0 使用合并id
        public String new_custom_call_gif_fb_id = ""; //99 - 对应的新fb id
        public int is_use_sms_list = 0; //短信列表是否独立fb id , 1 使用独立id, 0 使用合并id
        public String new_custom_smslist_fb_id = "";//99 - 对应的新fb id
        public int is_use_block_scan_home = 0; //号码拦截首页是否独立fb id , 1 使用独立id, 0 使用合并id
        public String new_custom_blockhome_fb_id = ""; //99 - 对应的新fb id
        public int is_use_fakecall_home = 0; //假电话首页是否独立fb id , 1 使用独立id, 0 使用合并id, 99 自定义， 2 新虚拟来电秀设置id
        public String fake_call_home_fb_id = ""; //99 - 对应的新fb id
        public int is_use_sms_come = 0; //短信详情页是否独立fb id , 1 使用独立id, 0 使用合并id
        public int is_use_sms_edit_send = 0; //短信编辑发送页是否独立fb id , 1 使用独立id, 0 使用合并id, 99 定制
        public String new_custom_sms_edit_fb_id = ""; //99 - 对应的新fb id

        public int is_use_endcall = 0; //是否使用new id, 默认0 - 不使用, 99 使用
        public String new_endcall_fb_id = ""; //99 - 对应的新fb id
        public int is_use_scanresult = 0; //是否使用new id, 默认0 - 不使用, 99 使用
        public String new_scanresult_fb_id = ""; //99 - 对应的新fb id
        //call flash set 设置页
        public int is_use_callflash = 0; //0 使用合并id; 1 独立id; 99 server配置id 是否使用new id, 默认0 - 不使用, 99 使用
        public String new_callflash_fb_id = ""; //99 - 对应的新fb id

        //call flash set 设置结果页
        public int is_use_call_flash_set = 1; //默认1 使用独立fb id,  99 使用 new
        public String new_call_flash_set_fb_id = ""; //99 - 对应的新fb id

        //call flash ad 背景 ， 边距控制
        public int callflashbottom = 0;//call flash fb广告距离底部边距， 默认0, 可能调整为6or8
        public String call_flash_bg_color = ""; // 默认黑色, FF0B2938


        public int is_show_tip_callflash_exit = 0; //退出callflash setting是否弹窗提示, 0 默认弹出
        public String not_show_tip_callflash_exit_list = ""; //不显示弹窗提示的国家, 默认没有
        public int is_enable_callflash_default = 0; //是否默认开启flashcall， 0 默认不开启
        public String not_enable_callflash_default_list = ""; //不默认开启flash call的国家, 默认没有, example "US,"


        public int splash_ad_not_skip = 0; //是否自动跳过splash ads， 1 否, 默认0， 跳过
        public int splash_ad_not_skip_count = 1; //不自动跳过splash ads时默认显示次数 1
        public int splash_ad_max_count = 99; //notskip 模式, 启动页最多广告次数，默认99 = 不限制
        //"not_show_ads_splash_list": "US,",
        //"not_show_interstitial": "US,CA,GB,AU,JP,DE,DK,FR,IT,FI,ES,NL,AT,BE,NO,SE,CH",
        //"not_show_ads_first_time": "US,CA,GB,AU,JP,DE,DK,FR,IT,FI,ES,NL,AT,BE,NO,SE,CH"
        //"show_interstitial_position": "p_in_main,p_in_calllog,p_in_endcall,p_in_smscoming"


        //smart lock
        public int smart_lock_param = 0; //0 不做任何操作, 1 全部打开， 2 全部关闭

        //插页广告控制
        //in ads ,
        public int in_ads_result_switch = 0; //0 not show, 1 show
        public String in_ads_result_countries = "";
        public int in_ads_result_limit = 98;

        //in ads call flash
        public int show_interstitial_call_flash = 1; //0 not show, 1 show

        //fb结果页插页广告fb_id
        public int is_in_ads_result_new = 0; //是否使用new id, 默认0 - 不使用, 99 使用
        public String new_in_ads_result_fb_id = ""; //99 - 对应的新fb id


        //通话记录详情页fb_id
        public int is_phone_detail_new = 0; //是否使用new id, 默认0 - 不使用, 99 使用
        public String new_phone_detail_fb_id = ""; //99 - 对应的新fb id

        //联系人大头像fb_id
        public int is_contact_big_new = 0; //是否使用new id, 默认0 - 不使用, 99 使用
        public String new_contact_big_fb_id = ""; //99 - 对应的新fb id

        //more来电秀下载页面
        public int call_flash_down_new = 0; //是否使用new id, 默认0 - 不使用, 99 使用
        public String call_flash_down_new_id = ""; //99 - 对应的新fb id

        //短信秀设置fb_id
        public int is_sms_flash_set_new = 0; //是否使用new id, 默认0 - 不使用, 99 使用
        public String sms_flash_set_new_id = ""; //99 - 对应的新fb id

        //短信秀设置结果页fb_id
        public int is_sms_flash_set_result_new = 0; //是否使用new id, 默认0 - 不使用, 99 使用
        public String sms_flash_set_result_new_id = ""; //99 - 对应的新fb id

        //短信秀收到短信fb_id
        public int is_sms_flash_show_new = 0; //是否使用new id, 默认0 - 不使用, 99 使用
        public String sms_flash_show_new_id = ""; //99 - 对应的新fb id

        //ps 带量开关
        public int is_enable_lf_intro = 0; //是否打开带量，0 打开

        //返回显示启动页控制，目前只有联系人列表 activity
        public int is_show_splash_current_switch = 0; //0 not show, 1 show
        public String is_show_splash_current_countries = "";
        public long last_show_back_splash_interval = 2 * 60 * 60 * 1000;//

        //来电秀, 短信秀 - 跳动的按钮是否可以点击，默认可以点击
        public int is_flash_btn_clickable = 0; // 0 可点击

        //ad img 作为背景时默认不可点击. 2017-12 fb 新政
        public int is_ad_img_background_clickable = 0; // 0 不可点击
        //
        public int is_endcall_only_btn_clickable = 0; //fb大图广告, 只有按钮可点， 默认 0 否
        public int is_scan_result_only_btn_clickable = 0; //fb大图广告
        public int is_splash_only_btn_clickable = 0; //fb大图广告
        public int is_call_flash_set_only_btn_clickable = 1; //fb 来电秀设置只有按钮可点，0 否, 206版本后只有元素可点
        public int is_sms_flash_set_only_btn_clickable = 0;//短信秀设置
        //
        public int is_endcall_only_btn_percent = 100;
        public int is_scan_result_only_btn_percent = 100;
        public int is_splash_only_btn_percent = 100;
        public int is_call_flash_set_only_btn_percent = 100;
        public int is_sms_flash_set_only_btn_percent = 100;

        //white space clickable
        public int is_scan_result_white_clickable = 0; //0 默认不可点击
        public int is_splash_white_clickable = 0;
        public int is_scan_result_white_percent = 100;
        public int is_splash_white_percent = 100;

        //mopub
        public int mopub_master_switcher = 0; //0不显示, 总开关
        public String mopub_show_ads_countries = ""; //显示mopub的国家

        //random ad_id
        public int cid_random_adid_enable = 0; //总开关, 0 默认 开
        public JSONObject cid_random_ad_ids; //mopub banner ads ids

        //分渠道 ， 广告区域点击控制
        public int is_smsedit_img_bg_clickable = 0;
        public int is_smsshow_img_bg_clickable = 0;

        public String channel_fb_big_whitespaces = "";
        public String channel_fb_big_image_background = "";
        public String channel_fb_big_only_btn = "";

        //动态添加下载来电秀间隔时间
        public long caller_show_server_down_interval = 2 * 60 * 60 * 1000;//
        //插入的位置
        public int new_flash_insert_position = 11; //默认11

        //是否创建联系人快捷方式入口
        public int is_need_contact_shortcut = 1; //0 不创建, 1 创建
        //是否创建短信快捷方式入口
        public int is_need_sms_shortcut = 1; //0 不创建, 1 创建

        //联系人大头像是否显示广告
        public int is_contact_big_ad_show = 0; //0 不显示
        public int is_call_flash_hot_big_ad_show = 0;//0 不显示

        //从外部返回主页是否过splash
        public int is_show_splash_from_others = 0; //0 过splash

        public int is_kill_to_release = 0; //1 is kill


        public int is_show_record_on_home = 0; // show call record icon on home

        public int show_end_call_max_count = 99; //max show end call count

        //splash
        public long splash_ad_first_load = 6500;
        public long splash_ad_load = 4500;
        public long splash_ad_show = 4000;
        //来电秀接听后，接听状态返回时间内认定成功，默认1秒
        public long answer_call_succ_bench = 1000;

        //插屏结果页 新
        public int show_interstitial_all_result = 0;//0 not show
        public long interstitial_from_install_bench = 0; //us 等发达国家 , 安装24小时后才显示插屏, 24 * 60 * 60 * 1000=86400000

        public int in_ads_admob_result_new = 0; //admob 插屏id, 0 默认， 99 新
        public String in_ads_admob_result_new_id = ""; //对应99

        public int load_fb_in_for_adx=1; //1 load, fb 插屏补漏

        //来电秀设置banner 多层请求
        public int is_use_group_banner = 1; //1 use, o not use
        public int is_use_group_ads = 1;

        public int is_use_group_banner_new = 1; //1 use, o not use

        //开屏新来电秀提示fb id
        public int screen_on_call_flash_new = 0; //99 new
        public String screen_on_call_flash_new_id = "";

        //开屏时弹出新来电秀提示
        public int is_show_new_flash_tips = 1; //0 显示， 1 不显示



        public int is_show_in_ad_first_scan = 0; //1 show, 0 not show

        public int days_start_night_after_install = 3; //0 not enable

        public int is_use_splash_new_style = 0; //1 use new,

        public String missed_call_fb_id = "";

        //
        public int request_theme_count_max = 100; //服务端来电秀素材请求个数，默认最大100

        //first_mode admob id
        public String first_mode_splash_admob_id = "";
        public String first_mode_smsflash_admob_id = "";
        public String first_mode_result_scan_admob_id = "";
        public String first_mode_result_update_admob_id = "";
        public String first_mode_result_flashset_admob_id = "";

        public String first_mode_result_smsset_admob_id = "";

        public String first_mode_result_onback_admob_id = "";
        public String first_mode_callflash_preview_admob_id = "";
        public String first_mode_callflash_download_admob_id = "";

        public String first_mode_flash_down_onback_admob_id = "";

        public int is_use_magic_button = 0; //来电秀下载页， 按钮跳开, 1 跳开

        //swipe
        public String swipe_fb_id = "";
        public int swipe_toogle_by_server = 0; //1 enable, 2 stop
        public int swipe_ad_refresh = 15; //fb ad 请求间隔分钟，默认15

        //preload check interval
        public int preload_ad_check_interval = 239;

        public String splash_fb_id = ""; //
        //
        public int is_auto_go_main = 0; //0 自动跳过splash，1 不自动
        public int  is_show_ad_end_call = 0;//1-渠道量才显示，默认不显示, 电话结束显示广告
    }
}
