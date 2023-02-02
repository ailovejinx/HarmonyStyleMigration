package com.example.stylemigration.slice;

import com.example.stylemigration.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.webengine.ResourceRequest;
import ohos.agp.components.webengine.WebView;
import ohos.agp.components.webengine.WebAgent;

public class VersionAbilitySlice extends AbilitySlice {
    private WebView webView;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_version);
        this.webView=(WebView)findComponentById(ResourceTable.Id_ability_main_webview);
        this.webView.setWebAgent(new WebAgent(){
            @Override
            public boolean isNeedLoadUrl(WebView webView, ResourceRequest request) {
                return super.isNeedLoadUrl(webView, request);
            }
        });
        this.webView.load("http://101.200.209.222:5050/version");
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
