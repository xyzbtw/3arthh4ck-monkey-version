package me.earth.earthhack.impl.modules.player.foreverspeedmine;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

public class ForeverSpeedMine extends Module {

    public ForeverSpeedMine() {
        super("SpeedMineForever", Category.Player);
        this.listeners.add(new ListenerBlock(this));
        this.listeners.add(new ListenerBlockReset(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerRightClick(this));
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new SendPacketEvent(this));

    }
    public Setting<CooldownBypass> silent = register(new EnumSetting<>("Silent", CooldownBypass.None));
    public Setting<Boolean> strict = register(new BooleanSetting("Strict", true));
    public Setting<Boolean> reBreak = register(new BooleanSetting("ReBreak", true));
    public Setting<Boolean> strictReBreak = register(new BooleanSetting("StrictReBreak", false));
    public Setting<Boolean> noReset = register(new BooleanSetting("NoReset", true));
    public Setting<Boolean> resetOnPlace =register (new BooleanSetting("ResetOnPlace", false));
    public Setting<Boolean> abort = register(new BooleanSetting("Abort", false));
    public Setting<Boolean> reset = register(new BooleanSetting("Reset", true));

    public Setting<Integer> range =register (new NumberSetting<>("Range", 6, 1, 30));
    public Setting<Float> speed = register(new NumberSetting<>("Speed", 0.8f, 0.1f, 2f));
    public Setting<Integer> spam = register(new NumberSetting<>("Packet Spam", 1, 1, 10));
    public Setting<RenderMode> renderMode =register (new EnumSetting<>("Render Mode", RenderMode.Fade));
    public Setting<Boolean> showProgress = register(new BooleanSetting("Show Progress", false));
    public Setting<Color> color = register(new ColorSetting("StartColor", new Color(255, 0, 0, 75)));
    public Setting<Color> readyColor = register(new ColorSetting("EndColor", new Color(0, 255, 0, 75)));
    @Override
    public void onEnable() {
        super.onEnable();
        rebreakCount = 0;
        currentPos = null;
        mineDamage = 0;
        isPlaced = false;
    }
    public boolean swap = false,
            checked,
            strictCheck;
    public enum RenderMode {
        None,
        Rise,
        Fade,
        Expand
    }
    void swapTo() {
        int pickSlot = InventoryUtil.findInHotbar(item-> item.getItem() instanceof ItemSword);
        int lastSlot = mc.player.inventory.currentItem;
        silent.getValue().switchTo(pickSlot);
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos, currentFace));
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, currentPos, currentFace));
        resetProgress(reset.getValue());
        silent.getValue().switchBack(lastSlot, pickSlot);
        if (!reBreak.getValue()) currentPos = null;
    }
    int attempts = reBreak.getValue() ? 1 : 0;
    protected BlockPos currentPos;
    protected EnumFacing currentFace;
    boolean doSyns = false;
    protected long start;

    protected int old,
            delay,
            rebreakCount;
    float mineDamage = 0;
    boolean isPlaced = false;
    boolean isNeedReset = false;
    IBlockState currentState;
    float currentHardness;
    boolean checkPos(BlockPos pos) {
        return (currentPos.equals(pos.up()) || currentPos.equals(pos.down())
                || currentPos.equals(pos.west()) || currentPos.equals(pos.east())
                || currentPos.equals(pos.north()) || currentPos.equals(pos.west()));
    }

    boolean checkCurrentPos() {
        return currentPos != null && mc.world.getBlockState(currentPos).getBlock() != Blocks.AIR;
    }
    CalcUtil calcUtil = new CalcUtil();
    public void doSyncAutoCrystal() {
        if (findTarget() == null) return;
        EntityPlayer target = findTarget();
        calcUtil.addAir(currentPos);
        double damage = DamageUtil.calculate(currentPos, target);
        if(damage > Caches.getModule(AutoCrystal.class).get().minDamage.getValue()) doPlace();
        doSyns = false;
    }

    public void doPlace() {
        EnumFacing facing = BlockUtil.getFirstFacing(currentPos);
        doSyns = true;
        placeCrystal();

    }

    EntityPlayer findTarget() {
        EntityPlayer target = null;
//        for (EntityPlayer temp :  mc.world.playerEntities) {
//            targetManager.addTarget(temp);
//        }
        for (EntityPlayer possibleTarget : mc.world.playerEntities) {
            double distancePos = currentPos.getDistance((int) possibleTarget.posX, (int) possibleTarget.posY, (int) possibleTarget.posZ);
            if (distancePos < 4 && !Managers.FRIENDS.contains(possibleTarget.getName()) && mc.player != possibleTarget) {
                target = possibleTarget;
                //Managers.TARGET.setAutoCrystal(possibleTarget);
                break;
            }
        }
        return target;
    }

    public void placeCrystal() {
        int oldslot = mc.player.inventory.currentItem;
        int crystal = InventoryUtil.findInHotbar(itemStack -> itemStack.getItem() instanceof ItemEndCrystal);
        if (crystal != mc.player.inventory.currentItem || !InventoryUtil.isHolding(Items.END_CRYSTAL, EnumHand.OFF_HAND))
            InventoryUtil.setCurrentItem(crystal);
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(currentPos, EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 1.0f, 0.5f));
//        InventoryUtil.setCurrentItem(oldslot);
    }


    public boolean canBlockBeBroken(final BlockPos pos) {
        final IBlockState blockState = mc.world.getBlockState(pos);
        boolean isPos = currentPos == null || !currentPos.equals(pos);
        return (blockState.getBlockHardness(mc.world, pos) >= 0 && mineDamage == 0) || (blockState.getBlockHardness(mc.world, pos) >= 0 && isPos);
    }

    void resetProgress(boolean reset) {
        resetProgress(reset, true);
    }

    void resetProgress(boolean reset, boolean rebreak) {
        if (currentPos == null) return;
        if (reset) {
            if(abort.getValue())   mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, currentPos, currentFace));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, currentPos, currentFace));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos, EnumFacing.DOWN));
        }

        mineDamage = 0;
        if (rebreak) rebreakCount = 0;
    }

    void stopProgress() {
//        if (reset) mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos, currentFace));
        rebreakCount = 0;
        currentPos = null;
    }

    void sendPacket() {
        if(abort.getValue())   mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, currentPos, currentFace));
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, currentPos, currentFace));
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos, EnumFacing.DOWN));
    }



    public int getBestSlot(IBlockState state) {

        // the efficient slot
        int bestSlot = -1;

        // find the most efficient item
        double bestBreakSpeed = 0;

        // iterate through item in the hotbar
        for (int i = 0; i < 9; i++) {
            if (!mc.player.inventory.getStackInSlot(i).isEmpty()) {
                float breakSpeed = mc.player.inventory.getStackInSlot(i).getDestroySpeed(state);

                // make sure the block is breakable
                if (breakSpeed > 1) {

                    // scale by efficiency enchantment
                    if (EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, mc.player.inventory.getStackInSlot(i)) > 0) {
                        breakSpeed += StrictMath.pow(EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, mc.player.inventory.getStackInSlot(i)), 2) + 1;
                    }

                    // if it's greater than our best break speed, mc.player our new most efficient item
                    if (breakSpeed > bestBreakSpeed) {
                        bestBreakSpeed = breakSpeed;
                        bestSlot = i;
                    }
                }
            }
        }

        // return the most efficient item
        if (bestSlot != -1) {
            return bestSlot;
        }

        return mc.player.inventory.currentItem;
    }

    public ItemStack getEfficientItem(IBlockState state) {

        // the efficient slot
        int bestSlot = -1;

        // find the most efficient item
        double bestBreakSpeed = 0;

        // iterate through item in the hotbar
        for (int i = 0; i < 9; i++) {
            if (!mc.player.inventory.getStackInSlot(i).isEmpty()) {
                float breakSpeed = mc.player.inventory.getStackInSlot(i).getDestroySpeed(state);

                // make sure the block is breakable
                if (breakSpeed > 1) {

                    // scale by efficiency enchantment
                    if (EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, mc.player.inventory.getStackInSlot(i)) > 0) {
                        breakSpeed += StrictMath.pow(EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, mc.player.inventory.getStackInSlot(i)), 2) + 1;
                    }

                    // if it's greater than our best break speed, mc.player our new most efficient item
                    if (breakSpeed > bestBreakSpeed) {
                        bestBreakSpeed = breakSpeed;
                        bestSlot = i;
                    }
                }
            }
        }

        // return the most efficient item
        if (bestSlot != -1) {
            return mc.player.inventory.getStackInSlot(bestSlot);
        }

        return mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem);
    }

    /**
     * Finds the block strength of a specified block
     *
     * @param state    The {@link IBlockState} block state of the specified block
     * @param position The {@link BlockPos} position of the specified block
     * @return The block strength of the specified block
     */
    public float getBlockStrength(IBlockState state, BlockPos position) {

//        if (mineDamage == 0) {
//            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, currentPos, currentFace));
//            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, currentPos, currentFace));
//        }
        // the block's hardness
        float hardness = state.getBlockHardness(mc.world, position);

        // if the block is air, it has no strength
        if (hardness > 0) {
            currentState = state;
            currentHardness = hardness;
            return getDigSpeed(state) / hardness / 30F;
        } else {
            return getDigSpeed(currentState) / currentHardness / 30F;
        }
    }

    /**
     * Finds the dig speed of a specified block
     *
     * @param state {@link IBlockState} The block state of the specified block
     * @return The dig speed of the specified block
     */
    @SuppressWarnings("all")
    public float getDigSpeed(IBlockState state) {

        // base dig speed
        float digSpeed = getDestroySpeed(state);

        if (digSpeed > 1) {
            ItemStack itemstack =  getEfficientItem(state);

            // efficiency level
            int efficiencyModifier = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, itemstack);

            // scale by efficiency level
            if (efficiencyModifier > 0 && !itemstack.isEmpty()) {
                digSpeed += StrictMath.pow(efficiencyModifier, 2) + 1;
            }
        }


        // scaled based on haste effect level
        if (mc.player.isPotionActive(MobEffects.HASTE)) {
            digSpeed *= 1 + (mc.player.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2F;
        }

        if (mc.player.isPotionActive(MobEffects.MINING_FATIGUE)) {

            // scale based on fatigue effect level
            float fatigueScale;
            switch (mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    fatigueScale = 0.3F;
                    break;
                case 1:
                    fatigueScale = 0.09F;
                    break;
                case 2:
                    fatigueScale = 0.0027F;
                    break;
                case 3:
                default:
                    fatigueScale = 8.1E-4F;
            }

            digSpeed *= fatigueScale;
        }

        // reduce dig speed if the player is in water
        if (mc.player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(mc.player)) {
            digSpeed /= 5;
        }

        // reduce dig speed if the player is not on the ground
        if (!mc.player.onGround) {
            digSpeed /= 5;
        }

        return (digSpeed < 0 ? 0 : digSpeed);
    }

    /**
     * Finds the destroy speed of a specified position
     *
     * @param state {@link IBlockState} The position to get the destroy speed for
     * @return The destroy speed of the specified position
     */
    public float getDestroySpeed(IBlockState state) {

        // base destroy speed
        float destroySpeed = 1;

        // scale by the item's destroy speed
        if (getEfficientItem(state) != null && !getEfficientItem(state).isEmpty()) {
            destroySpeed *= getEfficientItem(state).getDestroySpeed(state);
        }

        return destroySpeed;
    }

    public BlockPos getCurrentPos() {
        return currentPos;
    }

    public static boolean setBlock(BlockPos pos, EnumFacing facing) {
        if (!Caches.getModule(ForeverSpeedMine.class).isEnabled()) return false;
        if (pos == null || facing == null) return false;
//        speedMine.onBlockEvent(new BlockEvent(0,pos,facing));
        mc.playerController.clickBlock(pos, facing);
        return true;
    }

}
