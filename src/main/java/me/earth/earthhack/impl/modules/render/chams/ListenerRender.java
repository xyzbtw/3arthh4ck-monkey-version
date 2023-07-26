package me.earth.earthhack.impl.modules.render.chams;

import me.earth.earthhack.impl.core.ducks.render.IRenderManager;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.render.chams.mode.ChamsMode;
import me.earth.earthhack.impl.modules.render.esp.ESP;
import me.earth.earthhack.impl.modules.render.nametags.Nametag;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

import java.awt.*;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

public class ListenerRender
        extends ModuleListener<Chams, Render3DEvent>
{

    public ListenerRender(Chams module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        // TODO: read stenciling for army and people who can't use shaders
        if(module.mode.getValue() == ChamsMode.ShaderFill){
            module.runPreFill();
            module.runPostFill();
        }
        if (false)
        {
            if (true) return;
            if (module.imageShader != null)
            {
                module.imageShader.bind();
                module.imageShader.set("overlaySampler", 0);
                glBindTexture(GL_TEXTURE_2D, mc.getTextureManager().getTexture(Chams.GALAXY_LOCATION).getGlTextureId());
                Render2DUtil.drawRect(0, 0, mc.displayWidth, mc.displayHeight, Color.WHITE.getRGB());
                module.imageShader.unbind();
            }
            if (true) return;
            double renderPosX = ((IRenderManager) mc.getRenderManager()).getRenderPosX();
            double renderPosY = ((IRenderManager) mc.getRenderManager()).getRenderPosY();
            double renderPosZ = ((IRenderManager) mc.getRenderManager()).getRenderPosZ();

            // module.checkSetupFBO();
            glPushAttrib(GL_ALL_ATTRIB_BITS);
            glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // color white
            boolean renderShadows = mc.gameSettings.entityShadows;
            mc.gameSettings.entityShadows = false;
            module.force = true;
            module.renderLayers = false;
            module.renderModels = true;
            if (module.imageShader != null)
            {
                module.imageShader.bind();
                // module.imageShader.set("sampler", 0);
                glBindTexture(GL_TEXTURE_2D, mc.getTextureManager().getTexture(Chams.GALAXY_LOCATION).getGlTextureId());
                module.imageShader.set("overlaySampler", 1);
                glDisable(GL_TEXTURE_2D);
                renderEntities(renderPosX, renderPosY, renderPosZ);
                module.imageShader.unbind();
            }
            module.renderLayers = true;
            module.renderModels = false;
            module.force = false;
            mc.gameSettings.entityShadows = renderShadows;

            renderEntities(renderPosX, renderPosY, renderPosZ);
            /*glEnable(GL_ALPHA_TEST);
            glAlphaFunc(GL_GEQUAL, 0.1f);*/
            // glDisable(GL_ALPHA_TEST);

            // render the held stuff, but use a shader with the discord keyword to discord low alpha fragments, fixing our depth issues! genius!
            /*glEnable(GL_TEXTURE_2D);
            if (OpenGlHelper.areShadersSupported()
                    && module.alphaShader != null)
            {
                // glEnable(GL_ALPHA_TEST);
                // glAlphaFunc(GL_LEQUAL, 0.5f);
                module.alphaShader.bind();
                module.alphaShader.set("texture", 0);
                glStencilFunc(GL_ALWAYS, 0, 0xFF);
                renderEntities(renderPosX, renderPosY, renderPosZ);
                module.alphaShader.unbind();
                // glDisable(GL_ALPHA_TEST);
            }*/

            // TODO: fragment shader to discard ^^^

            // renderEntities(renderPosX, renderPosY, renderPosZ);
            /*if (!OpenGlHelper.areShadersSupported()
                    || module.alphaShader == null)
            {

            }*/
        }
    }

    private void renderEntities(double renderPosX, double renderPosY, double renderPosZ) {
        for (Entity e : mc.world.loadedEntityList) {
            if (!module.isValid(e)) continue;
            if (e == mc.getRenderViewEntity()) continue;
            if (e.ticksExisted == 0) {
                e.lastTickPosX = e.posX;
                e.lastTickPosY = e.posY;
                e.lastTickPosZ = e.posZ;
            }

            double d0 = e.lastTickPosX + (e.posX - e.lastTickPosX) * mc.getRenderPartialTicks();
            double d1 = e.lastTickPosY + (e.posY - e.lastTickPosY) * mc.getRenderPartialTicks();
            double d2 = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * mc.getRenderPartialTicks();
            double f = e.prevRotationYaw + (e.rotationYaw - e.prevRotationYaw) * mc.getRenderPartialTicks();

            // glStencilFunc(GL_ALWAYS, 1, 0xFF);
            mc.getRenderManager().renderEntity(e, d0 - renderPosX, d1 - renderPosY, d2 - renderPosZ, (float) f, mc.getRenderPartialTicks(), true);
        }
    }

    public static void drawCompleteImage(float posX, float posY, float width, float height) {
        // glPushMatrix();
        glTranslatef(posX, posY, 0.0F);
        glBegin(7);
        glTexCoord2f(0.0F, 0.0F);
        glVertex3f(0.0F, 0.0F, 0.0F);
        glTexCoord2f(0.0F, 1.0F);
        glVertex3f(0.0F, height, 0.0F);
        glTexCoord2f(1.0F, 1.0F);
        glVertex3f(width, height, 0.0F);
        glTexCoord2f(1.0F, 0.0F);
        glVertex3f(width, 0.0F, 0.0F);
        glEnd();
        // glPopMatrix();
    }

}

