package blocker.call.wallpaper.screen.caller.ringtones.callercolor.bean;

public class PermissionInfo {
    public int iconResId;
    public String title;
    public String permission;
    public String permissionDes;
    public String[] permissions;//该组权限下的所有权限
    public boolean isGet;
    public boolean isSpecialPermission;
    public int requestCode;
    public boolean isRequested;//是否请求过，不代表已经请求成功

}
