package me.zpleum.zcmd.client;

import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import me.zpleum.zcmd.config.ZCMDConfig;

public class ZCMDModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {

            ZCMDConfig config = ZcmdClient.CONFIG;

            if (config == null) {
                config = ZCMDConfig.load();
                ZcmdClient.CONFIG = config;
            }

            return new ZCMDConfigScreen(parent, config);
        };
    }
}