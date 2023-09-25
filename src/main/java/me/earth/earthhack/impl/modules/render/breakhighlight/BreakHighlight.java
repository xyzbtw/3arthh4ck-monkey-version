package me.earth.earthhack.impl.modules.render.breakhighlight;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.helpers.render.BlockESPBuilder;
import me.earth.earthhack.impl.util.helpers.render.IAxisESP;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BreakHighlight extends Module {

    private final Setting<Float> range = register(new NumberSetting<>("Range", 1.0f, 0.1f, 10.0f));
    public Setting<Boolean> showPercentage = register(new BooleanSetting("ShowPercentage", true));
    public Setting<Boolean> self = register(new BooleanSetting("SelfRender", true));
    public Setting<Boolean> box = register(new BooleanSetting("Box", true));
    public Setting<Boolean> outline = register(new BooleanSetting("Outline", true));
    private final Setting<Float> lineWidth = register(new NumberSetting<>("LineWidth", 1.0f, 0.1f, 5.0f));
    protected Setting<Color> boxColor = register(new ColorSetting("BoxColor", new Color(0, 39, 255, 84)));
    protected Setting<Color> outlineColor = register(new ColorSetting("OutlineColor", new Color(0, 34, 255, 255)));

    public BreakHighlight() {
        super("BreakHighlight", Category.Render);
        this.listeners.add(new LambdaListener<>(Render3DEvent.class, e-> {
            if (!this.isSafe()) return;
            Set<BlockPos> displayed = new HashSet<>();
            for (int i = 0; i < possiblePacket.size(); i++) {
                BlockPos temp = (BlockPos) possiblePacket.get(i).get(0);
                int tick = (int) possiblePacket.get(i).get(1);
                EntityPlayer tempp = (EntityPlayer) mc.world.getEntityByID((int) possiblePacket.get(i).get(2));
                String name = "";
                if (tempp != null) {
                    name = tempp.getName();

                }
                if (BlockUtil.getBlock(temp) == Blocks.AIR
                        || BlockUtil.getBlock(temp) == Blocks.BEDROCK) {
                    possiblePacket.remove(i);
                    i--;
                    continue;
                }
                if (temp.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= range.getValue()) {
                    displayed.add(temp);
                    renderPos(temp);
                    if (showPercentage.getValue())
                        RenderUtil.drawText(temp, String.format("%.1f", (float) (Math.min(tick, 200)) / 200 * 100) + "% " + name, true);
                } else possiblePacket.get(i).set(1, ++tick);
                if (++tick > 200 + 200) {
                    possiblePacket.remove(i);
                    i--;
                } else possiblePacket.get(i).set(1, tick);
            }
            {
                mc.renderGlobal.damagedBlocks.forEach((integer, destroyBlockProgress) -> {
                    if (destroyBlockProgress != null) {
                        EntityPlayer target = (EntityPlayer) mc.world.getEntityByID(integer);
                        String name = "";
                        if (target != null) {
                            if (target == mc.player && !self.getValue()) return;
                            name = target.getName();
                        }
                        BlockPos blockPos = destroyBlockProgress.getPosition();
                        if (mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR || displayed.contains(blockPos))
                            return;
                        if (blockPos.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= range.getValue()) {

                            renderPos(blockPos);
                            float f2 = destroyBlockProgress.getPartialBlockDamage() * 10.0f;
                            if (showPercentage.getValue())
                                RenderUtil.drawText(blockPos, String.format("%.1f", f2) + "% " + name, true);
                            //  RenderUtil.drawText(blockPos.getX() ,blockPos.getY() + lineWidth.getValue(),blockPos.getZ(),,true);
                        }
                    }
                });
            }
        }));
        this.listeners.add(new LambdaListener<>(PacketEvent.Receive.class, e-> {
            if (e.getPacket() instanceof SPacketBlockBreakAnim) {
                // Get it
                SPacketBlockBreakAnim pack = (SPacketBlockBreakAnim) e.getPacket();
                if(mc.world.getBlockState(pack.getPosition()).getBlock().equals(Blocks.BEDROCK)) return;
                // If we dont have it
                if (!havePos(pack.getPosition()))
                    possiblePacket.add(new ArrayList<Object>() {{
                        add(pack.getPosition());
                        add(0);
                        add(pack.getBreakerId());
                    }});
            }
        }));
    }
    protected IAxisESP esp = new BlockESPBuilder()
            .withColor(boxColor)
            .withOutlineColor(outlineColor)
            .withLineWidth(lineWidth)
            .build();
    public void renderPos(BlockPos pos)
    {
        esp.render(Interpolation.interpolatePos(pos, 1));
    }

    ArrayList<ArrayList<Object>> possiblePacket = new ArrayList<>();

    boolean havePos(BlockPos pos) {
        for (ArrayList<Object> part : possiblePacket) {
            // If we already have it
            BlockPos temp = (BlockPos) part.get(0);
            if (temp.getX() == pos.getX() && temp.getY() == pos.getY() && temp.getZ() == pos.getZ()) {
                // Remove
                return true;
            }
        }
        return false;
    }
}
