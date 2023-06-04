package me.earth.earthhack.impl.modules.combat.automend;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.blocks.InteractionUtil;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.math.path.BasePath;
import me.earth.earthhack.impl.util.math.path.PathFinder;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;



public class ListenerMotion extends ModuleListener<AutoMend, MotionUpdateEvent> {
    public ListenerMotion(AutoMend module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        {
            if (mc.world == null || mc.player == null) return;

            if (module.willtakedmg()) {
                ModuleUtil.disableRed(module, "Will take dmg, disabling");
                return;
            }

            if (module.blocks.getValue() && (
                    BlockUtil.isReplaceable(PlayerUtil.getPlayerPos().add(1, 1, 0))
                            || BlockUtil.isReplaceable(PlayerUtil.getPlayerPos().add(-1, 1, 0))
                            || BlockUtil.isReplaceable(PlayerUtil.getPlayerPos().add(0, 1, 1))
                            || BlockUtil.isReplaceable(PlayerUtil.getPlayerPos().add(0, 1, -1)))) {
                int lastSlot = mc.player.inventory.currentItem;
                BlockPos pos = PlayerUtil.getPlayerPos();
                for (Vec3i thing : module.blocking) {
                    if (BlockUtil.isReplaceable(pos.add(thing))) {
                        if (!InventoryUtil.isHolding(Blocks.OBSIDIAN) && InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN) !=-1) {
                            InventoryUtil.switchTo(InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN));
                        }
                        InteractionUtil.placeBlock(pos.add(thing), true, true, false);

                    }
                }
                InventoryUtil.switchTo(lastSlot);
            }
            if (module.blocks.getValue() && (
                    BlockUtil.isReplaceable(PlayerUtil.getPlayerPos().add(1, 1, 0))
                            || BlockUtil.isReplaceable(PlayerUtil.getPlayerPos().add(-1, 1, 0))
                            || BlockUtil.isReplaceable(PlayerUtil.getPlayerPos().add(0, 1, 1))
                            || BlockUtil.isReplaceable(PlayerUtil.getPlayerPos().add(0, 1, -1)))) {
                return;
            }

            if (event.getStage() == Stage.PRE) {
                event.setPitch(module.pitch.getValue());
            }else {


            if (module.timer.passed(module.Delay.getValue())) {
                module.timer.reset();

                if (module.SlotsToMoveTo.isEmpty())
                    return;

                boolean l_NeedBreak = false;

                for (AutoMend.MendState l_State : module.SlotsToMoveTo) {
                    if (l_State.MovedToInv)
                        continue;

                    l_State.MovedToInv = true;

                    //   SendMessage("" + l_State.SlotMovedTo);

                    if (l_State.Reequip) {
                        if (l_State.SlotMovedTo <= 4) {
                            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, l_State.SlotMovedTo, 0, ClickType.PICKUP, mc.player);
                            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, l_State.ArmorSlot, 0, ClickType.PICKUP, mc.player);
                        } else
                            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, l_State.SlotMovedTo, 0, ClickType.QUICK_MOVE, mc.player);
                        //   mc.playerController.windowClick(mc.player.inventoryContainer.windowId, l_State.ArmorSlot, 0, ClickType.PICKUP, mc.player);
                    } else {
                        //   mc.playerController.windowClick(mc.player.inventoryContainer.windowId, l_State.ArmorSlot, 0, ClickType.QUICK_MOVE, mc.player);
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, l_State.SlotMovedTo, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, l_State.ArmorSlot, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, l_State.SlotMovedTo, 0, ClickType.PICKUP, mc.player);
                    }

                    l_NeedBreak = true;
                    break;
                }

                if (!l_NeedBreak) {
                    module.ReadyToMend = true;

                    if (module.AllDone) {
                        ChatUtil.sendMessage(ChatFormatting.AQUA + "Disabling.");
                        module.disable();
                        return;
                    }
                }
            }

            if (!module.internalTimer.passed(1000))
                return;

            if (module.ReadyToMend && !module.AllDone) {
                ItemStack l_CurrItem = mc.player.getHeldItemMainhand();

                if (l_CurrItem.isEmpty() || l_CurrItem.getItem() != Items.EXPERIENCE_BOTTLE) {
                    int l_Slot = InventoryUtil.findInHotbar(item -> item.getItem() instanceof ItemExpBottle);

                    if (l_Slot != -1) {
                        InventoryUtil.switchTo(l_Slot);
                    } else {
                        ChatUtil.sendMessage(ChatFormatting.RED + "No XP Found!");

                        module.SlotsToMoveTo.forEach(p_State ->
                        {
                            p_State.MovedToInv = false;
                            p_State.Reequip = true;
                        });

                        module.SlotsToMoveTo.get(0).MovedToInv = true;
                        module.AllDone = true;
                        return;
                    }
                }

                l_CurrItem = mc.player.getHeldItemMainhand();

                if (l_CurrItem.isEmpty() || l_CurrItem.getItem() != Items.EXPERIENCE_BOTTLE)
                    return;

                for (ItemStack l_Stack : mc.player.getArmorInventoryList()) {
                    if (l_Stack == ItemStack.EMPTY || l_Stack.getItem() == Items.AIR)
                        continue;

                    float l_ArmorPct = module.GetArmorPct(l_Stack);

                    if (l_ArmorPct >= module.Pct.getValue()) {
                        if (!module.SlotsToMoveTo.isEmpty()) {
                            AutoMend.MendState l_State = module.SlotsToMoveTo.get(0);

                            if (l_State.DoneMending) {
                                module.SlotsToMoveTo.forEach(p_State ->
                                {
                                    p_State.MovedToInv = false;
                                    p_State.Reequip = true;
                                });
                                ChatUtil.sendMessage(ChatFormatting.GREEN + "All done!");
                                l_State.MovedToInv = true;
                                module.AllDone = true;
                                return;
                            }

                            l_State.DoneMending = true;
                            l_State.MovedToInv = false;
                            l_State.Reequip = false;

                            module.ReadyToMend = false;

                            module.SlotsToMoveTo.remove(0);
                            module.SlotsToMoveTo.add(l_State);

                            AutoMend.MendState l_NewState = module.SlotsToMoveTo.get(0);

                            if (l_NewState.DoneMending || !l_NewState.NeedMend) {
                                module.SlotsToMoveTo.forEach(p_State ->
                                {
                                    p_State.MovedToInv = false;
                                    p_State.Reequip = true;
                                });
                                l_State.MovedToInv = true;
                                ChatUtil.sendMessage(ChatFormatting.GREEN + "All done!");
                                module.AllDone = true;
                                return;
                            } else {

                                l_NewState.MovedToInv = false;
                                l_NewState.Reequip = true;
                            }
                        }

                        return;
                    } else {
                        if (module.xpTimer.passed(module.xpDelay.getValue())) {
                            mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
                            module.xpTimer.reset();
                        }


                        break;
                    }
                 }
                }
            }
        }
    }
}
