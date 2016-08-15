package cc.wco.permissiondemo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Desc: 检查权限工具类
 * Author: XingGang
 * Date: 2016/08/15
 */
public class PermissionChecker {
    private final Context mContext;

    public PermissionChecker(Context context) {
        mContext = context.getApplicationContext();
    }

    //判断权限集合，有一个未授权即认为缺少权限
    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    //判断是否缺少权限
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_DENIED;
    }
}