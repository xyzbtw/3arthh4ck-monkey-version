package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerClick extends ModuleListener<AutoMine, ClickBlockEvent> {
    public ListenerClick(AutoMine module) {
        super(module,ClickBlockEvent.class);
    }

    @Override
    public void invoke(ClickBlockEvent event) {
        if(event.getPos()!=module.current){
            module.current=event.getPos();
        }
    }
}
