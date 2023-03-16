package me.earth.earthhack.impl.modules.combat.selftrap;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.listeners.ReceiveListener;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyModule;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyUtil;
import me.earth.earthhack.impl.util.helpers.blocks.modes.RayTraceMode;
import me.earth.earthhack.impl.util.math.path.BasePath;
import me.earth.earthhack.impl.util.math.path.PathFinder;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.math.BlockPos;

// Maybe extend?
public class SelfTrap extends ObbyListenerModule<ListenerSelfTrap>
{
    protected final Setting<SelfTrapMode> mode =
            register(new EnumSetting<>("Mode", SelfTrapMode.Obsidian));
    protected final Setting<Boolean> smart =
            register(new BooleanSetting("Smart", false));
    protected final Setting<Float> range =
            register(new NumberSetting<>("SmartRange", 6.0f, 0.0f, 20.0f));
    protected final Setting<Double> placeRange =
            register(new NumberSetting<>("PlaceRange", 6.0, 0.0, 7.5));
    protected final Setting<Integer> maxHelping =
            register(new NumberSetting<>("HelpingBlocks", 4, 0, 20));
    protected final Setting<Boolean> autoOff =
            register(new BooleanSetting("Auto-Off", true));
    protected final Setting<Boolean> smartOff =
            register(new BooleanSetting("Smart-Off", true));
    protected final Setting<Boolean> topExt =
            register(new BooleanSetting("Top-Extend", false));
    protected final Setting<Boolean> prioBehind =
            register(new BooleanSetting("Prio-Behind", true));

    protected BlockPos startPos;
    protected int slot;
    protected BlockPos clown = PlayerUtil.getPlayerPos().add(0, 2, 0);

    public SelfTrap()
    {
        super("SelfTrap", Category.Combat);
        this.listeners.add(new ReceiveListener<>(SPacketBlockBreakAnim.class, event -> {
            if(     event.getPacket().getBreakerId() != mc.player.getEntityId()
                    && event.getPacket().getPosition() == clown
                    && mc.world.getBlockState(clown).getBlock().isReplaceable(mc.world, clown.add(0,1,0))
                    && topExt.getValue())
            {
                BasePath path = new BasePath(
                        RotationUtil.getRotationPlayer(),
                        clown.add(0,1,0),
                        1);

                PathFinder.findPath(
                        path,
                        6,
                        mc.world.loadedEntityList,
                        RayTraceMode.Fast,
                        HELPER,
                        Blocks.OBSIDIAN.getDefaultState(),
                        PathFinder.CHECK);
                ObbyUtil.place(this, path);
            }

        }));
    }

    @Override
    protected void onEnable()
    {
        Entity entity = RotationUtil.getRotationPlayer();
        if (entity != null)
        {
            startPos = PositionUtil.getPosition(entity);
        }

        super.onEnable();
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        startPos = null;
    }

    @Override
    public boolean execute()
    {
        if (mode.getValue() != SelfTrapMode.Obsidian)
        {
            attacking = null;
        }

        return super.execute();
    }

    @Override
    protected ListenerSelfTrap createListener()
    {
        return new ListenerSelfTrap(this);
    }

    @Override
    public EntityPlayer getPlayerForRotations()
    {
        return RotationUtil.getRotationPlayer();
    }

    @Override
    public EntityPlayer getPlayer()
    {
        return RotationUtil.getRotationPlayer();
    }

    @Override
    protected boolean entityCheckSimple(BlockPos pos)
    {
        return true;
    }

    @Override
    public boolean entityCheck(BlockPos pos)
    {
        return true; // ???
    }

    @Override
    protected boolean quickEntityCheck(BlockPos pos)
    {
        return false;
    }

}
