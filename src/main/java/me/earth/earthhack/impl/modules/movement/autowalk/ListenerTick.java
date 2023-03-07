package me.earth.earthhack.impl.modules.movement.autowalk;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.settings.KeyBinding;

final class ListenerTick extends ModuleListener<AutoWalk, TickEvent>
{
    public ListenerTick(AutoWalk module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(),
                true);
    }

}