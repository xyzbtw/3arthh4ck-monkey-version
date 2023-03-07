package me.earth.earthhack.impl.modules.movement.phase;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.phase.mode.PhaseMode;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import static me.earth.earthhack.impl.modules.movement.phase.Phase.getMotion;

final class ListenerUpdate extends ModuleListener<Phase, UpdateEvent>
{
    public ListenerUpdate(Phase module)
    {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event)
    {
        if (module.mode.getValue() == PhaseMode.NoClip)
        {
            mc.player.noClip       = true;
            mc.player.onGround     = false;
            mc.player.fallDistance = 0;
        }

        if (module.mode.getValue() == PhaseMode.Constantiam
                && MovementUtil.isMoving()
                && module.constTeleport.getValue()
                && module.isPhasing()) {
            double multiplier = module.constSpeed.getValue();
            double mx = -Math.sin(Math.toRadians(this.mc.player.rotationYaw));
            double mz = Math.cos(Math.toRadians(this.mc.player.rotationYaw));
            double x = (double) mc.player.movementInput.moveForward * multiplier * mx + (double) mc.player.movementInput.moveStrafe * multiplier * mz;
            double z = (double) mc.player.movementInput.moveForward * multiplier * mz - (double) mc.player.movementInput.moveStrafe * multiplier * mx;
            this.mc.player.setPosition(this.mc.player.posX + x, this.mc.player.posY, this.mc.player.posZ + z);
        }

        if (module.mode.getValue() == PhaseMode.ConstantiamNew) {
            double multiplier = 0.3;
            double mx = -Math.sin(Math.toRadians(this.mc.player.rotationYaw));
            double mz = Math.cos(Math.toRadians(this.mc.player.rotationYaw));
            double x = (double)mc.player.movementInput.moveForward * multiplier * mx + (double)mc.player.movementInput.moveStrafe * multiplier * mz;
            double z = (double)mc.player.movementInput.moveForward * multiplier * mz - (double)mc.player.movementInput.moveStrafe * multiplier * mx;
            if (mc.player.collidedHorizontally && !this.mc.player.isOnLadder()) {
                PacketUtil.doPosition(mc.player.posX + x, mc.player.posY, mc.player.posZ + z, false);
                for (int i = 1; i < 10; ++i) {
                    PacketUtil.doPosition(mc.player.posX,8.988465674311579E307, mc.player.posZ, false);
                }
                this.mc.player.setPosition(this.mc.player.posX + x, this.mc.player.posY, this.mc.player.posZ + z);
            }
        }
        if(module.mode.getValue() == PhaseMode.Wall) {
            double[] dir;
            double[] dirSpeed;
            RayTraceResult trace;
            ++module.delay;
            double phaseSpeedValue = module.phaseSpeed.getValue() / 1000.0;
            if (module.antiVoid.getValue() && mc.player.posY <= (double) module.antiVoidHeight.getValue() && ((trace = mc.world.rayTraceBlocks(mc.player.getPositionVector(), new Vec3d(mc.player.posX, 0.0, mc.player.posZ), false, false, false)) == null || trace.typeOfHit != RayTraceResult.Type.BLOCK)) {
                mc.player.setVelocity(0.0, 0.0, 0.0);
            }
            if ((mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown()) && (!module.eChestCheck() && module.isPhasing())) {
                    if (mc.gameSettings.keyBindSneak.isPressed() && mc.player.isSneaking()) {
                        dirSpeed = getMotion(phaseSpeedValue);
                        if (module.downOnShift.getValue() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + dirSpeed[0], mc.player.posY - 0.0424, mc.player.posZ + dirSpeed[1], mc.player.rotationYaw, mc.player.rotationPitch, false));
                        } else {
                            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + dirSpeed[0], mc.player.posY, mc.player.posZ + dirSpeed[1], mc.player.rotationYaw, mc.player.rotationPitch, false));
                        }
                        mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, -1300.0, mc.player.posZ, mc.player.rotationYaw * -5.0f, mc.player.rotationPitch * -5.0f, true));

                        if (module.fallPacket.getValue()) {
                            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_RIDING_JUMP));
                        }
                        if (module.sprintPacket.getValue()) {
                            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                        }
                        if (module.downOnShift.getValue() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                            mc.player.setPosition(mc.player.posX + dirSpeed[0], mc.player.posY - 0.0424, mc.player.posZ + dirSpeed[1]);
                        } else {
                            mc.player.setPosition(mc.player.posX + dirSpeed[0], mc.player.posY, mc.player.posZ + dirSpeed[1]);
                        }
                        mc.player.motionZ = 0.0;
                        mc.player.motionY = 0.0;
                        mc.player.motionX = 0.0;
                        mc.player.noClip = true;
                    }
                if (mc.player.collidedHorizontally && module.stopMotion.getValue() ? module.delay >= module.stopMotionDelay.getValue() : mc.player.collidedHorizontally) {
                    dirSpeed = getMotion(phaseSpeedValue);
                        if (module.downOnShift.getValue()  && mc.gameSettings.keyBindSneak.isKeyDown()) {
                            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + dirSpeed[0], mc.player.posY - 0.1, mc.player.posZ + dirSpeed[1], mc.player.rotationYaw, mc.player.rotationPitch, false));
                            } else {mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX + dirSpeed[0], mc.player.posY, mc.player.posZ + dirSpeed[1], mc.player.rotationYaw, mc.player.rotationPitch, false));}
                    mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, -1300.0, mc.player.posZ, mc.player.rotationYaw * -5.0f, mc.player.rotationPitch * -5.0f, true));
                    if (module.fallPacket.getValue()) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_RIDING_JUMP));
                    }
                    if (module.sprintPacket.getValue()) {
                        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                    }
                    if (module.downOnShift.getValue() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.player.setPosition(mc.player.posX + dirSpeed[0], mc.player.posY - 0.1, mc.player.posZ + dirSpeed[1]);
                    } else {mc.player.setPosition(mc.player.posX + dirSpeed[0], mc.player.posY, mc.player.posZ + dirSpeed[1]);}
                    mc.player.motionZ = 0.0;
                    if(mc.gameSettings.keyBindSneak.isPressed() && mc.player.isSneaking() && module.downOnShift.getValue()){
                        mc.player.motionY -= 0.3;
                    }else {mc.player.motionY = 0.0;}
                    mc.player.motionX = 0.0;
                    mc.player.noClip = true;
                    module.delay = 0;
                }
            }
        }
    }

}
