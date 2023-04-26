package me.earth.earthhack.impl.modules.misc.coordannouncer;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.client.SimpleData;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class CoordAnnouncer extends DisablingModule {
    public CoordAnnouncer() {
        super("CoordCopy", Category.Misc);
        this.setData(new SimpleData(this, "Copies your coords to clipboard"));
    }



    @Override
    public void onEnable(){
        super.onEnable();

        if(mc.world == null || mc.player == null) return;
        BlockPos coords = PlayerUtil.getPlayerPos();
        StringSelection stringSelection = new StringSelection(String.valueOf(coords));

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

        ModuleUtil.sendMessage(this, "Copied coordinates");
        this.disable();
    }


}
