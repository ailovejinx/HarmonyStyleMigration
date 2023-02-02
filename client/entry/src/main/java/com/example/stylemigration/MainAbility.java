package com.example.stylemigration;

import com.example.stylemigration.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.bundle.IBundleManager;
import ohos.security.SystemPermission;

import java.util.Arrays;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        requestPermissions();
    }
    //申请权限
    private void requestPermissions() {
        String[] permissions = {
                SystemPermission.WRITE_USER_STORAGE, SystemPermission.READ_USER_STORAGE, SystemPermission.CAMERA,
                SystemPermission.MICROPHONE, SystemPermission.LOCATION
        };
        requestPermissionsFromUser(Arrays.stream(permissions)
                .filter(permission -> verifySelfPermission(permission) != IBundleManager.PERMISSION_GRANTED).toArray(String[]::new), 0);
    }

    @Override
    public void onRequestPermissionsFromUserResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions == null || permissions.length == 0 || grantResults == null || grantResults.length == 0) {
            return;
        }
        for (int grantResult : grantResults) {
            if (grantResult != IBundleManager.PERMISSION_GRANTED) {
                terminateAbility();
                break;
            }
        }
    }
}
