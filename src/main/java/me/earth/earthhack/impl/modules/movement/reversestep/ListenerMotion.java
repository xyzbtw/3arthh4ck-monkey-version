package me.earth.earthhack.impl.modules.movement.reversestep;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.modules.movement.longjump.LongJump;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.stream.Collectors;

final class ListenerMotion extends ModuleListener<ReverseStep, MotionUpdateEvent> {
    private static final ModuleCache<PacketFly> PACKET_FLY =
            Caches.getModule(PacketFly.class);
    private static final ModuleCache<BlockLag> BLOCK_LAG =
            Caches.getModule(BlockLag.class);
    private static final ModuleCache<Speed> SPEED =
            Caches.getModule(Speed.class);
    private static final ModuleCache<LongJump> LONGJUMP =
            Caches.getModule(LongJump.class);
    private static final SettingCache<SpeedMode, EnumSetting<SpeedMode>, Speed>
            SPEED_MODE = Caches.getSetting(
                    Speed.class, Setting.class, "Mode", SpeedMode.Instant);

    private boolean reset = false;

    public ListenerMotion(ReverseStep module) {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if (event.getStage() == Stage.POST) {
            if (PositionUtil.inLiquid(true)
                    || PositionUtil.inLiquid(false)
                    || PACKET_FLY.isEnabled()
                    || BLOCK_LAG.isEnabled()
                    || LONGJUMP.isEnabled()
                    || SPEED.isEnabled()
                        && SPEED_MODE.getValue() != SpeedMode.Instant) {
                reset = true;
                module.shouldstopmotion=false;
                return;
            }
            final List<EntityEnderPearl> pearls = mc.world.loadedEntityList.stream()
                    .filter(EntityEnderPearl.class::isInstance)
                    .map(EntityEnderPearl.class::cast)
                    .collect(Collectors.toList());
            if (!pearls.isEmpty()) {
                module.waitForOnGround = true;
            }
            if (!mc.player.onGround) {
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    module.jumped = true;
                }
            } else {
                module.jumped = false;
                reset = false;
                module.waitForOnGround = false;
            }

            if (!module.jumped
                    && mc.player.fallDistance < 0.5
                    && mc.player.posY - module.getNearestBlockBelow() > 0.625
                    && mc.player.posY - module.getNearestBlockBelow() <= module.distance.getValue()
                    && !reset
                    && !module.waitForOnGround) {
                if (!mc.player.onGround) {
                    module.packets++;
                }

                if (!mc.player.onGround && mc.player.motionY < 0
                        && !(mc.player.isOnLadder()
                        || mc.player.isEntityInsideOpaqueBlock())
                        && (!module.strictLiquid.getValue() || (!mc.player.isInsideOfMaterial(Material.LAVA) && !mc.player.isInsideOfMaterial(Material.WATER)))
                        && !mc.gameSettings.keyBindJump.isKeyDown()
                        && module.packets > 0) {

                    module.shouldstopmotion=true;
                    if(module.mode.getValue() == ReverseStep.fallmode.strict && module.shouldstopmotion){
                        if(module.movementkeys.getValue()) {
                            mc.player.movementInput.forwardKeyDown = false;
                            mc.player.movementInput.backKeyDown = false;
                            mc.player.movementInput.rightKeyDown = false;
                            mc.player.movementInput.leftKeyDown = false;
                        }else{
                            MovementUtil.setMoveSpeed(0);
                        }
                        Managers.TIMER.setTimer(module.strictSpeed.getValue());
                    }
                    if(module.mode.getValue() == ReverseStep.fallmode.normal){
                        mc.player.motionY = -module.speed.getValue();
                    }
                    module.packets = 0;
                }
            }else{
                module.shouldstopmotion=false;
                Managers.TIMER.reset();
            }
        }
    }

    private boolean isLiquid(BlockPos position) {
        Block block = mc.world.getBlockState(position).getBlock();
        return block == Blocks.LAVA || block == Blocks.FLOWING_LAVA || block == Blocks.WATER || block == Blocks.FLOWING_WATER;
    }

}
