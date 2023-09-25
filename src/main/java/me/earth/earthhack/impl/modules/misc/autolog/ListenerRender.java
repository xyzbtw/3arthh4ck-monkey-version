package me.earth.earthhack.impl.modules.misc.autolog;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.EntityChunkEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;

public class ListenerRender extends ModuleListener<AutoLog, EntityChunkEvent> {
    public ListenerRender(AutoLog module) {
        super(module, EntityChunkEvent.class);
    }

    @Override
    public void invoke(EntityChunkEvent event) {
        if(!module.onRender.getValue()) return;
        Entity entity = event.getEntity();
        if (event.getStage() == Stage.PRE
                && event.getEntity() != null
                && entity != mc.player
                && !entity.getName().equals(module.FAKEPLAYER.get().getNameFakePlayer())
                && entity instanceof EntityPlayer
                && !Managers.FRIENDS.contains(entity)) {
            int totems = InventoryUtil.getCount(Items.TOTEM_OF_UNDYING);
            module.disconnect(mc.player.getHealth(), (EntityPlayer) entity, totems);
        }
    }
}
