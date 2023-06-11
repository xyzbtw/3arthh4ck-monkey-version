package me.earth.earthhack.impl.modules.combat.pearlclip;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PearlClip extends Module {

    public PearlClip() {
        super("PearlClip", Category.Combat);
    }


    @Override
    public void onEnable() {
        int PEARLSLOT = InventoryUtil.findItem(Items.ENDER_PEARL, false);
        if (!mc.player.collidedHorizontally || PEARLSLOT == -1 || PEARLSLOT == -2) {
            disable();
            return;
        }
        Method2822(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ));
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, 84, mc.player.onGround));
        if (mc.player.getHeldItemOffhand().getItem().equals(Items.ENDER_PEARL)) {
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        } else {
            int oldSlot = mc.player.inventory.currentItem;
            InventoryUtil.switchSilent(PEARLSLOT, PEARLSLOT, oldSlot, switchh.getValue());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            InventoryUtil.switchSilent(oldSlot, PEARLSLOT, oldSlot, switchh.getValue());
        }
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
        disable();
    }

    public static void Method2822(BlockPos blockPos) {
        IBlockState iBlockState = mc.world.getBlockState(blockPos);
        Block block = iBlockState.getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return;
        }
        EnumFacing enumFacing = Method3862(blockPos);
        if (enumFacing == null) return;
        BlockPos blockPos2 = blockPos.offset(enumFacing);
        EnumFacing enumFacing3 = enumFacing.getOpposite();
        Vec3d vec3d = new Vec3d(blockPos2).add(0.5, 0.5, 0.5);
        Vec3d vec3d2 = vec3d.add(new Vec3d(enumFacing3.getDirectionVec()).scale(0.5));
        if (!mc.player.isSneaking()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.setSneaking(true);
        }
        Method3852(blockPos2, vec3d2, EnumHand.MAIN_HAND, enumFacing3, true);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        //-1251036016
        mc.rightClickDelayTimer = 4;
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        mc.player.setSneaking(true);
    }

    public static void Method3852(BlockPos blockPos, Vec3d vec3d, EnumHand enumHand, EnumFacing enumFacing, boolean bl) {
        if (bl) {
            float f = (float) (vec3d.x - (double) blockPos.getX());
            float f2 = (float) (vec3d.y - (double) blockPos.getY());
            float f3 = (float) (vec3d.z - (double) blockPos.getZ());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(blockPos, enumFacing, enumHand, f, f2, f3));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, enumFacing, vec3d, enumHand);
        }
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
    }

    public static EnumFacing Method3862(BlockPos blockPos) {
        Iterator<EnumFacing> iterator = Method3864(blockPos).iterator();
        if (!iterator.hasNext()) return null;
        return iterator.next();
    }

    public static List<EnumFacing> Method3864(BlockPos blockPos) {
        ArrayList<EnumFacing> arrayList = new ArrayList<>();
        if (mc.world == null) return arrayList;
        if (blockPos == null) {
            return arrayList;
        }
        EnumFacing[] enumFacingArray = EnumFacing.values();
        int n = enumFacingArray.length;
        int n2 = 0;
        while (n2 < n) {
            EnumFacing enumFacing = enumFacingArray[n2];
            BlockPos blockPos2 = blockPos.offset(enumFacing);
            IBlockState iBlockState = mc.world.getBlockState(blockPos2);
            if (iBlockState.getBlock().canCollideCheck(iBlockState, false) && !iBlockState.getMaterial().isReplaceable()) {
                arrayList.add(enumFacing);
            }
            ++n2;
        }
        return arrayList;
    }
}
