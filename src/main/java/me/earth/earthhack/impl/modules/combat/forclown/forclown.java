package me.earth.earthhack.impl.modules.combat.forclown;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class forclown extends Module {

    protected final Setting<Boolean> extend =
            register(new BooleanSetting("Extend", true));
    protected final Setting<Boolean> face =
            register(new BooleanSetting("Face", true));
    protected final Setting<Boolean> packet =
            register(new BooleanSetting("Packet", true));



    public forclown() {
        super("Blocker", Category.Combat);
        this.listeners.add(new ListenerReceive(this));
    }



    protected void placeBlock(BlockPos pos){
        if (!mc.world.isAirBlock(pos)) return;

        int oldSlot = InventoryUtil.getServerItem();

        int obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        int eChestSlot = InventoryUtil.findHotbarBlock(Blocks.ENDER_CHEST);

        if (obbySlot == -1 && eChestSlot == 1) return;

        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity instanceof EntityEnderCrystal) {
                NetworkUtil.send(new CPacketUseEntity(entity));
                NetworkUtil.send(new CPacketAnimation(EnumHand.MAIN_HAND));
            }
        }

        CooldownBypass.None.switchTo(obbySlot == -1 ? eChestSlot : obbySlot);

        mc.playerController.updateController();

        Managers.INTERACTION.placeBlock(pos, packet.getValue(), true);

        if (mc.player.inventory.currentItem != oldSlot) {
            CooldownBypass.None.switchTo(oldSlot);
            mc.playerController.updateController();
        }

        CooldownBypass.None.switchTo(oldSlot);
    }

}
