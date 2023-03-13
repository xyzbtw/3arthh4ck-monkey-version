package me.earth.earthhack.impl.modules.movement.step;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.movement.StepEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import me.earth.earthhack.pingbypass.protocol.c2s.C2SStepPacket;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketPlayer;


final class ListenerStep extends ModuleListener<Step, StepEvent> {
    public ListenerStep(Step module) {
        super(module, StepEvent.class);
    }

    @Override
    public void invoke(StepEvent event) {
        if (!Managers.NCP.passed(module.lagTime.getValue())) {
            module.reset();
            return;
        }

        if (event.getStage() == Stage.PRE) {
            if (mc.player.getRidingEntity() != null) {
                mc.player.getRidingEntity().stepHeight =
                    module.entityStep.getValue()
                        ? 256.0f
                        : 1.0f;
            }

            if (module.mode.getValue() != StepMode.Slow || !module.stepping) {
                // x and z assignments are probably unnecessary here
                module.x = mc.player.posX;
                module.y = event.getBB().minY;
                module.z = mc.player.posZ;

                //noinspection AssignmentUsedAsCondition
                if (module.stepping = module.canStep()) {
                    if (module.useTimer.getValue()) {
                        Managers.TIMER.setTimer(module.timer.getValue().floatValue());
                    }
                    event.setHeight(module.height.getValue());
                } else {
                    module.reset();
                }
            }
        } else if (module.stepping) {
            double height = event.getBB().minY - mc.player.posY;
            if (module.mode.getValue() == StepMode.Normal
                && height > event.getHeight()) {
                double[] offsets = getOffsets(height);
                if (PingBypassModule.CACHE.isEnabled()
                    && !PingBypassModule.CACHE.get().isOld()) {
                    mc.player.connection.sendPacket(
                        new C2SStepPacket(
                            offsets, module.x, module.y, module.z));
                } else {
                    for (double offset : offsets) {
                        mc.player.connection.sendPacket(
                            new CPacketPlayer.Position(
                                mc.player.posX,
                                mc.player.posY + offset,
                                mc.player.posZ,
                                false));
                    }
                }
            } else if (module.mode.getValue() == StepMode.Slow
                && height > event.getHeight()
                && module.offsets == null) {
                module.offsets = getOffsets(height);
                module.bb = event.getBB();
                module.index = 0;
                module.currHeight = height;
                //module.x = mc.player.posX;
                //module.y = mc.player.posY;
                //module.z = mc.player.posZ;
                mc.player.setPosition(module.x, module.y, module.z);
            }

            if (module.gapple.getValue()
                && module.stepping
                && module.mode.getValue() != StepMode.Slow
                && !module.breakTimer.passed(60)
                && InventoryUtil.isHolding(ItemPickaxe.class)
                && !InventoryUtil.isHolding(ItemAppleGold.class)) {
                Entity closest = EntityUtil.getClosestEnemy();
                if (closest != null && closest.getDistanceSq(mc.player) < 144) {
                    int slot = InventoryUtil.findHotbarItem(Items.GOLDEN_APPLE);
                    if (slot != -1) {
                        Locks.acquire(Locks.PLACE_SWITCH_LOCK,
                                      () -> InventoryUtil.switchTo(slot));
                    }
                }
            }

            if (module.mode.getValue() != StepMode.Slow
                && height > event.getHeight()) {
                module.reset();
                if (module.autoOff.getValue()) {
                    module.disable();
                }
            }
        }
    }

    public double[] getOffsets(double height) {

        // confirm step height (helps bypass on NCP Updated)
        // enchantment tables, 0.75 block offset
        if (height == 0.75) {
                return new double[] {
                        0.42,
                        0.753
                };

        }

        // end portal frames, 0.8125 block offset
        else if (height == 0.8125) {
                return new double[] {
                        0.39,
                        0.7
                };

        }

        // chests, 0.875 block offset
        else if (height == 0.875) {
                return new double[] {
                        0.39,
                        0.7
                };
        }

        // 1 block offset -> LITERALLY IMPOSSIBLE TO PATCH BECAUSE ITS JUST THE SAME PACKETS AS A JUMP
        else if (height == 1) {
                return new double[] {
                        0.42,
                        0.753
                };
        }

        // 1.5 block offset
        else if (height == 1.5) {
            return new double[] {
                    0.42,
                    0.75,
                    1.0,
                    1.16,
                    1.23,
                    1.2
            };
        }

        // 2 block offset
        else if (height == 2) {
            return new double[] {
                    0.42,
                    0.78,
                    0.63,
                    0.51,
                    0.9,
                    1.21,
                    1.45,
                    1.43
            };
        }

        // 2.5 block offset
        else if (height == 2.5) {
            return new double[] {
                    0.425,
                    0.821,
                    0.699,
                    0.599,
                    1.022,
                    1.372,
                    1.652,
                    1.869,
                    2.019,
                    1.907
            };
        }

        return null;
    }

}
