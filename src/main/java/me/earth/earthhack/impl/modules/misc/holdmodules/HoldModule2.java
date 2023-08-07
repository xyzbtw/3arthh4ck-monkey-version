package me.earth.earthhack.impl.modules.misc.holdmodules;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.StringSetting;

public class HoldModule2 extends Module {

    public HoldModule2() {
        super("HoldModule2", Category.Misc);
    }
    protected Setting<String> string1 = register(new StringSetting("First", ""));
    protected Setting<String> string2 = register(new StringSetting("Second", ""));

    @Override
    public void onEnable(){
        super.onEnable();
        if(mc.world!=null && mc.player!=null) {
            if(!string1.getValue().isEmpty()) {
                mc.player.sendChatMessage(string1.getValue());
            }
            if(!string2.getValue().isEmpty() ) {
                mc.player.sendChatMessage(string2.getValue());
            }
        }
    }
    @Override
    public void onDisable(){
        super.onEnable();
        if( mc.world!=null && mc.player!=null) {
            if(!string1.getValue().isEmpty()) {
                mc.player.sendChatMessage(string1.getValue());
            }
            if(!string2.getValue().isEmpty() ) {
                mc.player.sendChatMessage(string2.getValue());
            }
        }
    }
}
