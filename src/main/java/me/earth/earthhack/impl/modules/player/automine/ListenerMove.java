package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

public class ListenerMove extends ModuleListener<AutoMine, MotionUpdateEvent> {
    public ListenerMove(AutoMine module) {
        super(module,MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event) {
        if(!module.rotate.getValue()) return;
        if(event.getStage()!= Stage.PRE) return;
        if(module.rotations != null) {
            setRotations(module.rotations, event);
        }
    }

    protected void setRotations(float[] rotations, MotionUpdateEvent event)
    {
        event.setYaw(rotations[0]);
        event.setPitch(rotations[1]);
    }


}
