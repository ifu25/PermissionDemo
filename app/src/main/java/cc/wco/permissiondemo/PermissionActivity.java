package cc.wco.permissiondemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

/**
 * Desc: 权限获取页面
 * Author: XingGang
 * Date: 2016/08/15
 */
public class PermissionActivity extends AppCompatActivity {
    public static final int PERMISSIONS_GRANTED = 1; //权限授权
    public static final int PERMISSIONS_DENIED = 0; //权限拒绝
    private static final int PERMISSION_REQUEST_CODE = 0; //系统权限管理页面的参数
    private static final String EXTRA_PERMISSIONS = "extra_permission"; //权限参数

    private PermissionChecker mChecker; // 权限检测器
    private boolean isRequireCheck; // 是否需要系统权限检测

    //启动当前权限页面的公开接口
    public static void startActivityForResult(Activity activity, int requestCode, String... permissions) {
        Intent intent = new Intent(activity, PermissionActivity.class);
        intent.putExtra(EXTRA_PERMISSIONS, permissions);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //判断是否是通过公开接口调用此页面
        if (getIntent() == null || !getIntent().hasExtra(EXTRA_PERMISSIONS)) {
            throw new RuntimeException("PermissionsActivity需要通过startActivityForResult静态方法启动！");
        }

        LinearLayout linearLayout = new LinearLayout(this);
        setContentView(linearLayout);

        mChecker = new PermissionChecker(this);
        isRequireCheck = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isRequireCheck) {
            String[] permissions = getIntent().getStringArrayExtra(EXTRA_PERMISSIONS); //获取需要检查的权限
            if (mChecker.lacksPermissions(permissions)) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE); //请求权限
            } else {
                allPermissionsGranted(); //全部权限都已获取
            }
        } else {
            isRequireCheck = true;
        }
    }

    /**
     * 处理授权结果。
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            isRequireCheck = true;
            allPermissionsGranted();
        } else {
            isRequireCheck = false;
            showMissingPermissionDialog();
        }
    }

    //是否含有全部的权限
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    //显示缺失权限提示
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PermissionActivity.this);
        builder.setTitle("系统提示");
        builder.setMessage("当前应用缺少必要权限。\n\n请点击\"设置\"-\"权限\"-打开所需权限。\n\n最后点击两次后退按钮即可返回。");

        //拒绝：退出应用
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(PERMISSIONS_DENIED);
                finish();
            }
        });

        //报团
        builder.setPositiveButton("前去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //启动系统的应用的设置页面
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName())); //当前应用包名
                startActivity(intent);
            }
        });

        builder.show();
    }

    //全部权限均已获取，返回调用页面
    private void allPermissionsGranted() {
        setResult(PERMISSIONS_GRANTED);
        finish();
    }
}
