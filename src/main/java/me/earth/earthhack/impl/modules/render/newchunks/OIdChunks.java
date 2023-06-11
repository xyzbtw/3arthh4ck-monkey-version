package me.earth.earthhack.impl.modules.render.newchunks;

import io.netty.util.internal.ConcurrentSet;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.events.render.WorldRenderEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.modules.render.newchunks.util.ChunkData;
import me.earth.earthhack.impl.util.client.ModuleUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ForgeClientHandler;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;

public class OIdChunks extends Module {

    public OIdChunks() {
        super("OldChunks", Category.Render);
        this.listeners.add(new LambdaListener<>(PacketEvent.Receive.class, e-> {
            if (e.getPacket() instanceof SPacketChunkData) {
                SPacketChunkData chunkPacket = (SPacketChunkData)((Object)e.getPacket());
                Point chunk = new Point(chunkPacket.getChunkX(), chunkPacket.getChunkZ());
                if (chunkPacket.isFullChunk()) {
                    if (!(newChunks.contains(chunk) || potentialOldChunks.contains(chunk) || oldChunks.contains(chunk))) {
                        unclassifiedChunks.add(chunk);
                        this.startClassificationDelay(chunk);
                    }
                } else if (!oldChunks.contains(chunk)) {
                    newChunks.add(chunk);
                    unclassifiedChunks.remove(chunk);
                    potentialOldChunks.remove(chunk);
                }
                allSeenChunks.add(chunk);
            }
        }));

    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e){
        if (renderUnclassified.getValue()) {
            this.drawChunkHighlights(unclassifiedChunks, Color.gray);
        }
        if (renderPotentialOld.getValue()) {
            this.drawChunkHighlights(potentialOldChunks, Color.orange);
        }
        if (renderOldChunks.getValue()) {
            this.drawChunkHighlights(oldChunks, Color.green);
        }
        if (renderNewChunks.getValue()) {
            this.drawChunkHighlights(newChunks, Color.red);
        }
        for (Point chunk : oldChunks) {
            int xw = chunk.x * 16;
            int yw = chunk.y * 16;
            if (debug.getValue()) {
                ModuleUtil.sendMessage(this, "found old chunk at " + xw + ", " + yw, "OldChunks");
            }
        }
    }

    private static Set<Point> unclassifiedChunks = new ConcurrentSet();
    private static Set<Point> newChunks = new ConcurrentSet();
    private static Set<Point> potentialOldChunks = new ConcurrentSet();
    private static Set<Point> oldChunks = new ConcurrentSet();
    private static Set<Point> allSeenChunks = new ConcurrentSet();
    public  Setting<Integer> renderDistance =
            register(new NumberSetting<>("RenderDistance", 1000, 0, 10000));
    public  Setting<Boolean> debug =
            register(new BooleanSetting("Debug", false));
    public  Setting<Integer> renderHeight =
            register(new NumberSetting<>("renderHeight", 5, -50, 256));
    public  Setting<Boolean> renderUnclassified =
            register(new BooleanSetting("renderUnclassified", false));
    public  Setting<Boolean> renderNewChunks =
            register(new BooleanSetting("NewChunks", false));
    public  Setting<Boolean> renderPotentialOld =
            register(new BooleanSetting("PotentialOldChunk", false));
    public  Setting<Boolean> renderOldChunks =
            register(new BooleanSetting("OldChunks", false));
    public  Setting<Integer> classificationDelay =
            register(new NumberSetting<>("classificationDelay", 5, 0, 100));
    public  Setting<Float> neighborScoreThreshold =
            register(new NumberSetting<>("neighborScoreThreshold", 2.5f, 0f, 100f));
    public  Setting<Integer> closeChunksDistance =
            register(new NumberSetting<>("closeChunksDistance", 32, 0, 10000));
    public  Setting<Integer> closeChunkThreshold =
            register(new NumberSetting<>("closeChunkThreshold", 10, 0, 100));
    public  Setting<Boolean> activateAlarm =
            register(new BooleanSetting("Alarm", false));
    Timer alarmTimer = null;
    TimerTask alarmTimerTask;


    @Override
    public void onEnable() {
        super.onEnable();
        MinecraftForge.EVENT_BUS.register(this);
        this.stopAlarm();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        MinecraftForge.EVENT_BUS.unregister(this);
        this.stopAlarm();
    }

    private void startClassificationDelay(final Point chunk) {
        new Timer().schedule(new TimerTask(){

            @Override
            public void run() {
                updateClassification(chunk, true);
            }
        }, (long)classificationDelay.getValue() * 1000L);
    }

    private void updateClassification(Point chunk, boolean internalTrigger) {
        int oldChunkState = this.getChunkState(chunk);
        if (oldChunkState > -1 && oldChunkState != 3) {
            unclassifiedChunks.remove(chunk);
            if (newChunks.contains(chunk) && !oldChunks.contains(chunk)) {
                potentialOldChunks.remove(chunk);
            } else if (this.hasOldNeighbor(chunk)) {
                oldChunks.add(chunk);
                potentialOldChunks.remove(chunk);
            } else {
                float score = this.calculateScore(chunk);
                if ((double)score >= neighborScoreThreshold.getValue()) {
                    oldChunks.add(chunk);
                    potentialOldChunks.remove(chunk);
                    this.checkIfStartAlarm();
                } else {
                    potentialOldChunks.add(chunk);
                }
            }
            int newChunkState = this.getChunkState(chunk);
            if (newChunkState != oldChunkState) {
                this.updateNeighbors(chunk);
            }
        }
    }

    private int getChunkState(Point chunk) {
        if (newChunks.contains(chunk)) {
            return 1;
        }
        if (oldChunks.contains(chunk)) {
            return 3;
        }
        if (potentialOldChunks.contains(chunk)) {
            return 2;
        }
        if (unclassifiedChunks.contains(chunk)) {
            return 0;
        }
        return -1;
    }

    private boolean hasOldNeighbor(Point chunk) {
        Point n = this.offset(chunk, 0, -1);
        Point e = this.offset(chunk, 1, 0);
        Point s = this.offset(chunk, 0, 1);
        Point w = this.offset(chunk, -1, 0);
        return oldChunks.contains(n) || oldChunks.contains(e) || oldChunks.contains(s) || oldChunks.contains(w);
    }

    private void updateNeighbors(Point chunk) {
        Point n = this.offset(chunk, 0, -1);
        Point e = this.offset(chunk, 1, 0);
        Point s = this.offset(chunk, 0, 1);
        Point w = this.offset(chunk, -1, 0);
        this.updateClassification(n, false);
        this.updateClassification(e, false);
        this.updateClassification(s, false);
        this.updateClassification(w, false);
    }

    private float calculateScore(Point chunk) {
        float score = 0.0f;
        Point n = this.offset(chunk, 0, -1);
        Point ne = this.offset(chunk, 1, -1);
        Point e = this.offset(chunk, 1, 0);
        Point se = this.offset(chunk, 1, 1);
        Point s = this.offset(chunk, 0, 1);
        Point sw = this.offset(chunk, -1, 1);
        Point w = this.offset(chunk, -1, 0);
        Point nw = this.offset(chunk, -1, -1);
        score += potentialOldChunks.contains(n) || oldChunks.contains(n) ? 1.0f : 0.0f;
        score += potentialOldChunks.contains(e) || oldChunks.contains(e) ? 1.0f : 0.0f;
        score += potentialOldChunks.contains(s) || oldChunks.contains(s) ? 1.0f : 0.0f;
        score = (float)((double)(score += potentialOldChunks.contains(w) || oldChunks.contains(w) ? 1.0f : 0.0f) + (potentialOldChunks.contains(ne) || oldChunks.contains(ne) ? 0.5 : 0.0));
        score = (float)((double)score + (potentialOldChunks.contains(se) || oldChunks.contains(se) ? 0.5 : 0.0));
        score = (float)((double)score + (potentialOldChunks.contains(sw) || oldChunks.contains(sw) ? 0.5 : 0.0));
        score = (float)((double)score + (potentialOldChunks.contains(nw) || oldChunks.contains(nw) ? 0.5 : 0.0));
        return score;
    }

    private Point offset(Point point, int deltaX, int deltaY) {
        return new Point(point.x + deltaX, point.y + deltaY);
    }

    private void checkIfStartAlarm() {
        if (!activateAlarm.getValue()) {
            return;
        }
        if (allSeenChunks.size() <= 100) {
            return;
        }
        int closeNewChunks = this.countCloseChunks(newChunks);
        if (closeNewChunks < 20) {
            return;
        }
        int closeOldChunks = this.countCloseChunks(oldChunks);
        if (closeOldChunks > closeChunkThreshold.getValue()) {
            return;
        }
        this.startAlarm();
    }

    private int countCloseChunks(Set<Point> chunks) {
        ConcurrentSet closeChunks = new ConcurrentSet();
        for (Point chunk : chunks) {
            int x = chunk.x * 16;
            int y = chunk.y * 16;
            if (mc.player.getDistance(x, mc.player.posY, y)>= closeChunksDistance.getValue() * 16) continue;
            closeChunks.add(chunk);
        }
        return closeChunks.size();
    }

    private synchronized void startAlarm() {
        if (this.alarmTimer == null) {
            this.alarmTimer = new Timer();
            this.alarmTimerTask = new TimerTask(){

                @Override
                public void run() {
                    try {
                        mc.world.playSound(mc.player.getPosition(), SoundEvents.BLOCK_NOTE_HARP, SoundCategory.MASTER, 100.0f, 0.94f, false);
                        mc.world.playSound(mc.player.getPosition(), SoundEvents.BLOCK_NOTE_BASS, SoundCategory.MASTER, 100.0f, 0.94f, false);
                        Thread.sleep(100L);
                        mc.world.playSound(mc.player.getPosition(), SoundEvents.BLOCK_NOTE_HARP, SoundCategory.MASTER, 100.0f, 0.94f, false);
                        mc.world.playSound(mc.player.getPosition(), SoundEvents.BLOCK_NOTE_BASS, SoundCategory.MASTER, 100.0f, 0.94f, false);
                        Thread.sleep(100L);
                        mc.world.playSound(mc.player.getPosition(), SoundEvents.BLOCK_NOTE_HARP, SoundCategory.MASTER, 100.0f, 0.94f, false);
                        mc.world.playSound(mc.player.getPosition(), SoundEvents.BLOCK_NOTE_BASS, SoundCategory.MASTER, 100.0f, 0.94f, false);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            this.alarmTimer.scheduleAtFixedRate(this.alarmTimerTask, 0L, 800L);
        }
    }

    private synchronized void stopAlarm() {
        if (this.alarmTimer != null) {
            this.alarmTimer.cancel();
            this.alarmTimer = null;
        }
    }


    private void drawChunkHighlights(Set<Point> chunks, Color color) {

        for (Point chunk : chunks) {
            int x = chunk.x * 16;
            int y = chunk.y * 16;
            if(mc.player.getDistance(x, mc.player.posY, y) >= renderDistance.getValue()) continue;
            RenderUtil.draw2DRec(RenderUtil.getBB(new BlockPos(x, renderHeight.getValue(), y), 16), 1.0f, (float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, 1.0f);
        }
    }


    public class oldchunkalert extends TimerTask{
        final Point val$chunk;
        oldchunkalert(Point point) {
            this.val$chunk = point;
        }

        @Override
        public void run() {
            updateClassification(this.val$chunk, true);
        }
    }

}
