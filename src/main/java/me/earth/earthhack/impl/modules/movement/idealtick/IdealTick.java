package me.earth.earthhack.impl.modules.movement.idealtick;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.minecraft.combat.HealthManager;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.Hole;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.SimpleHoleManager;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

import java.util.Arrays;

public class IdealTick extends Module {
    private int teleportID;

    SimpleHoleManager HM = new SimpleHoleManager();

    public IdealTick() {
        super("IdealTick", Category.Movement);
        this.listeners.add(new LambdaListener<>(PacketEvent.Receive.class, e-> {
            if(e.getPacket() instanceof SPacketPlayerPosLook) teleportID = ((SPacketPlayerPosLook) e.getPacket()).teleportId;
        }));
        this.listeners.add(new LambdaListener<>(UpdateEvent.class, event -> {
            if(!isSafe()) return;

            if(EntityUtil.getHealth(mc.player) > health.getValue()) return;

            Hole hole = HM.getHoles().values().stream().filter(h->  h.isValid()
                    && h.getDistanceSq(
                    mc.player.posX,
                    mc.player.posY,
                    mc.player.posZ) <= MathUtil.square(range.getValue())).findAny().orElse(null);
            //Hole hole = Arrays.stream(holecalc).findFirst().get();

            if(hole != null && !HM.isPlayerInHole()) {
                PacketUtil.doPosition(hole.getX(), hole.getY(), hole.getZ(), mc.player.onGround);
                mc.player.connection.sendPacket(new CPacketConfirmTeleport(teleportID++));
            }
        }));
    }


    protected final Setting<Float> health = register(new NumberSetting<>("Health", 8.0f, 0.0f, 36.0f));

    protected final Setting<Float> range = register(new NumberSetting<>("Range", 1.0f, 0.0f, 12.0f));




}
