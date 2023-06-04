/*(package me.earth.earthhack.impl.modules.render.molochhole;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.thread.holes.HoleObserver;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

public class HoleRender extends Module implements HoleObserver  {

   protected Setting<Page> page = register(new EnumSetting<>( "Page", Page.Render));

    protected Setting<Boolean> rollingHeight = register(new BooleanSetting("RollingHeight", false));
    protected Setting<Float> rollingSpeed = register(new NumberSetting<>("RollingSpeed", 1.0f, 0.1f, 10.0f));
    protected   Setting<Float> rollingWidth = register(new NumberSetting<>("RollWidth", 0.4f, 0.0f, 2.0f));
    protected  Setting<Float> rollingHeightMax = register(new NumberSetting<>("RollHeightMax", 1.0f, -2.0f, 2.0f));
    protected  Setting<Float> rollingHeightMin = register(new NumberSetting<>("RollHeightMin", 0.1f, -2.0f, 2.0f));
    public Setting<Boolean> doubleHoles =register(new BooleanSetting("DoubleHoles", true));
    public Setting<Boolean> mergeDoubleHoles = register(new BooleanSetting("MergeDoubleHoles", false));
    protected Setting<SelfHighlightMode> selfHighlight = register(new EnumSetting<>("SelfHighlight", SelfHighlightMode.None));
    protected   Setting<Float> selfHightlightAlphaFactor = register(new NumberSetting<>("SelfHighlightAlphaFactor", 0.5f, 0.0f, 10.0f));
    protected Setting<Float> selfHightlightHeight = register(new NumberSetting<>("SelfHighlightHeight", 0.2f, -2.0f, 2.0f));
    protected  Setting<Boolean> fadeIn = register(new BooleanSetting("FadeIn", false));
    protected Setting<Float> fadeInRange = register(new NumberSetting<>("FadeRange", 7.0f, 0.1f, 10.0f));
    protected  Setting<Boolean> xCross = register(new BooleanSetting("BoxCross", false));
    protected Setting<Boolean> flatXCross = register(new BooleanSetting("FlatBoxCross", false));
    protected Setting<Boolean> solidBox = register(new BooleanSetting("SolidBox", true));
    protected  Setting<Boolean> solidPyramid = register(new BooleanSetting("SolidPyramid", false));
    protected Setting<Boolean> solidCull = register(new BooleanSetting("SolidCull", false));
    protected   Setting<Boolean> wallSolid = register(new BooleanSetting("WallSolid", true));
    protected Setting<Boolean> sidesOnly = register(new BooleanSetting("SidesOnly", false));
    protected  Setting<Boolean> flatSolidBox = register(new BooleanSetting("FlatSolidBox", false));
    protected  Setting<Boolean> linesBox = register(new BooleanSetting("LinesBox", true));
    protected  Setting<Boolean> linesPyramid = register(new BooleanSetting("LinesPyramid", false));
    protected  Setting<Boolean> wallLines = register(new BooleanSetting("WallLines", true));
    protected Setting<Boolean> flatLinesBox = register(new BooleanSetting("FlatLinesBox", false));
    protected Setting<Boolean> boxOneBlockHeight = register(new BooleanSetting("OneBlockHeight", false));
    protected   Setting<Float> boxHeight = register(new NumberSetting<>("Height", 1.0f, -2.0f, 2.0f));
    protected Setting<Float> lineWidth = register(new NumberSetting<>("LineWidth", 1.0f, 0.0f, 5.0f));
    protected Setting<Boolean> flipPyramids = register(new BooleanSetting("FlipPyramids", false));

   protected Setting<Color> singleSafeColorSolid = register(new ColorSetting("SingleSafeColorSolid",
            new Color(50, 255, 50, 20)));
    protected Setting<Color> singleSafeColorLines = register(new ColorSetting("SingleSafeColorLines",
            new Color(50, 255, 50, 20)));

    protected Setting<Color> singleUnSafeColorSolid = register(new ColorSetting("SingleUnSafeColorSolid",
            new Color(50, 255, 50, 20)));

    protected Setting<Color> singleUnSafeColorLines = register(new ColorSetting("SingleUnSafeColorLines",
           new Color(50, 255, 50, 20)));

    protected Setting<Color> doubleSafeColorSolid = register(new ColorSetting("DoubleSafeColorSolid",
            new Color(50, 255, 50, 20)));

    protected Setting<Color> doubleSafeColorLines = register(new ColorSetting("DoubleSafeColorLines",
            new Color(50, 255, 50, 20)));

    protected Setting<Color> doubleUnSafeColorSolid = register(new ColorSetting("DoubleUnSafeColorSolid",
          new Color(50, 255, 50, 20)));

    protected Setting<Color> doubleUnSafeColorLines = register(new ColorSetting("DoubleUnSafeColorLines",
         new Color(50, 255, 50, 20)));


    public static HoleRender INSTANCE;


    public HoleRender() {
        super("MolochHole", Category.Render);
    }


    @Override
    public String getModuleInfo() {
        if (solidBox.getValue() && linesBox.getValue()) return "Full";
        else if (solidBox.getValue()) return "Solid";
        else if (linesBox.getValue()) return "Outline";
        else return "Ok";
    }

    @Override
    public void onRenderWorld(RenderEvent event) {
        renderHoles(false, wallSolid.getValue(), wallLines.getValue());
    }

    @Listener
    public void renderEventOnBottom(RenderEvent.AlwaysOnBottom event) {
        if (HeldModelTweaks.INSTANCE.render3dOnTop.getValue()
                && (!wallSolid.getValue() || !wallLines.getValue()))
            renderHoles(true, wallSolid.getValue(), wallLines.getValue());
    }

    private void renderHoles(boolean depthPass, boolean wallSolid, boolean wallLines) {
        HashMap<BlockPos, Integer> localHolePositions = new HashMap<>(HoleSettings.INSTANCE.holePositions);
        if (doubleHoles.getValue() && !mergeDoubleHoles.getValue()) {
            localHolePositions.putAll(HoleSettings.INSTANCE.doubleHolePositions);
        }

        localHolePositions.entrySet().stream()
                .filter(entry -> SpartanTessellator.isInViewFrustrum(SpartanTessellator.getBoundingFromPos(entry.getKey())))
                .forEach(entry -> {
                    AtomicInteger solidColor = new AtomicInteger();
                    AtomicInteger linesColor = new AtomicInteger();
                    AtomicInteger solidColor2 = new AtomicInteger();
                    AtomicInteger linesColor2 = new AtomicInteger();
                    boolean isInHole = BlockUtil.isSameBlockPos(EntityUtil.floorEntity(mc.player), entry.getKey());
                    float height = boxOneBlockHeight.getValue() ? 1.0f : boxHeight.getValue();
                    Vec3d holeVec = new Vec3d(entry.getKey());

                    float alphaFactor = 1.0f;
                    if (fadeIn.getValue()) {
                        float f = HoleSettings.INSTANCE.range.getValue() - fadeInRange.getValue() * 0.5f;
                        alphaFactor = (MathUtil.clamp((HoleSettings.INSTANCE.range.getValue() * HoleSettings.INSTANCE.range.getValue() - (float) MathUtil.getDistSq(mc.player.getPositionVector(), holeVec)) / (f * f), 0.0f, 1.0f));
                    }

                    if (rollingHeight.getValue()) {
                        height = getRolledHeight(entry.getKey().x);
                    }

                    if (selfHighlight.getValue() == SelfHighlightMode.Alpha && isInHole) {
                        alphaFactor *= selfHightlightAlphaFactor.getValue();
                    }

                    if (selfHighlight.getValue() == SelfHighlightMode.Height && isInHole) {
                        height = selfHightlightHeight.getValue();
                    }

                    sortColors(entry.getKey(), entry.getValue(), alphaFactor, height,
                            solidColor, solidColor2, linesColor, linesColor2);

                    if (solidBox.getValue() && depthPass != wallSolid) {
                        if (!wallSolid) GL11.glEnable(GL_DEPTH_TEST);

                        if (flatSolidBox.getValue()) {
                            SpartanTessellator.drawFlatFullBox(holeVec, !wallSolid, solidColor.get());
                        } else {
                            if (solidPyramid.getValue()) {
                                SpartanTessellator.drawGradientPyramidFullBox(holeVec, !solidCull.getValue(), flipPyramids.getValue(), !wallSolid, height, solidColor.get(), solidColor2.get());
                            } else {
                                SpartanTessellator.drawGradientBlockFullBox(holeVec, !solidCull.getValue(), !wallSolid, sidesOnly.getValue(), height, solidColor.get(), solidColor2.get());
                            }
                        }

                        if (!wallSolid) GL11.glDisable(GL_DEPTH_TEST);
                    }

                    if (linesBox.getValue() && depthPass != wallLines) {
                        if (!wallLines) GL11.glEnable(GL_DEPTH_TEST);

                        if (flatLinesBox.getValue()) {
                            SpartanTessellator.drawFlatLineBox(holeVec, !wallLines, lineWidth.getValue(), linesColor.get());
                        } else {
                            if (linesPyramid.getValue()) {
                                SpartanTessellator.drawGradientPyramidLineBox(holeVec, flipPyramids.getValue(), !wallLines, height, lineWidth.getValue(), linesColor.get(), linesColor2.get());
                            } else {
                                SpartanTessellator.drawGradientBlockLineBox(holeVec, !wallLines, height, lineWidth.getValue(), linesColor.get(), linesColor2.get());
                            }
                        }

                        if (!wallLines) GL11.glDisable(GL_DEPTH_TEST);
                    }

                    if (xCross.getValue() && depthPass != wallLines) {
                        if (!wallLines) GL11.glEnable(GL_DEPTH_TEST);

                        if (flatXCross.getValue() || (linesBox.getValue() && linesPyramid.getValue()) || (solidBox.getValue() && solidPyramid.getValue())) {
                            SpartanTessellator.drawFlatXCross(holeVec.add(0.0, wallLines ? 0.0 : 0.003, 0.0), lineWidth.getValue(), linesColor.get());
                        } else {
                            SpartanTessellator.drawGradientXCross(holeVec, height, lineWidth.getValue(), linesColor.get(), linesColor2.get());
                        }

                        if (!wallLines) GL11.glDisable(GL_DEPTH_TEST);
                    }
                });

        if (mergeDoubleHoles.getValue()) {
            HoleSettings.INSTANCE.mergedHolePositions.entrySet().stream()
                    .filter(entry -> SpartanTessellator.isInViewFrustrum(SpartanTessellator.getBoundingFromPos(entry.getValue().a))
                            || SpartanTessellator.isInViewFrustrum(SpartanTessellator.getBoundingFromPos(entry.getValue().b)))
                    .forEach(entry -> {
                        AtomicInteger solidColor = new AtomicInteger();
                        AtomicInteger linesColor = new AtomicInteger();
                        AtomicInteger solidColor2 = new AtomicInteger();
                        AtomicInteger linesColor2 = new AtomicInteger();
                        BlockPos playerPos = PlayerUtil.getPlayerPos();
                        boolean isInHole = BlockUtil.isSameBlockPos(playerPos, entry.getValue().a) || BlockUtil.isSameBlockPos(playerPos, entry.getValue().b);
                        float height = boxHeight.getValue();
                        if (boxOneBlockHeight.getValue()) height = 1.0f;
                        boolean flagx = false;
                        boolean flagz = false;

                        if (rollingHeight.getValue()) {
                            height = getRolledHeight((float) (((entry.getValue().a.x == entry.getValue().b.x + 1.0 ? entry.getValue().a.x + 0.5 : entry.getValue().a.x - 0.5)
                                    + (entry.getValue().a.x == entry.getValue().b.x + 1.0 ? entry.getValue().b.x - 0.5 : entry.getValue().b.x + 0.5))
                                    / 2.0));
                        }

                        if (selfHighlight.getValue() == SelfHighlightMode.Height && isInHole) {
                            height = selfHightlightHeight.getValue();
                        }

                        Vec3d holeVec1 = new Vec3d(entry.getValue().a.x - 0.5, entry.getValue().a.y, entry.getValue().a.z - 0.5);
                        Vec3d holeVec2 = new Vec3d(entry.getValue().b.x + 0.5, entry.getValue().b.y + height, entry.getValue().b.z + 0.5);

                        if (entry.getValue().a.x == entry.getValue().b.x + 1.0) {
                            flagx = true;
                            holeVec1 = new Vec3d(entry.getValue().a.x + 0.5, entry.getValue().a.y, entry.getValue().a.z - 0.5);
                            holeVec2 = new Vec3d(entry.getValue().b.x - 0.5, entry.getValue().b.y + height, entry.getValue().b.z + 0.5);
                        }

                        if (entry.getValue().a.z == entry.getValue().b.z + 1.0) {
                            flagz = true;
                            holeVec1 = new Vec3d(entry.getValue().a.x - 0.5, entry.getValue().a.y, entry.getValue().a.z + 0.5);
                            holeVec2 = new Vec3d(entry.getValue().b.x + 0.5, entry.getValue().b.y + height, entry.getValue().b.z - 0.5);
                        }
                        Vec3d centerVec = new Vec3d((holeVec1.x + holeVec2.x) / 2.0, holeVec1.y, (holeVec1.z + holeVec2.z) / 2.0);

                        float alphaFactor = 1.0f;
                        if (fadeIn.getValue()) {
                            float f = HoleSettings.INSTANCE.range.getValue() - fadeInRange.getValue() * 0.5f;
                            alphaFactor = (MathUtil.clamp(
                                    (range.getValue() * HoleSettings.INSTANCE.range.getValue() - (float) MathUtil.getDistSq(mc.player.getPositionVector(), centerVec))
                                            / (f * f), 0.0f, 1.0f));
                        }

                        if (selfHighlight.getValue() == SelfHighlightMode.Alpha && isInHole) {
                            alphaFactor *= selfHightlightAlphaFactor.getValue();
                        }

                        sortColors(new BlockPos(centerVec), entry.getValue().c, alphaFactor, height,
                                solidColor, solidColor2, linesColor, linesColor2);

                        if (solidBox.getValue() && depthPass != wallSolid) {
                            if (!wallSolid) GL11.glEnable(GL_DEPTH_TEST);

                            if (flatSolidBox.getValue()) {
                                SpartanTessellator.drawDoubleBlockFlatFullBox(holeVec1, holeVec2, !wallSolid, solidColor.get());
                            } else {
                                if (solidPyramid.getValue()) {
                                    SpartanTessellator.drawGradientDoubleBlockFullPyramid(holeVec1, holeVec2, !solidCull.getValue(), flipPyramids.getValue(), !wallSolid, flagx, flagz, solidColor.get(), solidColor2.get());
                                } else {
                                    SpartanTessellator.drawGradientDoubleBlockFullBox(holeVec1, holeVec2, !solidCull.getValue(), !wallSolid, sidesOnly.getValue(), solidColor.get(), solidColor2.get());
                                }
                            }

                            if (!wallSolid) GL11.glDisable(GL_DEPTH_TEST);
                        }

                        if (linesBox.getValue() && depthPass != wallLines) {
                            if (!wallLines) GL11.glEnable(GL_DEPTH_TEST);

                            if (flatLinesBox.getValue()) {
                                SpartanTessellator.drawDoubleBlockFlatLineBox(holeVec1, holeVec2, !wallLines, lineWidth.getValue(), linesColor.get());
                            } else {
                                if (linesPyramid.getValue()) {
                                    SpartanTessellator.drawGradientDoubleBlockLinePyramid(holeVec1, holeVec2, flipPyramids.getValue(), !wallLines, lineWidth.getValue(), flagx, flagz, linesColor.get(), linesColor2.get());
                                } else {
                                    SpartanTessellator.drawGradientDoubleBlockLineBox(holeVec1, holeVec2, !wallLines, lineWidth.getValue(), linesColor.get(), linesColor2.get());
                                }
                            }

                            if (!wallLines) GL11.glDisable(GL_DEPTH_TEST);
                        }

                        if (xCross.getValue() && depthPass != wallLines) {
                            if (!wallLines) GL11.glEnable(GL_DEPTH_TEST);

                            if (flatXCross.getValue() || (linesBox.getValue() && linesPyramid.getValue()) || (solidBox.getValue() && solidPyramid.getValue())) {
                                SpartanTessellator.drawDoublePointFlatXCross(holeVec1.add(0.0, wallLines ? 0.0 : 0.003, 0.0), holeVec2.add(0.0, wallLines ? 0.0 : 0.003, 0.0), lineWidth.getValue(), linesColor.get());
                            } else {
                                SpartanTessellator.drawGradientDoublePointXCross(holeVec1, holeVec2, lineWidth.getValue(), linesColor.get(), linesColor2.get());
                            }

                            if (!wallLines) GL11.glDisable(GL_DEPTH_TEST);
                        }
                    });
        }
    }

    private float getRolledHeight(float offset) {
        double s = (System.currentTimeMillis() * (double)rollingSpeed.getValue() * 0.1) + (offset * rollingWidth.getValue() * 100.0f);
        s %= 300.0;
        s = (150.0f * Math.sin(((s - 75.0f) * Math.PI) / 150.0f)) + 150.0f;
        return rollingHeightMax.getValue() + ((float)s * ((rollingHeightMin.getValue() - rollingHeightMax.getValue()) / 300.0f));
    }

    private void sortColors(BlockPos pos, int type, float alphaFactor, float height,
                            AtomicInteger solidColor, AtomicInteger solidColor2, AtomicInteger linesColor, AtomicInteger linesColor2) {
        switch (type) {
            case 1: {
                singleSafeColorSolid.getValue().multiplyAlpha(MathUtil.clamp(alphaFactor, 0.0f, 1.0f));
                solidColor.set(singleSafeColorSolid.getValue().getColor(height + singleSafeColorSolid.getValue().getRollingOffset() * height, pos.x, height));
                solidColor2.set(singleSafeColorSolid.getValue().getColor(singleSafeColorSolid.getValue().getRollingOffset() * height , pos.x, height));

                singleSafeColorLines.getValue().multiplyAlpha(MathUtil.clamp(alphaFactor, 0.0f, 1.0f));
                linesColor.set(singleSafeColorLines.getValue().getColor(height + singleSafeColorLines.getValue().getRollingOffset() * height, pos.x, height));
                linesColor2.set(singleSafeColorLines.getValue().getColor(singleSafeColorLines.getValue().getRollingOffset() * height , pos.x, height));
                break;
            }

            case 2: {
                singleUnSafeColorSolid.getValue().multiplyAlpha(MathUtil.clamp(alphaFactor, 0.0f, 1.0f));
                solidColor.set(singleUnSafeColorSolid.getValue().getColor(height + singleUnSafeColorSolid.getValue().getRollingOffset() * height, pos.x, height));
                solidColor2.set(singleUnSafeColorSolid.getValue().getColor(singleUnSafeColorSolid.getValue().getRollingOffset() * height , pos.x, height));

                singleUnSafeColorLines.getValue().multiplyAlpha(MathUtil.clamp(alphaFactor, 0.0f, 1.0f));
                linesColor.set(singleUnSafeColorLines.getValue().getColor(height + singleUnSafeColorLines.getValue().getRollingOffset() * height, pos.x, height));
                linesColor2.set(singleUnSafeColorLines.getValue().getColor(singleUnSafeColorLines.getValue().getRollingOffset() * height , pos.x, height));
                break;
            }

            case 3: {
                doubleSafeColorSolid.getValue().multiplyAlpha(MathUtil.clamp(alphaFactor, 0.0f, 1.0f));
                solidColor.set(doubleSafeColorSolid.getValue().getColor(height + doubleSafeColorSolid.getValue().getRollingOffset() * height, pos.x, height));
                solidColor2.set(doubleSafeColorSolid.getValue().getColor(doubleSafeColorSolid.getValue().getRollingOffset() * height , pos.x, height));

                doubleSafeColorLines.getValue().multiplyAlpha(MathUtil.clamp(alphaFactor, 0.0f, 1.0f));
                linesColor.set(doubleSafeColorLines.getValue().getColor(height + doubleSafeColorLines.getValue().getRollingOffset() * height, pos.x, height));
                linesColor2.set(doubleSafeColorLines.getValue().getColor(doubleSafeColorLines.getValue().getRollingOffset() * height, pos.x, height));
                break;
            }

            default: {
                doubleUnSafeColorSolid.getValue().multiplyAlpha(MathUtil.clamp(alphaFactor, 0.0f, 1.0f));
                solidColor.set(doubleUnSafeColorSolid.getValue().getColor(height + doubleUnSafeColorSolid.getValue().getRollingOffset() * height, pos.x, height));
                solidColor2.set(doubleUnSafeColorSolid.getValue().getColor(doubleUnSafeColorSolid.getValue().getRollingOffset() * height, pos.x, height));

                doubleUnSafeColorLines.getValue().multiplyAlpha(MathUtil.clamp(alphaFactor, 0.0f, 1.0f));
                linesColor.set(doubleUnSafeColorLines.getValue().getColor(height + doubleUnSafeColorLines.getValue().getRollingOffset() * height, pos.x, height));
                linesColor2.set(doubleUnSafeColorLines.getValue().getColor(doubleUnSafeColorLines.getValue().getRollingOffset() * height, pos.x, height));
                break;
            }
        }
    }

    @Override
    public double getRange() {
        return 0;
    }

    @Override
    public int getSafeHoles() {
        return 0;
    }

    @Override
    public int getUnsafeHoles() {
        return 0;
    }

    @Override
    public int get2x1Holes() {
        return 0;
    }

    @Override
    public int get2x2Holes() {
        return 0;
    }

    enum Page {
        Render,
        Color
    }

    enum SelfHighlightMode {
        Alpha,
        Height,
        None
    }
}

 */
