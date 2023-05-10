package me.earth.earthhack.impl.modules.combat.hitboxdesync;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;

public class HitBoxDesync extends Module {



    protected final Setting<Float> offsetx =
            register(new NumberSetting<>("OffsetX", 0.0f, -2.0f,2.0f ));
    protected final Setting<Float> offsetz =
            register(new NumberSetting<>("OffsetZ", 0.0f, -2.0f,2.0f ));
    public HitBoxDesync() {
        super("HitboxDesync", Category.Combat);
        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if(mc.player == null && mc.world == null) return;

            mc.player.setEntityBoundingBox(new AxisAlignedBB(
                    mc.player.posX - offsetx.getValue(),
                    mc.player.getEntityBoundingBox().minY,
                    mc.player.posZ - offsetz.getValue(),
                    mc.player.posX + offsetx.getValue(),
                    mc.player.getEntityBoundingBox().maxY,
                    mc.player.posZ + offsetz.getValue()

            ));
        }));
    }

}
