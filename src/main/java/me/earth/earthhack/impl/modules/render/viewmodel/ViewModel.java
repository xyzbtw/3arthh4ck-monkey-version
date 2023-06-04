package me.earth.earthhack.impl.modules.render.viewmodel;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.mixins.render.MixinItemRenderer;
import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import net.minecraft.util.EnumHand;

/**
 * {@link MixinItemRenderer}.
 */
public class ViewModel extends Module
{
    public static final float[] DEFAULT_SCALE =
            new float[]{1.0f, 1.0f, 1.0f};
    public static final float[] DEFAULT_TRANSLATION =
            new float[]{0.0f, 0.0f, 0.0f, 0.0f};
    public static final float[] DEFAULT_ROTATIONS =
            new float[]{0.0f, 0.0f, 0.0f, 0.0f};
    public final BooleanSetting noSway =
            register(new BooleanSetting("No-Sway", false));
    protected final Setting<Float> offX  =
            register(new NumberSetting<>("OffHand-X", 0.0f, -10.0f, 10.0f));
    protected final Setting<Float> offY  =
            register(new NumberSetting<>("OffHand-Y", 0.0f, -10.0f, 10.0f));
    protected final Setting<Float> mainX =
            register(new NumberSetting<>("MainHand-X", 0.0f, -10.0f, 10.0f));
    protected final Setting<Float> mainY =
            register(new NumberSetting<>("MainHand-Y", 0.0f, -10.0f, 10.0f));
    protected final Setting<Float> xScale =
            register(new NumberSetting<>("X-Scale", 1.0f, 0.0f, 10.0f));
    protected final Setting<Float> yScale =
            register(new NumberSetting<>("Y-Scale", 1.0f, 0.0f, 10.0f));
    protected final Setting<Float> zScale =
            register(new NumberSetting<>("Z-Scale", 1.0f, 0.0f, 10.0f));
    protected final Setting<Float> angleTranslate =
            register(new NumberSetting<>("Angle-Translate", 0.0f, -360.0f, 360.0f));
    protected final Setting<Float> xTranslate =
            register(new NumberSetting<>("X-Translate", 1.0f, -10.0f, 10.0f));
    protected final Setting<Float> yTranslate =
            register(new NumberSetting<>("Y-Translate", 1.0f, -10.0f, 10.0f));
    protected final Setting<Float> zTranslate =
            register(new NumberSetting<>("Z-Translate", 1.0f, -10.0f, 10.0f));
    protected final Setting<Float> rotatex =
            register(new NumberSetting<>("Rotate-X", 1.0f, 0.0f, 360.0f));
    protected final Setting<Float> rotatey =
            register(new NumberSetting<>("Rotate-Y", 1.0f, 0.0f, 360.0f));
    protected final Setting<Float> rotatez =
            register(new NumberSetting<>("Rotate-Z", 1.0f, 0.0f, 360.0f));
    protected final Setting<Float> animationSpeed =
            register(new NumberSetting<>("NnimationSpeed", 1.0f, 0.1f, 10.0f));
    public final BooleanSetting animation =
            register(new BooleanSetting("Animations", false));

    public ViewModel()
    {
        super("ViewModel", Category.Render);
        this.setData(new ViewModelData(this));
        this.listeners.add(new LambdaListener<>(UpdateEvent.class, e ->{

            if(this.animation.getValue()){
                rotatex.setValue((rotatex.getValue() - animationSpeed.getValue()) % 360.0f);
                if (rotatex.getValue() <= 0.0f) {
                    rotatex.setValue(rotatex.getValue() + 360.0f);
                }
                rotatey.setValue((rotatey.getValue() - animationSpeed.getValue()) % 360.0f);
                if (rotatey.getValue() <= 0.0f) {
                    rotatey.setValue(rotatey.getValue() + 360.0f);
                }
                rotatez.setValue((rotatez.getValue() - animationSpeed.getValue()) % 360.0f);
                if (rotatez.getValue() <= 0.0f) {
                    rotatez.setValue(rotatez.getValue() + 360.0f);
                }
            }

        } ));
    }

    public float getX(EnumHand hand)
    {
        if (!this.isEnabled())
        {
            return 0.0f;
        }

        return hand == EnumHand.MAIN_HAND ? mainX.getValue() : offX.getValue();
    }

    public float getY(EnumHand hand)
    {
        if (!this.isEnabled())
        {
            return 0.0f;
        }

        return hand == EnumHand.MAIN_HAND ? mainY.getValue() : offY.getValue();
    }

    public float[] getScale()
    {
        if (!this.isEnabled())
        {
            return DEFAULT_SCALE;
        }

        return new float[]
                {xScale.getValue(), yScale.getValue(), zScale.getValue()};
    }

    public float[] getTranslation()
    {
        if (!this.isEnabled())
        {
            return DEFAULT_TRANSLATION;
        }

        return new float[]
            {angleTranslate.getValue(), xTranslate.getValue(), yTranslate.getValue(), zTranslate.getValue()};
    }

    public float[] getRotations() {
        if (this.isEnabled()) {
            return DEFAULT_ROTATIONS;
        }
        return new float[]
                {
                    rotatey.getValue(), rotatex.getValue(), rotatez.getValue()
                };
    }

}
