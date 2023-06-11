package me.earth.earthhack.impl.modules.combat.blocker;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Complexity;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.DeathEvent;
import me.earth.earthhack.impl.event.events.misc.EndCrystalDestroyEvent;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.helpers.render.BlockESPBuilder;
import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;
import me.earth.earthhack.impl.util.helpers.render.IAxisESP;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.HoleUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Blocker extends ObbyListenerModule<ListenerObsidian> {
    protected final Setting<mode> modeSetting = register(new EnumSetting<>("Mode", mode.broken));
    protected final Setting<Boolean> anticev = register(new BooleanSetting("AntiCev", false));
    protected final Setting<Boolean> extend = register(new BooleanSetting("Extend", true));
    protected final Setting<Boolean> face = register(new BooleanSetting("Face", true));
    protected final Setting<Boolean> hole = register(new BooleanSetting("HoleCheck", true));
    protected final Setting<Boolean> fullExtend = register(new BooleanSetting("FullExtend", true));
    protected final Setting<Boolean> extendxyz = register(new BooleanSetting("Extend-diag", false));
    protected final Setting<Boolean> helping = register(new BooleanSetting("HelpingBlocks", false));
    protected final Setting<Boolean> undersurround = register(new BooleanSetting("UnderSurround", true));
    protected final Setting<Float> enemyrange = register(new NumberSetting<>("EnemyRange", 6.0f, 0.0f, 10.0f));
    protected final Setting<Integer> clearDelay = register(new NumberSetting<>("ClearDelay", 500, 0, 3000));
    protected final Setting<Integer> progress = register(new NumberSetting<>("Progress", 3, 0, 9));
    protected final Setting<Boolean> render = register(new BooleanSetting("Render", false));
    protected final Setting<Color> boxColor = register(new ColorSetting("Box", new Color(255, 255, 255, 120)));
    protected final Setting<Color> outLine = register(new ColorSetting("Outline", new Color(255, 255, 255, 240)));
    protected final Setting<Float> linewidth = register(new NumberSetting<>("LineWidth", 1.0f, 0.0f, 5.0f));
    protected final Setting<Float> renderheight = register(new NumberSetting<>("Height", 1.0f, -1.0f, 1.0f));
    protected final Setting<Boolean> debug = register(new BooleanSetting("Debug", false));

    protected HashMap<BlockPos, IBlockState> blockmap = new HashMap<>();

    protected EntityPlayer target;
    protected final ModuleCache<Speedmine> speedmine = Caches.getModule(Speedmine.class);

    public Blocker() {
        super("Blocker", Category.Combat);
        this.listeners.clear(); // Remove DisablingModule listeners
        this.listeners.add(new ListenerBlockBreakAnim(this));
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new ListenerObsidian(this));
        // this.listeners.add(new ListenerMultiBlockChang(this));
        this.listeners.add(new ListenerRender(this));
        this.setData(new BlockerData(this));
    }

    protected enum mode {
        touched,
        broken,
        both

    }

    protected ArrayList<BlockPos> scheduledPlacements = new ArrayList<>();
    Vec3i[] replaceList = new Vec3i[] {
            new Vec3i(0, 2, 0), // anticev check
            new Vec3i(0, -1, 0), // block underneath you

            new Vec3i(1, 0, 0), // surround checks
            new Vec3i(-1, 0, 0),
            new Vec3i(0, 0, 1),
            new Vec3i(0, 0, -1),

            new Vec3i(1, 1, 0), // side cev checks
            new Vec3i(-1, 1, 0),
            new Vec3i(0, 1, 1),
            new Vec3i(0, 1, -1)

    };

    protected IAxisESP esp = new BlockESPBuilder()
            .withColor(boxColor)
            .withOutlineColor(outLine)
            .withLineWidth(linewidth)
            .build();

    public void renderPos(BlockPos pos) {
        esp.render(Interpolation.interpolatePos(pos, renderheight.getValue()));
    }

    @Override
    protected boolean shouldHelp(EnumFacing facing, BlockPos pos) {
        return super.shouldHelp(facing, pos)
                && helping.getValue();
    }

    protected void scanAndPlace(BlockPos pos) {
        if (mc.world == null)
            return;
        if (mc.player == null)
            return;
        if (mc.currentScreen instanceof GuiConnecting)
            return;
        target = EntityUtil.getClosestEnemy();
        if (Speedmine.compatibility.contains(pos))
            return;
        if (target == null || pos.getDistance(target.getPosition().getX(), target.getPosition().getY(),
                target.getPosition().getZ()) >= this.enemyrange.getValue())
            return;
        if (modeSetting.getValue() == mode.broken)
            if (!(blockmap.get(pos).getBlock() instanceof BlockObsidian)
                    && !(blockmap.get(pos).getBlock() instanceof BlockEnderChest)) {
                if (debug.getValue())
                    ModuleUtil.sendMessage(this, "failed the fucking check xyz", "Blocker");
                blockmap.remove(pos);
                return;
            }

        BlockPos playerPos = PlayerUtil.getPlayerPos();

        if (HoleUtil.is2x1(playerPos)) {

        }

        // checking if we should care about the block in question
        for (Vec3i offset : replaceList) {
            if (playerPos.add(offset).equals(pos)) {
                if (debug.getValue()) {
                    ModuleUtil.sendMessageWithAquaModule(this, "Accepted the pos: " + pos, "");
                }
                break;
            }

            // if we are at our last element in the iterator and none of the elements suited
            // us, abort the method
            if (offset.equals(replaceList[replaceList.length - 1])) {
                if (debug.getValue()) {
                    ModuleUtil.sendMessageWithAquaModule(this, "Not found in replacelist, aborting", "");
                }
                return;
            }
        }

        if (hole.getValue() && !PlayerUtil.isInHoleAll(mc.player))
            return;

        if (pos == playerPos.add(0, 2, 0) && anticev.getValue()) {
            scheduledPlacements.add(pos.add(0, 1, 0));
            return;
        }

        if (pos == playerPos.add(0, -1, 0) && undersurround.getValue()) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                if (pos.offset(facing).equals(playerPos))
                    continue;
                scheduledPlacements.add(pos.offset(facing));
            }
        } else if (pos == playerPos.add(0, -1, 0) && !undersurround.getValue()) {
            return;
        }

        for (EnumFacing face : EnumFacing.values()) {
            if (pos.offset(face).equals(playerPos))
                continue;

            /*
             * if(mc.world.isAirBlock(pos.offset(EnumFacing.DOWN))){
             * scheduledPlacements.add(pos.offset(EnumFacing.DOWN));
             * }
             * 
             */

            if (fullExtend.getValue()) {
                if (pos.getY() == playerPos.getY()) {
                    scheduledPlacements.add(pos.offset(face));
                } else {
                    scheduledPlacements.add(pos.add(0, 1, 0));
                }
            } else {
                if (playerPos.offset(face).equals(pos)) {
                    if (this.extend.getValue() && face != EnumFacing.DOWN) {
                        scheduledPlacements.add(playerPos.offset(face).offset(face));
                    }

                    if (this.face.getValue()) {
                        scheduledPlacements.add(playerPos.offset(face).add(0, 1, 0));
                    }
                    if (this.extendxyz.getValue()) {
                        scheduledPlacements.add(playerPos.offset(face).offset(face.rotateYCCW()));
                        scheduledPlacements.add(playerPos.offset(face).offset(face.rotateY()));
                    }
                }
            }
        }
    }

    @Override
    protected ListenerObsidian createListener() {
        return new ListenerObsidian(this);
    }
}
