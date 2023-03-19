package me.earth.earthhack.impl.modules.combat.forclown;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.util.blocks.InteractionUtil;
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
import net.minecraft.util.text.TextComponentString;

public class forclown extends Module {

    protected final Setting<Boolean> extend =
            register(new BooleanSetting("Extend", true));
    protected final Setting<Boolean> face =
            register(new BooleanSetting("Face", true));
    protected final Setting<Boolean> packet =
            register(new BooleanSetting("Packet", true));
    //protected final Setting<Boolean> swing =
            //register(new BooleanSetting("Swing", false));
    protected final Setting<Boolean> hole =
            register(new BooleanSetting("HoleCheck", true));
    protected final Setting<Boolean> destroyEvent =
            register(new BooleanSetting("DestroyEvent", true));
    protected final Setting<Boolean> debug =
            register(new BooleanSetting("Debug", false));




    public forclown() {
        super("Blocker", Category.Combat);
        this.listeners.add(new ListenerReceive(this));
        this.listeners.add(new ListenerBlockDestroy(this));
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

        InteractionUtil.placeBlock(pos, packet.getValue(), true);

        CooldownBypass.None.switchTo(oldSlot);

        if(debug.getValue()) mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(ChatFormatting.AQUA+ "placed at " + pos));
    }

}
