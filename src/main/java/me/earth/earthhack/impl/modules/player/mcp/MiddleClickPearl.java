package me.earth.earthhack.impl.modules.player.mcp;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Event;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BindSetting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.event.events.keyboard.ClickMiddleEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.mcf.MCF;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MiddleClickPearl extends Module
{
    private static final ModuleCache<MCF> MCFRIENDS =
            Caches.getModule(MCF.class);

    protected final Setting<Boolean> preferMCF   =
            register(new BooleanSetting("PrioMCF", false));
    protected final Setting<Boolean> midclickOnly =
            register(new BooleanSetting("MidClickOnly", false));
    protected final Setting<Bind> button =
            register(new BindSetting("Button", Bind.none()));
    protected final Setting<Boolean> cancelMCF   =
            register(new BooleanSetting("CancelMCF", true));
    protected final Setting<Boolean> cancelBlock =
            register(new BooleanSetting("CancelBlock", false));
    protected final Setting<Boolean> pickBlock =
            register(new BooleanSetting("PickBlock", false));

    protected Runnable runnable;

    public MiddleClickPearl()
    {
        super("MCP", Category.Player);
        this.listeners.add(new ListenerPickBlock(this));
        this.listeners.add(new ListenerMotion(this));
        this.listeners.add(new ListenerMiddleClick(this));
        this.listeners.add(new ListenerKeyBoard(this));
        SimpleData data = new SimpleData(
            this, "Middle click to throw an Ender Pearl.");
        this.setData(data);
    }

    @Override
    public void onEnable()
    {
        runnable = null;
    }

    @Override
    protected void onDisable()
    {
        runnable = null;
    }

    protected boolean prioritizeMCF()
    {
        return preferMCF.getValue() && MCFRIENDS.isEnabled()
                && mc.objectMouseOver != null
                && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY
                && mc.objectMouseOver.entityHit instanceof EntityPlayer;
    }

    public void onClick(Event event)
    {
        if (mc.player == null || mc.world == null)
        {
            return;
        }

        if (InventoryUtil.findHotbarItem(Items.ENDER_PEARL) == -1)
        {
            return;
        }

        if (!this.prioritizeMCF())
        {
            if (this.cancelBlock.getValue())
            {
                event.setCancelled(true);
            }
        }
        else
        {
            if (this.cancelMCF.getValue())
            {
                if (event instanceof ClickMiddleEvent)
                {
                    ((ClickMiddleEvent) event).setModuleCancelled(true);
                }
                else
                {
                    event.setCancelled(true);
                }
            }
            else
            {
                return;
            }
        }

        this.runnable = () ->
        {
            int slot = InventoryUtil.findHotbarItem(Items.ENDER_PEARL);
            if (slot == -1)
            {
                return;
            }

            Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
            {
                int lastSlot = mc.player.inventory.currentItem;
                InventoryUtil.switchTo(slot);

                mc.playerController.processRightClick(
                    mc.player, mc.world, InventoryUtil.getHand(slot));

                InventoryUtil.switchTo(lastSlot);
            });
        };

        if (Managers.ROTATION.getServerPitch() == mc.player.rotationPitch
            && Managers.ROTATION.getServerYaw() == mc.player.rotationYaw)
        {
            this.runnable.run();
            this.runnable = null;
        }
    }
    protected void ccBypass(){
        int PEARLSLOT = InventoryUtil.findHotbarItem(Items.ENDER_PEARL);
        if (!mc.player.collidedHorizontally || PEARLSLOT == -1 || PEARLSLOT == -2) {
            return;
        }

        if (!mc.player.isSneaking()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.setSneaking(true);
        }
        mc.player.swingArm(EnumHand.MAIN_HAND);
        EnumFacing enumFacing = Method3862(mc.player.getPosition());
        assert enumFacing != null;
    //    mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(mc.player.getPosition(), enumFacing, EnumHand.MAIN_HAND, f, f2, f3));
        try {
            Field field = Minecraft.class.getDeclaredField("rightClickDelayTimer");
            field.set(mc, 4);
        }
        catch (IllegalAccessException | NoSuchFieldException reflectiveOperationException) {
            // empty catch block
        }
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        mc.player.setSneaking(true);
        if (mc.player.itemStackMainHand.getItem().equals(Items.ENDER_PEARL)) {
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        } else {
            int oldSlot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = PEARLSLOT;
            InventoryUtil.syncItem();
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            mc.player.inventory.currentItem = oldSlot;
            InventoryUtil.syncItem();
        }
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));

    }
    public static void Method2822(BlockPos blockPos) {
        IBlockState iBlockState = mc.world.getBlockState(blockPos);
        Block block = iBlockState.getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return;
        }
        EnumFacing enumFacing = Method3862(blockPos);
        if (enumFacing == null) {
            return;
        }
        BlockPos blockPos2 = blockPos.offset(enumFacing);
        EnumFacing enumFacing3 = enumFacing.getOpposite();
        Vec3d vec3d = new Vec3d(blockPos2.add(0.5, 0.5, 0.5));
        Vec3d vec3d2 = vec3d.add(new Vec3d(enumFacing3.getDirectionVec()).scale(0.5));
        if (!mc.player.isSneaking()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.setSneaking(true);
        }
        //Method3852(blockPos2, vec3d2, EnumHand.MAIN_HAND, enumFacing3, true);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        try {
            Field field = Minecraft.class.getDeclaredField("rightClickDelayTimer");
            field.set(mc, 4);
        }
        catch (IllegalAccessException | NoSuchFieldException reflectiveOperationException) {
            // empty catch block
        }
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        mc.player.setSneaking(true);
    }

    public static void Method3852(BlockPos blockPos, Vec3d vec3d, EnumHand enumHand, EnumFacing enumFacing, boolean bl) {
        if (bl) {
            float f = (float)(vec3d.x - (double)blockPos.getX());
            float f2 = (float)(vec3d.y - (double)blockPos.getY());
            float f3 = (float)(vec3d.z - (double)blockPos.getZ());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(blockPos, enumFacing, enumHand, f, f2, f3));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, enumFacing, vec3d, enumHand);
        }
        mc.player.swingArm(EnumHand.MAIN_HAND);
        try {
            Field field = Minecraft.class.getDeclaredField("rightClickDelayTimer");
            field.set(mc, 4);
        }
        catch (IllegalAccessException | NoSuchFieldException reflectiveOperationException) {
            // empty catch block
        }
    }

    public static EnumFacing Method3862(BlockPos blockPos) {
        Iterator<EnumFacing> iterator = Method3864(blockPos).iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        return iterator.next();
    }

    public static List<EnumFacing> Method3864(BlockPos blockPos) {
        ArrayList<EnumFacing> arrayList = new ArrayList<EnumFacing>();
        if (mc.world == null) {
            return arrayList;
        }
        if (blockPos == null) {
            return arrayList;
        }
        for (EnumFacing enumFacing : EnumFacing.values()) {
            BlockPos blockPos2 = blockPos.offset(enumFacing);
            IBlockState iBlockState = mc.world.getBlockState(blockPos2);
            if (!iBlockState.getBlock().canCollideCheck(iBlockState, false) || BlockUtil.isReplaceable(blockPos2)) continue;
            arrayList.add(enumFacing);
        }
        return arrayList;
    }

}
