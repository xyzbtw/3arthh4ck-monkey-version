package me.earth.earthhack.impl.modules.combat.fastprojectiles;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.combat.fastprojectiles.FastProjectile;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;

final class ListenerPlayerTryUseItem extends ModuleListener<FastProjectile, PacketEvent.Send<CPacketPlayerTryUseItem>>
{
    public ListenerPlayerTryUseItem(FastProjectile module)
    {
        super(module, PacketEvent.Send.class, CPacketPlayerTryUseItem.class);
    }

    @Override
    public void invoke(PacketEvent.Send<CPacketPlayerTryUseItem> event)
    {
        if (!shouldSprint()) return;

        PacketUtil.sendAction(CPacketEntityAction.Action.START_SPRINTING);

        double[] dir = MovementUtil.strafe(0.001);
        double posX = mc.player.posX;
        double posY = mc.player.posY;
        double posZ = mc.player.posZ;
        float rotationYaw = mc.player.rotationYaw;
        float rotationPitch = mc.player.rotationPitch;

        boolean moveValue = module.move.getValue();
        int intervalValue = module.interval.getValue();

        double moveX = moveValue ? dir[0] : 0;
        double moveZ = moveValue ? dir[1] : 0;

        double posYOffset1 = 1.3E-13;
        double posYOffset2 = 2.7E-13;

        for (int i = 0; i < intervalValue; i++)
        {
            posX += moveX;
            posZ += moveZ;

            PacketUtil.doPosRotNoEvent(posX, posY + posYOffset1, posZ, rotationYaw, rotationPitch, true); // onground true
            PacketUtil.doPosRotNoEvent(posX, posY + posYOffset2, posZ, rotationYaw, rotationPitch, false); // onground false, jump
        }
    }

    private boolean shouldSprint()
    {
        return (module.eggs.getValue() && InventoryUtil.isHolding(Items.EGG))
                || (module.snowballs.getValue() && InventoryUtil.isHolding(Items.SNOWBALL))
                || (module.pearls.getValue() && InventoryUtil.isHolding(Items.ENDER_PEARL));
    }
}