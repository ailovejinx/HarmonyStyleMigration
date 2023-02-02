package com.example.stylemigration;

import com.example.stylemigration.slice.TheoryAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class TheoryAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(TheoryAbilitySlice.class.getName());
    }
}
