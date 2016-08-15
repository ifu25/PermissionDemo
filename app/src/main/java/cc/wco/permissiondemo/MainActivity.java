package cc.wco.permissiondemo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Desc: 权限获取示例
 * Author: XingGang
 * Date: 2016/08/15
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0; // 请求码

    //所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    private PermissionChecker mPermissionsChecker; //权限检测器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化权限检测器
        mPermissionsChecker = new PermissionChecker(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //检查是否缺少权限，如缺少则进入授权页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            //调用静态方法启动授权页面
            PermissionActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //拒绝时：关闭页面，缺少权限法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }
}
