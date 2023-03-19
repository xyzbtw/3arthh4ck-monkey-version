package me.earth.earthhack.impl.modules.combat.forclown;

import com.mojang.realmsclient.gui.ChatFormatting;
import jdk.nashorn.internal.ir.Block;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.blocks.InteractionUtil;
import me.earth.earthhack.impl.util.minecraft.CooldownBypass;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.NetworkUtil;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import org.apache.commons.codec.language.Nysiis;

public class forclown extends Module {

    protected final Setting<Boolean> extend =
            register(new BooleanSetting("Extend", true));
    protected final Setting<Boolean> face =
            register(new BooleanSetting("Face", true));
    protected final Setting<Boolean> packet =
            register(new BooleanSetting("Packet", true));
    //protected final Setting<Boolean> swing =
            //register(new BooleanSetting("Swing", false));
    protected final Setting<Boolean> hole =
            register(new BooleanSetting("HoleCheck", true));
    protected final Setting<Boolean> debug =
            register(new BooleanSetting("Debug", false));
    protected final Setting<Boolean> fullExtend =
            register(new BooleanSetting("FullExtend", true));
    protected final Setting<Boolean> extendxyz =
            register(new BooleanSetting("Extend-diag", false));
    protected final Setting<Float> range =
            register(new NumberSetting<>("Range", 6.0f, 0.0f, 10.0f));
    protected EntityPlayer target;

    protected final ModuleCache<Speedmine> speedmine = Caches.getModule(Speedmine.class);
    public forclown() {
        super("Blocker", Category.Combat);
        this.listeners.add(new ListenerBlockBreakAnim(this));
        this.listeners.add(new ListenerBlockChange(this));
        this.listeners.add(new ListenerUpdate(this));
        this.setData(new BlockerData(this));
    }

    Vec3i[] replaceList= new Vec3i[]{
            new Vec3i(1,0,0), //surround checks
            new Vec3i(-1,0,0),
            new Vec3i(0,0,1),
            new Vec3i(0,0,-1),

            new Vec3i(1,1,0), //diag cev checks
            new Vec3i(-1,1,0),
            new Vec3i(0,1,1),
            new Vec3i(0,1,-1)
    };

    protected void placeBlock(BlockPos pos){
        if (pos == null) return;
        if(mc.world==null)return;
        if(mc.player==null)return;
        if(mc.currentScreen instanceof GuiConnecting)return;
        target = EntityUtil.getClosestEnemy();
        if (pos == this.speedmine.get().getPos()) return;
        if(target == null ||pos.getDistance(target.getPosition().getX(), target.getPosition().getY(), target.getPosition().getZ()) >= this.range.getValue()) return;

        if (!mc.world.isAirBlock(pos)) return;

        int oldSlot = InventoryUtil.getServerItem();

        int obbySlot = InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN);
        int eChestSlot = InventoryUtil.findHotbarBlock(Blocks.ENDER_CHEST);

        if (obbySlot == -1 && eChestSlot == 1) return;

        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity instanceof EntityEnderCrystal) {
                NetworkUtil.send(new CPacketUseEntity(entity));
                NetworkUtil.send(new CPacketAnimation(EnumHand.MAIN_HAND));
            }
        }

        CooldownBypass.None.switchTo(obbySlot == -1 ? eChestSlot : obbySlot);

        InteractionUtil.placeBlock(pos, packet.getValue(), true);

        CooldownBypass.None.switchTo(oldSlot);

        if(debug.getValue()) mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(ChatFormatting.AQUA+ "placed at " + pos));
    }

    protected void scanAndPlace(BlockPos pos, boolean replace){
        if(mc.world==null)return;
        if(mc.player==null)return;
        if(mc.currentScreen instanceof GuiConnecting)return;
        if(hole.getValue() && !PlayerUtil.isInHoleAll(mc.player))
            return;

        BlockPos playerPos = PlayerUtil.getPlayerPos();

        //checking if we should care about the block in question
        for(Vec3i offset : replaceList){
            if(playerPos.add(offset).equals(pos)){
                break;
            }

            //if we are at our last element in the iterator and none of the elements suited us, abort the method
            if(offset.equals(replaceList[replaceList.length-1])){
                return;
            }
        }

        //if the block was broken, it should create a supporting block for extend to be placed at
        if(replace){
            ListenerUpdate.scheduledPlacements.add(pos);
        }


        for(EnumFacing face : EnumFacing.values()) {
            if(pos.offset(face).equals(playerPos)) continue;

            if(mc.world.isAirBlock(pos.offset(EnumFacing.DOWN))){
                ListenerUpdate.scheduledPlacements.add(pos.offset(EnumFacing.DOWN));
            }

            if(fullExtend.getValue()){
                if(pos.getY()==playerPos.getY()){
                    ListenerUpdate.scheduledPlacements.add(pos.offset(face));
                }else {
                    ListenerUpdate.scheduledPlacements.add(pos.add(0,1,0));
                }
            }else {
                if(playerPos.offset(face).equals(pos)){
                    if(this.extend.getValue()){
                        ListenerUpdate.scheduledPlacements.add(playerPos.offset(face).offset(face));
                    }

                    if(this.face.getValue()){
                        ListenerUpdate.scheduledPlacements.add(playerPos.offset(face).add(0,1,0));
                    }
                    if(this.extendxyz.getValue()){
                        ListenerUpdate.scheduledPlacements.add(playerPos.offset(face).offset(face.rotateYCCW()));
                        ListenerUpdate.scheduledPlacements.add(playerPos.offset(face).offset(face.rotateY()));
                    }
                }
            }
        }

    }
}
