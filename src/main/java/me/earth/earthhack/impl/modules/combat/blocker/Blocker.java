package me.earth.earthhack.impl.modules.combat.blocker;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.helpers.blocks.ObbyListenerModule;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;

public class Blocker extends ObbyListenerModule<ListenerObsidian> {

    protected final Setting<Boolean> anticev =
            register(new BooleanSetting("AntiCev", false));
    protected final Setting<Boolean> extend =
            register(new BooleanSetting("Extend", true));
    protected final Setting<Boolean> face =
            register(new BooleanSetting("Face", true));
    protected final Setting<Boolean> hole =
            register(new BooleanSetting("HoleCheck", true));
    protected final Setting<Boolean> blockchange =
            register(new BooleanSetting("BlockChange", true));
    protected final Setting<Boolean> debug =
            register(new BooleanSetting("Debug", false));

    protected final Setting<Boolean> fullExtend =
            register(new BooleanSetting("FullExtend", true));
    protected final Setting<Boolean> extendxyz =
            register(new BooleanSetting("Extend-diag", false));
    protected final Setting<Boolean> helping =
            register(new BooleanSetting("HelpingBlocks", false));
    protected final Setting<Float> enemyrange =
            register(new NumberSetting<>("EnemyRange", 6.0f, 0.0f, 10.0f));
    protected final Setting<Integer> clearDelay =
            register(new NumberSetting<>("ClearDelay", 500, 0, 3000));

    protected EntityPlayer target;
    protected final ModuleCache<Speedmine> speedmine = Caches.getModule(Speedmine.class);

    public Blocker() {
        super("Blocker", Category.Combat);
        this.listeners.add(new ListenerBlockBreakAnim(this));
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerUpdate(this));
        this.setData(new BlockerData(this));

    }
    protected ArrayList<BlockPos> scheduledPlacements = new ArrayList<>();
    Vec3i[] replaceList= new Vec3i[]{
            new Vec3i(0,3,0), //anticev check


            new Vec3i(1,0,0), //surround checks
            new Vec3i(-1,0,0),
            new Vec3i(0,0,1),
            new Vec3i(0,0,-1),

            new Vec3i(1,1,0), //diag cev checks
            new Vec3i(-1,1,0),
            new Vec3i(0,1,1),
            new Vec3i(0,1,-1)


    };




    @Override
    protected boolean shouldHelp(EnumFacing facing, BlockPos pos)
    {
        return super.shouldHelp(facing, pos) // ??????
                && helping.getValue();
    }

    protected void scanAndPlace(BlockPos pos, boolean replace){
        if(mc.world==null)return;
        if(mc.player==null)return;
        if(mc.currentScreen instanceof GuiConnecting)return;
        target = EntityUtil.getClosestEnemy();
        if (pos == this.speedmine.get().getPos()) return;
        if(target == null || pos.getDistance(target.getPosition().getX(), target.getPosition().getY(), target.getPosition().getZ()) >= this.enemyrange.getValue()) return;

        if(hole.getValue() && !PlayerUtil.isInHoleAll(mc.player))
            return;

        BlockPos playerPos = PlayerUtil.getPlayerPos();

        //checking if we should care about the block in question
        for(Vec3i offset : replaceList){
            if(playerPos.add(offset).equals(pos)){
                break;
            }

            //if we are at our last element in the iterator and none of the elements suited us, abort the method
            if(offset.equals(replaceList[replaceList.length-1])){
                return;
            }
        }

        if(pos == playerPos.add(0,3,0)){
            scheduledPlacements.add(pos);
            return;
        }

        //if the block was broken, it should create a supporting block for extend to be placed at
        if(replace){
           scheduledPlacements.add(pos);
        }


        for(EnumFacing face : EnumFacing.values()) {
            if(pos.offset(face).equals(playerPos)) continue;

            if(mc.world.isAirBlock(pos.offset(EnumFacing.DOWN))){
                scheduledPlacements.add(pos.offset(EnumFacing.DOWN));
            }

            if(fullExtend.getValue()){
                if(pos.getY()==playerPos.getY()){
                    scheduledPlacements.add(pos.offset(face));
                }else {
                    scheduledPlacements.add(pos.add(0,1,0));
                }
            }else {
                if(playerPos.offset(face).equals(pos)){
                    if(this.extend.getValue()){
                       scheduledPlacements.add(playerPos.offset(face).offset(face));
                    }

                    if(this.face.getValue()){
                       scheduledPlacements.add(playerPos.offset(face).add(0,1,0));
                    }
                    if(this.extendxyz.getValue()){
                        scheduledPlacements.add(playerPos.offset(face).offset(face.rotateYCCW()));
                        scheduledPlacements.add(playerPos.offset(face).offset(face.rotateY()));
                    }
                }
            }
        }

    }
    @Override
    public boolean execute()
    {
        return super.execute();
    }

    @Override
    protected ListenerObsidian createListener() {
        return new ListenerObsidian(this);
    }
}
