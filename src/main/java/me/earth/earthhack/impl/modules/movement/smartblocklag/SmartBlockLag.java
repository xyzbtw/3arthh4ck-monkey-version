package me.earth.earthhack.impl.modules.movement.smartblocklag;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.text.ChatIDs;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.util.math.MathHelper.floor;

public class SmartBlockLag extends Module {
    public SmartBlockLag() {
        super("SmartBlockLag", Category.Movement);
        this.listeners.add(new ListenerTick(this));
    }
    protected final Setting<Float> smartRange =
            register(new NumberSetting<>("Range", 3.0f, 0.0f, 10.0f));
    protected  final Setting<Boolean> turnoff =
            register(new BooleanSetting("AutoOff", false));
    protected  final Setting<Boolean> holeonly =
            register(new BooleanSetting("OnlyInHole", false));
    protected final Setting<Integer> delay =
            register(new NumberSetting<>("Delay", 100, 0, 1000));

    protected final StopWatch delayTimer = new StopWatch();

    public void onEnable(){
        delayTimer.setTime(0);
        if(mc.isSingleplayer()){
            Managers.CHAT.sendDeleteMessage("Not a multiplayer world retard", getName(), ChatIDs.MODULE);
            this.disable();
        }
    }
    public boolean isPhasing()
    {
        AxisAlignedBB bb = mc.player.getEntityBoundingBox();
        for (int x = floor(bb.minX); x < floor(bb.maxX) + 1; x++)
        {
            for (int y = floor(bb.minY); y < floor(bb.maxY) + 1; y++)
            {
                for (int z = floor(bb.minZ); z < floor(bb.maxZ) + 1; z++)
                {
                    if (mc.world.getBlockState(new BlockPos(x, y, z))
                            .getMaterial()
                            .blocksMovement())
                    {
                        if (bb.intersects(
                                new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1)))
                        {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
