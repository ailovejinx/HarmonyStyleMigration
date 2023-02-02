package com.example.stylemigration;

import com.example.stylemigration.slice.TakePhotoAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class TakePhotoAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(TakePhotoAbilitySlice.class.getName());
    }
}
