package me.earth.earthhack.impl.modules.client.keybindlist;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;

import java.awt.*;

public class keybindthing extends Module {


    public keybindthing() {
        super("KeyBindList", Category.Client);
    }

    protected final Setting<Color> color           =
            register(new ColorSetting("Color", new Color(255, 255, 255, 255)));

    protected final Setting<Color> background           =
            register(new ColorSetting("BackgroundColor", new Color(255, 255, 255, 255)));
    protected final Setting<Boolean> anim =
            register(new BooleanSetting("Animations", true));
    protected final Setting<Integer> offset =
            register(new NumberSetting<>("Offset", 2, 0, 6));
    protected final Setting<Boolean> headerAnim =
            register(new BooleanSetting("HeaderAnim", true));
    protected final Setting<Integer> x =
            register(new NumberSetting<>("X", 2, 0, 1080));
    protected final Setting<Integer> y =
            register(new NumberSetting<>("Y", 3, 0, 720));
    protected float percent;

    @Override
    public void onEnable(){
        super.onEnable();

    }

    private static class Bind {

        Module module;
        float percent;

        public Bind(Module module) {
            this.module = module;
            this.percent = 0;
        }

    }



}
