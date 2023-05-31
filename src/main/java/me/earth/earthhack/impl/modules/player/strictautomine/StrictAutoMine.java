package me.earth.earthhack.impl.modules.player.strictautomine;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.otherplayers.IgnoreSelfClosest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;


public class StrictAutoMine extends Module {

    public StrictAutoMine() {
        super("StrictAutoMine", Category.Player);
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerMultiBlockChange(this));
        this.listeners.add(new ListenerUpdate(this));

    }
    protected Vec3i[] offsets = new Vec3i[]{
            new Vec3i(1,0,0),
            new Vec3i(-1,0,0),
            new Vec3i(0,0,1),
            new Vec3i(0,0,-1)
    };

    protected final Setting<Double> range =
            register(new NumberSetting<>("Range", 6.0d, 0.1d, 100.0d));
    protected final Setting<Boolean> raytrace =
            register(new BooleanSetting("Raytrace", false));

    protected BlockPos current;
    protected boolean hitting =false;
    protected float[] rotations;
    protected EnumFacing facing;
    protected List<BlockPos> getSurroundBlocks(final EntityPlayer player) {
        if (player == null) {
            return null;
        }
        final List<BlockPos> positions = new ArrayList<BlockPos>();
        for (final EnumFacing direction : EnumFacing.values()) {
            if (direction != EnumFacing.UP) {
                if (direction != EnumFacing.DOWN) {
                    final BlockPos pos = IgnoreSelfClosest.GetClosestIgnoreFriends(range.getValue()).getPosition().offset(direction);
                    if (mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN && canCityBlock(pos, direction)) {
                        positions.add(pos);
                    }
                }
            }
        }
        return positions;
    }
    protected boolean isBurrow(final Entity target) {
        final BlockPos blockPos = new BlockPos(target.posX, target.posY, target.posZ);
        return mc.world.getBlockState(blockPos).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(blockPos).getBlock().equals(Blocks.ENDER_CHEST);
    }
    protected BlockPos getBurrowBlock(final EntityPlayer player)
    {
        if (player == null)
        {
            return null;
        }
        final BlockPos blockPos = new BlockPos(player.posX, player.posY, player.posZ);
        if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(blockPos).getBlock().equals(Blocks.ENDER_CHEST))
        {
            return blockPos;
        }
        else
        {
            return null;
        }
    }
    protected void mine(final BlockPos blockPos) {
        mc.playerController.onPlayerDamageBlock(blockPos, EnumFacing.UP);
        mc.player.swingArm(EnumHand.MAIN_HAND);

        current = blockPos;
    }
    protected BlockPos getCityBlockSurround(final EntityPlayer player) {
        final List<BlockPos> posList = getSurroundBlocks(player);
        posList.sort(Comparator.comparingDouble((ToDoubleFunction<? super BlockPos>) MathUtil::distanceTo));
        return posList.isEmpty() ? null : posList.get(0);
    }
    protected boolean canCityBlock(final BlockPos blockPos, final EnumFacing direction) {
        return mc.world.getBlockState(blockPos.up()).getBlock() == Blocks.AIR || (mc.world.getBlockState(blockPos.offset(direction)).getBlock() == Blocks.AIR && mc.world.getBlockState(blockPos.offset(direction).up()).getBlock() == Blocks.AIR && (mc.world.getBlockState(blockPos.offset(direction).down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockPos.offset(direction).down()).getBlock() == Blocks.BEDROCK));
    }
}
