package me.earth.earthhack.impl.modules.player.arrows;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class MMQuiver extends Module {
    boolean usePressed;
    int currslot, bowslot;
    public MMQuiver() {
        super("MMQuiver", Category.Player);
        this.listeners.add(new LambdaListener<>(TickEvent.class, e-> {
            if(mc.world==null || mc.player==null){
                disable();
                return;
            }

            if (usePressed && auto.getValue()) mc.gameSettings.keyBindUseItem.pressed=true;


            if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && mc.player.getItemInUseMaxCount() >= 4 && mc.player.isHandActive()){
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, -90, mc.player.onGround));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                mc.player.stopActiveHand();
                if (auto.getValue()) {
                    usePressed = false;
                    mc.gameSettings.keyBindUseItem.pressed=false;
                    disable();
                }
            }
        }));
    }
    protected Setting<Boolean> auto = register(new BooleanSetting("Auto", false));
    protected Setting<CooldownBypass> cdbypass = register(new EnumSetting<>("CD-Bypass", CooldownBypass.None));

    @Override
    public void onEnable(){
        super.onEnable();
        currslot=mc.player.inventory.currentItem;
        if (auto.getValue()) usePressed = true;
        bowslot = InventoryUtil.findInHotbar(bow -> bow.getItem() == Items.BOW);
        if(bowslot !=-1){
            cdbypass.getValue().switchTo(bowslot);
        }else {
            ModuleUtil.disableRed(this, "No bow");
        }
    }

    @Override
    public void onDisable(){
        super.onDisable();
        usePressed = false;
        cdbypass.getValue().switchBack(currslot, bowslot);
    }



}
