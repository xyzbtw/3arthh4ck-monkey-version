package me.earth.earthhack.impl.modules.misc.holdmodules;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;

public class HoldModule8 extends Module {

    public HoldModule8() {
        super("HoldModule8", Category.Misc);
        this.listeners.add(new LambdaListener<>(TickEvent.class, e ->{
            if(!e.isSafe() || !disableonmove.getValue()) return;
            if(MovementUtil.isMoving()){
                this.disable();
            }
        }));
    }
    protected Setting<String> string1 = register(new StringSetting("First", ""));
    protected Setting<String> string2 = register(new StringSetting("Second", ""));
    protected Setting<Boolean> disableonmove = register(new BooleanSetting("DisableOnMove", false));


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
