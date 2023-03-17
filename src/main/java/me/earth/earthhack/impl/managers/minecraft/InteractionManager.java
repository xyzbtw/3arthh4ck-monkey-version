package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.network.ServerUtil;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

import static me.earth.earthhack.api.util.interfaces.Globals.mc;

public class InteractionManager {
    private final StopWatch attackTimer = new StopWatch();

    //Block placements

    public void placeBlock(BlockPos pos, boolean packet, boolean attackCrystal, boolean ignoreEntities) {

        if (mc.world == null || mc.player == null) return;

        if (BlockUtil.isReplaceable(pos)) {



            Optional<ClickLocation> posCL = getClickLocation(pos, ignoreEntities, false, attackCrystal);

            if (posCL.isPresent()) {

                BlockPos currentPos = posCL.get().neighbour;
                EnumFacing currentFace = posCL.get().opposite;

                boolean shouldSneak = shouldShiftClick(currentPos);

                if (shouldSneak) {
                    NetworkUtil.send(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }

                Vec3d hitVec = new Vec3d(currentPos)
                        .add(0.5, 0.5, 0.5)
                        .add(new Vec3d(currentFace.getDirectionVec()).scale(0.5));


                if (packet) {
                    Vec3d extendedVec = new Vec3d(currentPos)
                            .add(0.5, 0.5, 0.5);

                    float x = (float) (extendedVec.x - currentPos.getX());
                    float y = (float) (extendedVec.y - currentPos.getY());
                    float z = (float) (extendedVec.z - currentPos.getZ());

                    NetworkUtil.send(new CPacketPlayerTryUseItemOnBlock(currentPos, currentFace, EnumHand.MAIN_HAND, x, y, z));

                } else {
                    mc.playerController.processRightClickBlock(mc.player, mc.world, currentPos, currentFace, hitVec, EnumHand.MAIN_HAND);
                }

                NetworkUtil.send(new CPacketAnimation(EnumHand.MAIN_HAND));

                if (shouldSneak) {
                    NetworkUtil.send(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }
            }
        }
    }

    public void placeBlock(BlockPos pos, boolean packet, boolean attackCrystal) {
        placeBlock(pos, packet, attackCrystal, false);
    }

    //Getters & variable methods

    public static class ClickLocation {
        public final BlockPos neighbour;
        public final EnumFacing opposite;

        public ClickLocation(BlockPos neighbour, EnumFacing opposite) {
            this.neighbour = neighbour;
            this.opposite = opposite;
        }
    }

    public Optional<ClickLocation> getClickLocation(BlockPos pos, boolean ignoreEntities, boolean noPistons, boolean onlyCrystals) {
        Block block = mc.world.getBlockState(pos).getBlock();

        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return Optional.empty();
        }

        if (!ignoreEntities) {
            for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
                if (onlyCrystals && entity instanceof EntityEnderCrystal) continue;
                if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb) && !(entity instanceof EntityArrow)) {
                    return Optional.empty();
                }
            }
        }

        EnumFacing side = null;

        for (EnumFacing blockSide : EnumFacing.values()) {
            BlockPos sidePos = pos.offset(blockSide);
            if (noPistons) {
                if (mc.world.getBlockState(sidePos).getBlock() == Blocks.PISTON) continue;
            }
            if (!mc.world.getBlockState(sidePos).getBlock().canCollideCheck(mc.world.getBlockState(sidePos), false)) {
                continue;
            }
            IBlockState blockState = mc.world.getBlockState(sidePos);
            if (!blockState.getMaterial().isReplaceable()) {
                side = blockSide;
                break;
            }
        }
        if (side == null) {
            return Optional.empty();
        }

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
            return Optional.empty();
        }

        return Optional.of(new ClickLocation(neighbour, opposite));
    }

    public boolean shouldShiftClick(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();

        TileEntity tileEntity = null;

        for (TileEntity entity : mc.world.loadedTileEntityList) {
            if (!entity.getPos().equals(pos)) continue;
            tileEntity = entity;
            break;
        }
        return tileEntity != null || block instanceof BlockBed || block instanceof BlockContainer || block instanceof BlockDoor || block instanceof BlockTrapDoor || block instanceof BlockFenceGate || block instanceof BlockButton || block instanceof BlockAnvil || block instanceof BlockWorkbench || block instanceof BlockCake || block instanceof BlockRedstoneDiode;
    }
}
