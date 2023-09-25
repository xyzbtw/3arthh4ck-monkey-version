package me.earth.earthhack.impl.modules.misc.autolog;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.misc.autoreconnect.AutoReconnect;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;

final class ListenerTick extends ModuleListener<AutoLog, TickEvent>
{
    public ListenerTick(AutoLog module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (mc.world != null && mc.player != null)
        {
            int totems = InventoryUtil.getCount(Items.TOTEM_OF_UNDYING);
            if(module.onRender.getValue()){
                for(Entity entity : mc.world.getLoadedEntityList()){
                    if (entity != mc.player
                            && !entity.getName().equals(module.FAKEPLAYER.get().getNameFakePlayer())
                            && !Managers.FRIENDS.contains(entity)
                            && entity instanceof EntityPlayer) {
                        module.disconnect(mc.player.getHealth(), (EntityPlayer) entity, totems);
                    }
                }
            }
            float health = module.absorption.getValue()
                            ? EntityUtil.getHealth(mc.player)
                            : mc.player.getHealth();
            if(module.ykick.getValue()){
                if(mc.player.getPosition().getY() <= module.ylevel.getValue()){
                    module.disconnect(health, null,InventoryUtil.getCount(Items.TOTEM_OF_UNDYING));
                }
            }
            if (health <= module.health.getValue())
            {
                EntityPlayer player = module.enemy.getValue() == 100
                        ? null
                        : EntityUtil.getClosestEnemy();

                if (module.enemy.getValue() == 100
                        || player != null
                            && player.getDistanceSq(mc.player)
                                <= MathUtil.square(module.enemy.getValue()))
                {
                    if (totems <= module.totems.getValue())
                    {
                        module.disconnect(health, player, totems);
                    }
                }
            }
        }
    }

}
