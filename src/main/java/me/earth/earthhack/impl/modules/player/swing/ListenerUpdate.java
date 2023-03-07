package me.earth.earthhack.impl.modules.player.swing;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.EnumHand;

import java.lang.reflect.Field;

public class ListenerUpdate extends ModuleListener<Swing, UpdateEvent> {

    public ListenerUpdate(Swing module) {
        super(module, UpdateEvent.class);
    }

    @Override
    public void invoke(UpdateEvent event) {
        try {
            Field equippedProgressMainHand = ItemRenderer.class.getDeclaredField("equippedProgressMainHand");
            Field equippedProgressOffHand = ItemRenderer.class.getDeclaredField("equippedProgressOffHand");
            Field itemStackMainHand = ItemRenderer.class.getDeclaredField("itemStackMainHand");
            Field itemStackOffHand = ItemRenderer.class.getDeclaredField("itemStackOffHand");
            equippedProgressMainHand.setAccessible(true);
            equippedProgressOffHand.setAccessible(true);
            if (module.changeMainhand.getValue()) {
                equippedProgressMainHand.setFloat(mc.entityRenderer.itemRenderer, module.mainhand.getValue());
                itemStackMainHand.set(mc.entityRenderer.itemRenderer, mc.player.getHeldItemMainhand());
            }
            if (module.changeOffhand.getValue()) {
                equippedProgressOffHand.setFloat(mc.entityRenderer.itemRenderer, module.offhand.getValue());
                itemStackOffHand.set(mc.entityRenderer.itemRenderer, mc.player.getHeldItemOffhand());
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            // Handle the exception(s) as needed
            e.printStackTrace();
        }
    }
}
