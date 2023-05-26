package me.earth.earthhack.impl.modules.movement.smartblocklag;

import me.earth.earthhack.impl.event.events.misc.EatEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.item.ItemChorusFruit;

public class ListenerEat extends ModuleListener<SmartBlockLag,EatEvent> {


    public ListenerEat(SmartBlockLag module) {
        super(module,EatEvent.class);
    }

    @Override
    public void invoke(EatEvent event) {
        if(event.getStack().getItem() instanceof ItemChorusFruit){
            module.atechorus=true;
        }
    }
}
