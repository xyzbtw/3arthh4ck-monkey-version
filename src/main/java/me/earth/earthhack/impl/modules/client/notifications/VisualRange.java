package me.earth.earthhack.impl.modules.client.notifications;

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.events.render.RenderEntityInWorldEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


final class VisualRange extends ModuleListener<Notifications, TickEvent> {

    public VisualRange(Notifications module) {
        super(module, TickEvent.class);
    }

    List<Entity> knownPlayers = new ArrayList<>();
    List<Entity> players;
    @Override
    public void invoke(TickEvent event){
        if (mc.world == null || mc.player == null) {
            return;
        }

        players = mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityPlayer).collect(Collectors.toList());
        try {
            for (Entity e : players) {
                if (e instanceof EntityPlayer && !e.getName().equalsIgnoreCase(mc.player.getName())) {
                    if (!knownPlayers.contains(e)) {
                        knownPlayers.add(e);
                        module.onRenderEntityInWorld(e);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        try {
            for (Entity e : knownPlayers) {
                if (e instanceof EntityPlayer && !e.getName().equalsIgnoreCase(mc.player.getName())) {
                    if (!players.contains(e)) {
                        module.onLeaveRenderDistance(e);
                        knownPlayers.remove(e);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    };

}
