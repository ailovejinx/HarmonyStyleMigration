package com.example.stylemigration;

import com.example.stylemigration.slice.LandscapeAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class LandscapeAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(LandscapeAbilitySlice.class.getName());
    }
}
