package me.earth.earthhack.impl.modules.client.keybindlist;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.MathUtil;

public class ListenerUpdate extends ModuleListener<keybindthing, UpdateEvent> {
    public ListenerUpdate(keybindthing module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        if (module.isEnabled()) {
            module.percent += 0.02f;
        } else {
            module.percent -= 0.02f;
        }
        module.percent = ( float ) MathUtil.clamp(module.percent, 0, 1);
    }
}
