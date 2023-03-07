/*package me.earth.earthhack.impl.modules.player.holesnap;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.mixins.util.ITimer;
import me.earth.earthhack.impl.event.events.network.HSPacketEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.minecraft.timer.TimerManager;
import me.earth.earthhack.impl.util.math.Timer;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HoleSnap extends Module {
    protected final Setting<Float> range =
            register(new NumberSetting<>("Range", 4.5f, 0.1f, 12.0f));
    protected final Setting<Boolean> step =
            register(new BooleanSetting("Step", true));
    protected final Setting<Float> factor =
            register(new NumberSetting<>("Factor", 2.5f, 1.0f, 15.0f));
    Timer timer = new Timer();
    HoleUtil.Hole holes;

    public HoleSnap() {
        super("HoleSnap", Category.Player);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        this.timer.reset();
        this.holes = null;
    }

    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        this.timer.reset();
        this.holes = null;
        if (this.step.getValue()) {
            HoleSnap.mc.player.stepHeight = 0.6f;
        }
        if ((TimerManager.mc.getTickLength()) != 50.0f) {
            Managers.TIMER.reset();
        }
    }

    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        this.holes = RotationUtil.getTargetHoleVec3D(this.range.getValue().floatValue());
        RotationUtil.getRotations(this.range.getValue().floatValue());
        if (this.holes == null || HoleUtil.isHole(RotationUtil.getPlayerPos())) {
            this.disable();
            return;
        }
        if (this.timer.passed(500L) && MovementUtil.anyMovementKeys()) {
            this.disable();
            return;
        }
        if (HoleSnap.mc.world.getBlockState(this.holes.pos1).getBlock() == Blocks.AIR) {
            if (this.step.getValue()) {
                MovementUtil.step(2.0f);
            }
        } else {
            this.disable();
            return;
        }
        Vec3d playerPos = HoleSnap.mc.player.getPositionVector();
        Vec3d targetPos = new Vec3d((double)this.holes.pos1.getX() + 0.5, HoleSnap.mc.player.posY, (double)this.holes.pos1.getZ() + 0.5);
        double yawRad = Math.toRadians(RotationUtil.getRotationTo((Vec3d)playerPos, (Vec3d)targetPos).x);
        double dist = playerPos.distanceTo(targetPos);
        double speed = HoleSnap.mc.player.onGround ? -Math.min(0.2805, dist / 2.0) : -MovementUtil.getSpeed() + 0.02;
        Cascade.timerManager.set(this.factor.getValue().floatValue());
        HoleSnap.mc.player.motionX = -Math.sin(yawRad) * speed;
        HoleSnap.mc.player.motionZ = Math.cos(yawRad) * speed;
    }

    @SubscribeEvent
    public void onPacketReceive(HSPacketEvent.Receive e) {
        if (e.getPacket() instanceof SPacketPlayerPosLook && this.isEnabled()) {
            this.disable();
            return;
        }
    }
}

 */




