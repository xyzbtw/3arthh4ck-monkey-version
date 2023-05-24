package me.earth.earthhack.impl.modules.movement.clip;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;

final class ListenerTick extends ModuleListener<Clip, TickEvent>
{
    public ListenerTick(Clip module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (!MovementUtil.noMovementKeys() || !event.isSafe())
        {
            module.disable();
            return;
        }

        final EntityPlayerSP player = mc.player;
        final int tickCount = player.ticksExisted;
        final int delay = module.delay.getValue();
        final boolean canTeleport = mc.world.getCollisionBoxes(player, player.getEntityBoundingBox().grow(0.01, 0, 0.01)).size() < 2;
        final boolean shouldSendPacket = tickCount % delay == 0 && !canTeleport;

        float posX = (float) player.posX;
        float posY = (float) player.posY;
        float posZ = (float) player.posZ;
        double roundedX = MathUtil.roundToClosest(posX, Math.floor(posX) + 0.241, Math.floor(posX) + 0.759);
        double roundedZ = MathUtil.roundToClosest(posZ, Math.floor(posZ) + 0.241, Math.floor(posZ) + 0.759);
        double roundedX2 = MathUtil.roundToClosest(posX, Math.floor(posX) + 0.23, Math.floor(posX) + 0.77);
        double roundedZ2 = MathUtil.roundToClosest(posZ, Math.floor(posZ) + 0.23, Math.floor(posZ) + 0.77);


        if (canTeleport)
        {
            player.setPosition(roundedX, posY, roundedZ);
        }
        else if (shouldSendPacket)
        {
            float deltaX = MathHelper.clamp((float) (roundedX - posX), -0.03f, 0.03f);
            float deltaZ = MathHelper.clamp((float) (roundedZ - posZ), -0.03f, 0.03f);
            player.setPositionAndRotation(posX + deltaX, posY, posZ + deltaZ, player.rotationYaw, player.rotationPitch);
            NetworkUtil.send(new CPacketPlayer.Position(posX + deltaX, posY, posZ + deltaZ, true));
            NetworkUtil.send(new CPacketPlayer.Position(roundedX2, posY, roundedZ2, true));
        }

        if (module.disable.getValue())
        {
            int updates = module.updates.getValue();
            int disableTime = module.disabletime;
            if (disableTime >= updates)
            {
                module.disable();
            }
            module.disabletime++;
        }
    }
}