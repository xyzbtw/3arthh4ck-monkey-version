package me.earth.earthhack.impl.modules.movement.smartblocklag;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.text.ChatIDs;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.util.math.MathHelper.floor;

public class SmartBlockLag extends Module {
    protected EntityPlayer target;
    protected BlockPos pos;

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
        target = null;
    }
    @Override
    public String getDisplayInfo()
    {
        if (target != null)
        {
            return TextColor.RED + target.getName();
        }

        return null;
    }

    protected boolean isInsideBlock() {
        double x = mc.player.posX;
        double y = mc.player.posY + 0.20;
        double z = mc.player.posZ;

        return mc.world.getBlockState(new BlockPos(x, y, z)).getMaterial().blocksMovement() || !mc.player.collidedVertically;
    }
}
