package me.earth.earthhack.impl.modules.movement.smartblocklag;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.ChatUtil;

public class SmartBlockLag extends Module {
    public SmartBlockLag() {
        super("SmartBlockLag", Category.Movement);
        this.listeners.add(new ListenerUpdate(this));
    }
    protected final Setting<Float> smartRange =
            register(new NumberSetting<>("Range", 3.0f, 0.0f, 10.0f));
    protected  final Setting<Boolean> turnoff =
            register(new BooleanSetting("AutoOff", false));
    protected  final Setting<Boolean> holeonly =
            register(new BooleanSetting("OnlyInHole", false));
    protected final Setting<Integer> delay =
            register(new NumberSetting<>("Delay", 100, 0, 1000));

    public void onEnable(){
        if(mc.isSingleplayer()){
            Managers.CHAT.sendDeleteMessage("Not a multiplayer world retard", getName(), ChatIDs.MODULE);
            this.disable();
        }
    }
}
