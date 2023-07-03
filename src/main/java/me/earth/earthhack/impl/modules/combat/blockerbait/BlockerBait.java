package me.earth.earthhack.impl.modules.combat.blockerbait;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.automine.AutoMine;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class BlockerBait extends Module {

    public BlockerBait() {
        super("BlockerBait", Category.Combat);
        this.listeners.add(new ListenerUpdate(this));
    }

    protected ModuleCache<AutoMine> AUTOMINE = Caches.getModule(AutoMine.class);

    protected Setting<Double> distance =
            register(new NumberSetting<>("Distance", 6.0, 0.0, 12.0));
    protected Setting<Long> delay =
            register(new NumberSetting<>("Delay", 100L, 0L, 1000L));


    protected StopWatch timer = new StopWatch();

    protected Vec3i[] offsets = new Vec3i[]{

            new Vec3i(1,0,0), //surround
            new Vec3i(-1,0,0),
            new Vec3i(0,0,1),
            new Vec3i(0,0,-1),


            new Vec3i(1,1,0), //near face
            new Vec3i(-1,1,0),
            new Vec3i(0,1,1),
            new Vec3i(0,1,-1),

            new Vec3i(0,2,0) //cev

    };


    @Override
    public void onEnable(){
        super.onEnable();
        new Thread(() -> {
            EntityPlayer target = EntityUtil.getClosestEnemy();
            if(target!=null){
                for(Vec3i hit : offsets){
                    BlockPos enemyPos = target.getPosition();
                    BlockPos hitPos = enemyPos.add(hit);
                    EnumFacing facing = RayTraceUtil.getFacing(mc.player, hitPos, true);

                    if(BlockUtil.isAir(hitPos) || mc.world.getBlockState(hitPos).getBlock() == (Blocks.BEDROCK)) continue;
                    if (hit.equals(offsets[offsets.length - 1]) || AUTOMINE.get().getCurrent() !=null) {
                       this.disable();
                    }
                    assert facing != null;
                    mc.playerController.onPlayerDamageBlock(hitPos, facing);
                    try {
                        Thread.sleep(delay.getValue());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }




}
