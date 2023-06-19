package me.earth.earthhack.impl.modules.player.swing;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.gen.Accessor;

public class Swing extends Module {
    public final NumberSetting<Integer> swingSpeed =
            register(new NumberSetting<>("Speed", 6, 0, 20));

    public final BooleanSetting clientside =
            register(new BooleanSetting("ClientSide", false));

    protected final EnumSetting<SwingEnum> hand =
            register(new EnumSetting<>("Hand", SwingEnum.Mainhand));
    public final BooleanSetting changeMainhand =
            register(new BooleanSetting("ChangeMainhand", false));
    public final BooleanSetting changeOffhand =
            register(new BooleanSetting("ChangeOffhand", false));
    protected final NumberSetting<Float> mainhandprogress =
            register(new NumberSetting<>("MHProgress", 1.0f, -10.0f, 10.0f));
    protected final NumberSetting<Float> offhandprogress =
            register(new NumberSetting<>("OHProgress", 1.0f, -10.0f, 10.0f));

    public Swing() {
        super("Swing", Category.Player);
        this.setData(new SwingData(this));
        this.listeners.add(new ListenerUpdate(this));
        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e -> {
            if(mc.player == null && mc.world == null) return;

            switch(hand.getValue()){
                case Mainhand:
                    mc.player.swingingHand = EnumHand.MAIN_HAND;
                    break;

                case Offhand:
                    mc.player.swingingHand = EnumHand.OFF_HAND;
                    break;
            }
        }));
        this.listeners.add(new LambdaListener<>(PacketEvent.Send.class,e -> {
            if (!clientside.getValue()) {
                if (e.getPacket() instanceof CPacketAnimation) {
                    switch (hand.getValue())
                    {
                        case Mainhand:
                            if (((CPacketAnimation) e.getPacket()).getHand() != EnumHand.MAIN_HAND)
                            {
                                e.setCancelled(true);
                                mc.player.swingArm(EnumHand.MAIN_HAND);
                            }
                            break;

                        case Offhand:
                            if (((CPacketAnimation) e.getPacket()).getHand() != EnumHand.OFF_HAND)
                            {
                                e.setCancelled(true);
                                mc.player.swingArm(EnumHand.OFF_HAND);
                            }
                            break;
                    }
                }
            }

        }));
    }

}