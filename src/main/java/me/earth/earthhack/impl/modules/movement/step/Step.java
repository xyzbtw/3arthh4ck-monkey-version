package me.earth.earthhack.impl.modules.movement.step;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.anchor.Anchor;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.modules.movement.longjump.LongJump;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.block.BlockSlab;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class Step extends BlockESPModule
{
    protected final Setting<StepESP> esp = registerBefore(
            new EnumSetting<>("ESP", StepESP.None), super.color);

    protected final Setting<StepMode> mode =
        register(new EnumSetting<>("Mode", StepMode.Normal));

    protected final Setting<Float> height =
            register(new NumberSetting<>("Height", 2.0f, 0.6f, 10.0f));
    protected final Setting<Boolean> useTimer =
            register(new BooleanSetting("UseTimer", false));
    protected final Setting<Double> timer =
            register(new NumberSetting<>("Timer", 1.0, 0.1, 2.0));
    protected final Setting<Boolean> entityStep =
            register(new BooleanSetting("EntityStep", true));
    protected final Setting<Boolean> autoOff =
            register(new BooleanSetting("AutoOff", false));
    protected final Setting<Boolean> reverseStep =
            register(new BooleanSetting("ReverseStep", false));
    protected final Setting<Boolean> stopsneak =
            register(new BooleanSetting("StopOnSneak", false));
    protected final Setting<Integer> lagTime =
            register(new NumberSetting<>("LagTime", 0, 0, 250));
    protected final Setting<Double> speed =
            register(new NumberSetting<>("Speed", 4.0, 0.1, 10.0));
    protected final Setting<Double> distance =
            register(new NumberSetting<>("Distance", 3.0, 0.1, 10.0));
    protected final Setting<Boolean> pauseInAir  =
            register(new BooleanSetting("PauseBypassAir", false));
    protected final Setting<Boolean> boost = register(new BooleanSetting("Boost", false));
    protected StopWatch bypasstime = new StopWatch();
    protected StopWatch velocityTimer = new StopWatch();
    double maxVelocity = 0;

    protected final Setting<Boolean> strictLiquid =
            register(new BooleanSetting("StrictLiquid", false));
    protected final Setting<Boolean> gapple =
            register(new BooleanSetting("Mine-Gapple", false));
    public final Setting<Boolean> bypass = register(new BooleanSetting("SpeedBypass", false));
    public final  Setting<Integer> bypassTime = register(new NumberSetting<>("Time", 100, 0, 1000));
    protected Setting<Float> bypassspeed = register(new NumberSetting<>("BypassSpeed", 1.5f, 0f, 5f));
    protected final Setting<Float> crystalFactor    =
            register(new NumberSetting<>("ExplosionFactor", 1.0f, 0.0f, 5.0f));
    protected final Setting<Float> bowFactor    =
            register(new NumberSetting<>("VelocityFactor", 1.0f, 0.0f, 5.0f));


    protected final StopWatch breakTimer = new StopWatch();
    protected AxisAlignedBB bb;
    protected boolean stepping;
    protected double[] offsets;
    protected double currHeight;
    protected int index;
    protected boolean jumped;
    protected boolean waitForOnGround;
    protected int packets;

    protected double x;
    protected double y;
    protected double z;

    public Step()
    {
        super("Step", Category.Movement);
        this.listeners.add(new ListenerStep(this));
        this.listeners.add(new ListenerDestroy(this));
        this.listeners.add(new ListenerBreak(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerPreMotionUpdate(this));
        this.listeners.add(new LambdaListener<>(PacketEvent.Receive.class, event-> {
                if (event.getPacket() instanceof SPacketExplosion) {
                    SPacketExplosion velocity = (SPacketExplosion) event.getPacket();

                    maxVelocity = Math.sqrt(velocity.getMotionX() * velocity.getMotionX() + velocity.getMotionZ() * velocity.getMotionZ());


                    maxVelocity *= crystalFactor.getValue();

                    velocityTimer.reset();
                    event.setCancelled(true);
                }
            if (event.getPacket() instanceof SPacketEntityVelocity) {
                SPacketEntityVelocity velocity = (SPacketEntityVelocity) event.getPacket();

                if (velocity.getEntityID() != mc.player.entityId) {
                    event.setCancelled(true);
                    return;
                }


                maxVelocity = Math.sqrt(velocity.getMotionX() * velocity.getMotionX() + velocity.getMotionZ() * velocity.getMotionZ()) / 8000.0;


                maxVelocity *= bowFactor.getValue();

                velocityTimer.reset();
                event.setCancelled(true);
            }


        }));


        this.listeners.add(new LambdaListener<>(MoveEvent.class, e-> {
            if (!bypasstime.passed(bypassTime.getValue()) && bypass.getValue() && mode.getValue() ==StepMode.Vanilla ) {
               strafe(e, bypassspeed.getValue());
            }
        }));
        register(new BooleanSetting("Compatibility", false));
        DisablingModule.makeDisablingModule(this);
        super.color.setValue(new Color(0, 255, 255, 76));
        super.outline.setValue(new Color(0, 255, 255));
        mode.addObserver(e -> mc.addScheduledTask(this::reset));
        this.setData(new StepData(this));
    }
    public void strafe(MoveEvent event, float speed1) {
        //if (Anchor.pulling) return;
        if(pauseInAir.getValue() && !mc.player.onGround) return;
        double speed = MovementUtil.getSpeedforever(true, speed1);

        if (boost.getValue() && velocityTimer.passed(75) && maxVelocity > 0) {
            speed = Math.max(speed, maxVelocity);
        }

        float moveForward = mc.player.movementInput.moveForward;
        float moveStrafe = mc.player.movementInput.moveStrafe;
        float rotationYaw = mc.player.prevRotationYaw
                + (mc.player.rotationYaw - mc.player.prevRotationYaw)
                * mc.getRenderPartialTicks();

        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                rotationYaw += ((moveForward > 0.0f) ? -45 : 45);
            } else if (moveStrafe < 0.0f) {
                rotationYaw += ((moveForward > 0.0f) ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        double v = moveForward * speed * -Math.sin(Math.toRadians(rotationYaw))
                + moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double v1 = moveForward * speed * Math.cos(Math.toRadians(rotationYaw))
                - moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
        event.setX(v);
        event.setZ(v1);

    }
    @Override
    public String getDisplayInfo() {
        return mode.getValue().toString();
    }

    @Override
    protected void onEnable() {
        reset();
    }

    @Override
    protected void onDisable()
    {
        if (mc.player != null)
        {
            if (mc.player.getRidingEntity() != null)
            {
                mc.player.getRidingEntity().stepHeight = 1.0F;
            }

            mc.player.stepHeight = 0.6f;
        }

        Managers.TIMER.reset();
        reset();
    }

    public void onBreak()
    {
        breakTimer.reset();
    }

    protected boolean canStep()
    {
        return !mc.player.isInWater()
                && mc.player.onGround
                && !mc.player.isOnLadder()
                && !mc.player.movementInput.jump
                && mc.player.collidedVertically
                && mc.player.fallDistance < 0.1;
    }

    protected void reset() {
        Managers.TIMER.reset();
        stepping = false;
        bb = null;
        offsets = null;
        if (bypass.getValue()) {
            bypasstime.setTime(System.currentTimeMillis() + System.currentTimeMillis());
            velocityTimer.reset();
            maxVelocity = 0;
        }
        index = 0;
    }
    protected double getNearestBlockBelow()
    {
        for (double y = mc.player.posY; y > 0; y -= 0.001) {
            if (mc.world.getBlockState(new BlockPos(mc.player.posX, y, mc.player.posZ)).getBlock().getDefaultState().getCollisionBoundingBox(mc.world, new BlockPos(0, 0, 0)) != null) {
                if (mc.world.getBlockState(new BlockPos(mc.player.posX, y, mc.player.posZ)).getBlock() instanceof BlockSlab) {
                    return -1;
                }
                return y;
            }
        }
        return -1;
    }

}
