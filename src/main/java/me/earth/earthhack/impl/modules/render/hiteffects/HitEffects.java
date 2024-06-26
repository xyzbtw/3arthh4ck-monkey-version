package me.earth.earthhack.impl.modules.render.hiteffects;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Random;

public class HitEffects extends Module {
    public HitEffects(){
        super("HitEffects", Category.Render);
        this.listeners.add(new ListenerDamage(this));
    }

    Random rnd = new Random();
    String[] heroFxText = {"kaboom", "wham", "zap", "boom", "whack", "smash", "knockout"};
    Color[] heroFxColor = {Color.RED, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.ORANGE};
    protected EntityPlayer target;
    protected final Setting<Boolean> lightning =
            register(new BooleanSetting("Lightning", true));
    protected final Setting<Boolean> screenShader =
            register(new BooleanSetting("ScreenShader", false));
    protected final Setting<Integer> screenShaderLength =
            register(new NumberSetting<>("ShaderLength", 2,1,5));
    protected final Setting<Boolean> superheroFx =
            register(new BooleanSetting("SuperheroFX", false));
    protected final Setting<Float> superheroFadeTime =
            register(new NumberSetting<>("FadeTime", 1.5f,0.1f,5.0f));
    protected final Setting<Boolean> onlyOnKill =
            register(new BooleanSetting("OnlyKills", false));
    protected final Setting<Boolean> onlyTargets =
            register(new BooleanSetting("OnlyTargets", false));
    protected final Setting<Float> scale =
            register(new NumberSetting<>("Scale", 1.0f, 0.0f, 10.0f));

}