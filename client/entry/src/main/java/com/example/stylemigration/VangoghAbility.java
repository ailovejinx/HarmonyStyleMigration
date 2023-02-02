package com.example.stylemigration;

import com.example.stylemigration.slice.VangoghAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class VangoghAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(VangoghAbilitySlice.class.getName());
    }
}
