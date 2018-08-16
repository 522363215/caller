package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.app.Application;
import android.widget.FrameLayout;

import com.quick.easyswipe.EasySwipe;
import com.quick.easyswipe.callback.EasySwipeFunctionCallback;
import com.quick.easyswipe.callback.EasySwipeViewCallback;
import com.quick.easyswipe.callback.QuickSwitchCallback;
import com.quick.easyswipe.callback.QuickSwitchResultCallback;
import com.quick.easyswipe.callback.ServerConfigCallback;
import com.quick.easyswipe.type.EasySwipeItem;

import java.util.ArrayList;
import java.util.List;

import blocker.call.wallpaper.screen.caller.ringtones.callercolor.R;

public class SwipeUtil {
    public static void initEasySwipe(Application application) {
        // 1. 全局初始化, 传入Applicatoin
        EasySwipe.init(application);
        // 2. 快划布局的隐藏和显示回调, 用于显示广告
        EasySwipe.setEasySwipeViewCallback(new EasySwipeViewCallback() {
            @Override
            public void swipeViewShown(FrameLayout frameLayout) {
                //frameLayout是侧滑布局中的广告布局
            }

            @Override
            public void swipeViewHidden() {
            }
        });
        // 3. 通过服务器配置，设置快划功能
        EasySwipe.setServerConfigCallback(new ServerConfigCallback() {
            // 快划是否默认开启, 默认为false
            @Override
            public boolean defaultEasySwipeOn() {
                return false;//ServerConfig.esp_def_on
            }

            // 点击快划页面空白区域是否自动关闭, 默认为false(不关闭)
            @Override
            public boolean blankClickCancelable() {
                return true;//ServerConfig.esp_blank_clk_ccb
            }

            // "常用应用"的菜单是否显示自己, 默认为false(不显示)
            @Override
            public boolean needDisplaySelf() {
                return false;
            }

            // "最近应用"菜单中是否隐藏自己, 默认为true(隐藏)
            @Override
            public boolean needHideInRecent() {
                return true;
            }

            // 点击快划菜单角落的关闭按钮(叉), 延迟关闭快划布局的时间（毫秒), 默认为0
            @Override
            public long delayedCloseSwipeViewTimeMillis() {
                return 0;//ServerConfig.esp_close_dly_mm
            }

            // 快划菜单栏中，是否在快捷工具栏中显示手电筒菜单, 需要camera权限
            @Override
            public boolean enableFlashLight() {
                return false;
            }
            // 快划菜单快捷工具栏中是否显示蓝牙功能，需要Bluetooth相关权限，建议不使用(false)

            // 仅在1.0.4.8支持
            @Override
            public boolean enableBluetooth() {
                return false;
            }
        });
        // 4. 设置快划菜单中"快捷工具"按钮行为
        EasySwipe.setQuickSwitchCallback(new QuickSwitchCallback() {
            @Override
            public void openSettingActivity(QuickSwitchResultCallback quickSwitchResultCallback) {
//                SwipeSettingActivity.startForFlags(ApplicationEx.getInstance());
                if (quickSwitchResultCallback != null) {
                    //true-使用了外部打开设置页面的方式
                    quickSwitchResultCallback.isUseExternal(true);
                }
            }

            @Override
            public void openFlashlight(QuickSwitchResultCallback quickSwitchResultCallback) {
                //点击手电筒按钮
                if (quickSwitchResultCallback != null) {
                    //如果使用其他方式打开手电筒 isUseExternal返回true
                    //使用easeyswipe默认方式打开手电筒 isUseExternal返回false
                    quickSwitchResultCallback.isUseExternal(false);
                }
            }
        });
        // 注：如果想自定义菜单替换”最近应用”菜单栏，则使用下面方法
        initEasySwipeMenu();
        //启动快划服务
        EasySwipe.tryStartService();
    }

    // EasySwipeItem用来装载用户自定义的item信息
    private static void initEasySwipeMenu() {
        List<EasySwipeItem> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            EasySwipeItem item;
            item = new EasySwipeItem(i, R.drawable.ic_launcher, R.string.app_name);
            // 参数1：自定义功能item的id，用于点击click事件标记你点击的是哪个自定义功能item
            // 参数2：自定义功能item的图标资源id
            // 参数3：自定义功能item的名称资源id
            // 参数4：自定义功能item的图标ImageView的background资源id 默认蓝色圆形则写-1,自定义参照文档末尾swipe_item_bg.xml
            list.add(item);
        }
        //swipe菜单模块：常用应用，快捷工具，自定义菜单(替换 最近应用)
        //参数2：自定菜单的图标；参数3：true快划布局中只显示单个自定义菜单;false则显示三个菜单
        EasySwipe.customSwipeMenu(list, R.drawable.ic_launcher); //扩展菜单中item的点击事件
        EasySwipe.setEasySwipeFunctionCallback(new EasySwipeFunctionCallback() {
            @Override
            public void swipeFunctionClick(int i) {
                //i是 EasySwipeItem对象中的id
                switch (i) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }
        });
    }
}
