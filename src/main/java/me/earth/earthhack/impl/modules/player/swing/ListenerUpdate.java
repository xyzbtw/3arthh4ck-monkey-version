package me.earth.earthhack.impl.modules.player.swing;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerUpdate extends ModuleListener<Swing, UpdateEvent> {
    public ListenerUpdate(Swing module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        /*(if (module.changeMainhand.getValue() && module.mc.entityRenderer.itemRenderer.equippedProgressMainHand != module.mainhandprogress.getValue()) {
            module.mc.entityRenderer.itemRenderer.equippedProgressMainHand = module.mainhandprogress.getValue();
            module.mc.entityRenderer.itemRenderer.itemStackMainHand = module.mc.player.getHeldItemMainhand();
        }
        if (module.changeOffhand.getValue() && module.mc.entityRenderer.itemRenderer.equippedProgressOffHand != module.offhandprogress.getValue()) {
            module.mc.entityRenderer.itemRenderer.equippedProgressOffHand = module.offhandprogress.getValue();
            module.mc.entityRenderer.itemRenderer.itemStackOffHand = module.mc.player.getHeldItemOffhand();
        }

         */
    }
}
