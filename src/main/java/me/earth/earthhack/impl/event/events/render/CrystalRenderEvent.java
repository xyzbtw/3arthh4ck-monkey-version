package me.earth.earthhack.impl.event.events.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.events.Event;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.crystalchams.CrystalChams;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;

public class CrystalRenderEvent extends Event
{

    private final RenderEnderCrystal render;
    private final Entity entity;
    private final ModelBase model;
    private static final ModuleCache<CrystalChams> CHAMS =
            Caches.getModule(CrystalChams.class);

    private CrystalRenderEvent(RenderEnderCrystal render,
                               Entity entity,
                               ModelBase model)
    {
        this.render = render;
        this.entity = entity;
        this.model  = model;
    }

    public RenderEnderCrystal getRender()
    {
        return render;
    }

    public Entity getEntity()
    {
        return entity;
    }

    public ModelBase getModel()
    {
        return model;
    }

    public static class Pre extends CrystalRenderEvent
    {
        private final float limbSwing;
        private final float limbSwingAmount;
        private final float ageInTicks;
        private final float netHeadYaw;
        private final float headPitch;
        private final float scale;

        public Pre(RenderEnderCrystal render,
                   Entity entity,
                   ModelBase model,
                   float limbSwing,
                   float limbSwingAmount,
                   float ageInTicks,
                   float netHeadYaw,
                   float headPitch,
                   float scale)
        {
            super(render, entity, model);
            this.limbSwing       = limbSwing;
            this.limbSwingAmount = CHAMS.get().modify.getValue() ? CHAMS.get().spinnerspeed.getValue()*limbSwingAmount : limbSwingAmount;
            this.ageInTicks      = CHAMS.get().modify.getValue() ? CHAMS.get().bouncespeed.getValue()*ageInTicks : ageInTicks;
            this.netHeadYaw      = netHeadYaw;
            this.headPitch       = headPitch;
            this.scale           = scale;
        }

        public float getLimbSwing()
        {
            return limbSwing;
        }

        public float getLimbSwingAmount()
        {
            return limbSwingAmount;
        }

        public float getAgeInTicks()
        {
            return ageInTicks;
        }

        public float getNetHeadYaw()
        {
            return netHeadYaw;
        }

        public float getHeadPitch()
        {
            return headPitch;
        }

        public float getScale()
        {
            return scale;
        }
    }

    public static class Post extends CrystalRenderEvent
    {
        private final float limbSwing;
        private final float limbSwingAmount;
        private final float ageInTicks;
        private final float netHeadYaw;
        private final float headPitch;
        private final float scale;

        public Post(RenderEnderCrystal render,
                    Entity entity,
                    ModelBase model,
                    float limbSwing,
                    float limbSwingAmount,
                    float ageInTicks,
                    float netHeadYaw,
                    float headPitch,
                    float scale)
        {
            super(render, entity, model);
            this.limbSwing       = limbSwing;
            this.limbSwingAmount = CHAMS.get().modify.getValue() ? CHAMS.get().spinnerspeed.getValue()*limbSwingAmount : limbSwingAmount;
            this.ageInTicks      = CHAMS.get().modify.getValue() ? CHAMS.get().bouncespeed.getValue()*ageInTicks : ageInTicks;
            this.netHeadYaw      = netHeadYaw;
            this.headPitch       = headPitch;
            this.scale           = scale;
        }

        public float getLimbSwing()
        {
            return limbSwing;
        }

        public float getLimbSwingAmount()
        {
            return limbSwingAmount;
        }

        public float getAgeInTicks()
        {
            return ageInTicks;
        }

        public float getNetHeadYaw()
        {
            return netHeadYaw;
        }

        public float getHeadPitch()
        {
            return headPitch;
        }

        public float getScale()
        {
            return scale;
        }
    }

}
