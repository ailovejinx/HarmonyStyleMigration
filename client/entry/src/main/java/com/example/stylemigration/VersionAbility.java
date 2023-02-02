package com.example.stylemigration;

import com.example.stylemigration.slice.VersionAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class VersionAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(VersionAbilitySlice.class.getName());
    }
}
