package me.earth.earthhack.impl.modules.combat.holefiller;

import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.ducks.entity.IEntityPlayer;
import me.earth.earthhack.impl.event.events.misc.UpdateEntitiesEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.MotionTracker;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class ExtrapolationHelper extends SubscriberImpl implements Globals {

    public ExtrapolationHelper(HoleFiller module) {
        this.listeners.add(new LambdaListener<>(UpdateEntitiesEvent.class, e -> {
            for (EntityPlayer player : mc.world.playerEntities) {
                MotionTracker tracker = ((IEntityPlayer) player).getMotionTracker();
                if (EntityUtil.isDead(player)
                        || RotationUtil.getRotationPlayer().getDistanceSq(player) > 400
                        && player.equals(RotationUtil.getRotationPlayer())) {
                    if (tracker != null) {
                        tracker.active = false;
                    }

                    continue;
                }

                if (tracker == null && module.extrapol.getValue() != 0) {
                    tracker = new MotionTracker(mc.world, player);
                    ((IEntityPlayer) player).setMotionTracker(tracker);
                }

                updateTracker(tracker, module.extrapol.getValue());
            }
        }));
    }

    private void updateTracker(MotionTracker tracker, int ticks) {
        if (tracker == null) {
            return;
        }

        tracker.active = false;
        tracker.copyLocationAndAnglesFrom(tracker.tracked);
        tracker.gravity = false;
        for (tracker.ticks = 0; tracker.ticks < ticks; tracker.ticks++) {
            tracker.updateFromTrackedEntity();
        }

        tracker.active = true;
    }

    public MotionTracker getTrackerFromEntity(Entity player) {
        return ((IEntityPlayer) player).getMotionTracker();
    }


}
