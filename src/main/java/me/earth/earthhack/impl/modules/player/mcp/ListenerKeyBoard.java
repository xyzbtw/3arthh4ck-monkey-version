package me.earth.earthhack.impl.modules.player.mcp;

import me.earth.earthhack.impl.event.events.keyboard.KeyboardEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerKeyBoard extends ModuleListener<MiddleClickPearl, KeyboardEvent> {
    public ListenerKeyBoard(MiddleClickPearl module) {
        super(module, KeyboardEvent.class);
    }

    @Override
    public void invoke(KeyboardEvent event) {
        if(event.getKey() == module.button.getValue().getKey() && !module.midclickOnly.getValue() ){
            module.ccBypass();
        }
    }
}
