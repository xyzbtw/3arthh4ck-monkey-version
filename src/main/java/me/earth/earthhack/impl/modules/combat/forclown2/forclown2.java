package me.earth.earthhack.impl.modules.combat.forclown2;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.blocks.InteractionUtil;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class forclown2 extends ObbyListenerModule<ListenerObsidian> {
    protected EntityPlayer target;
    protected final ModuleCache<Speedmine> speedmine = Caches.getModule(Speedmine.class);

    protected final Setting<Boolean> helping =
            register(new BooleanSetting("Helping", false));
    protected final Setting<Float> range =
            register(new NumberSetting<>("Range", 6.0f, 0.0f, 10.0f));


    public forclown2() {
        super("AntiCev", Category.Combat);
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerUpdate(this));
        this.setData(new AntiCevData(this));
    }


   /* protected void placeBlock(BlockPos pos){
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

    }

    */
   @Override
   protected boolean shouldHelp(EnumFacing facing, BlockPos pos)
   {
       return super.shouldHelp(facing, pos) // ??????
               && helping.getValue();
   }

    protected void scanAndPlace(BlockPos pos){
        if(mc.world==null)return;
        if(mc.player==null)return;
        if(mc.currentScreen instanceof GuiConnecting)return;
        target = EntityUtil.getClosestEnemy();
        if (pos == this.speedmine.get().getPos()) return;
        if(target == null ||pos.getDistance(target.getPosition().getX(), target.getPosition().getY(), target.getPosition().getZ()) >= this.range.getValue()) return;

        me.earth.earthhack.impl.modules.combat.forclown2.ListenerUpdate.scheduledPlacements.add(pos);


    }
    @Override
    public boolean execute()
    {
        return super.execute();
    }

    @Override
    protected ListenerObsidian createListener() {
        return new ListenerObsidian(this);
    }
}
