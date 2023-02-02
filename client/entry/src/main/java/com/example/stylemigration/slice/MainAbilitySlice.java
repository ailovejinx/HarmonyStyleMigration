package com.example.stylemigration.slice;

import com.example.stylemigration.ResourceTable;
import com.example.stylemigration.slice.LandscapeAbilitySlice;
import com.example.stylemigration.slice.TheoryAbilitySlice;
import com.example.stylemigration.slice.VangoghAbilitySlice;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.window.service.WindowManager;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;





public class MainAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        // 隐藏状态栏、设置状态栏和导航栏透明
        getWindow().addFlags(WindowManager.LayoutConfig.MARK_TRANSLUCENT_STATUS);
    }


    @Override
    public void onActive() {
        super.onActive();

        //点击按钮跳转至山水画界面
        Component ButtonLandscapeSlice = findComponentById(ResourceTable.Id_buttonLandscape);
        ButtonLandscapeSlice.setClickedListener(listener -> presentForResult(new LandscapeAbilitySlice() , new Intent(), 0));
        //点击按钮跳转至梵高界面
        Component ButtonVangoghSlice = findComponentById(ResourceTable.Id_buttonVangogh);
        ButtonVangoghSlice.setClickedListener(listener -> presentForResult(new VangoghAbilitySlice() , new Intent(), 0));
        //点击按钮跳转至原理介绍界面
        Component ButtonTheorySlice = findComponentById(ResourceTable.Id_buttonTheory);
        ButtonTheorySlice.setClickedListener(listener -> presentForResult(new TheoryAbilitySlice() , new Intent(), 0));
        //点击按钮跳转至版本信息界面
        Component ButtonVersionSlice = findComponentById(ResourceTable.Id_buttonVersion);
        ButtonVersionSlice.setClickedListener(listener -> presentForResult(new VersionAbilitySlice() , new Intent(), 0));
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
