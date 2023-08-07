package me.earth.earthhack.impl.modules.misc.clownfat;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.network.play.server.SPacketChat;

public class ClownFat extends Module {

    protected Setting<String> name = register(new StringSetting("Name", "pidor"));
    protected Setting<Integer> delay = register(new NumberSetting<>("Delay", 1000, 0, 5000));
    protected StopWatch timer = new StopWatch();
    public ClownFat() {
        super("ClownFat", Category.Misc);
        this.listeners.add(new LambdaListener<>(PacketEvent.Receive.class, e->
        {
            if(mc.player==null || mc.world==null) return;
            if(!timer.passed(delay.getValue())) return;
            if(e.getPacket() instanceof SPacketChat){
                SPacketChat packet = (SPacketChat) e.getPacket();
                String string = packet.getChatComponent().getFormattedText();
                int indexofname = string.indexOf(name.getValue() + ">");

                if(string.contains(name.getValue())){
                    String string2 = string.substring(indexofname + name.getValue().length());
                    String[] replace = new String[]{"{", "}", ":", "\u00A7", "<", ">" };
                    String finalstring = "";
                    for(String r : replace){
                        finalstring = string2.replace(r, "");
                    }
                    mc.player.sendChatMessage(finalstring);
                    timer.reset();
                }
            }
        }));
    }
}
