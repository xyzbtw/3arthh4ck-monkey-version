package me.earth.earthhack.impl.modules.misc.suffixmodule;

import me.earth.earthhack.impl.event.events.misc.EventSendMessage;
import me.earth.earthhack.impl.event.events.render.ChatEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.text.ITextComponent;

public class ListenerChat extends ModuleListener<SuffixModule, EventSendMessage> {
    public ListenerChat(SuffixModule module) {
        super(module, EventSendMessage.class);
    }



    @Override
    public void invoke(EventSendMessage event) {
        String message = event.getMessage();
        if(message.startsWith("." ) || message.startsWith("+")) return;
        String newMessage = message + module.getSuffix();
        event.setCancelled(true);
        mc.player.connection.sendPacket(new CPacketChatMessage(newMessage));
    }
}
