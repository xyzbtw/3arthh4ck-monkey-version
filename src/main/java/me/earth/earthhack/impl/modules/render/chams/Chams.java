package me.earth.earthhack.impl.modules.render.chams;

import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.SettingContainer;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.earthhack.impl.core.mixins.render.entity.IRenderEnderCrystal;
import me.earth.earthhack.impl.event.events.render.ModelRenderEvent;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.chams.mode.ChamsMode;
import me.earth.earthhack.impl.modules.render.chams.mode.WireFrameMode;
import me.earth.earthhack.impl.util.minecraft.EntityType;
import me.earth.earthhack.impl.util.render.GlShader;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.impl.util.render.forevershader.FillShader;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class Chams extends Module
{
    public static final ResourceLocation GALAXY_LOCATION = new ResourceLocation("earthhack:textures/client/galaxy.jpg");


    protected int texID = -1;

    public final Setting<ChamsMode> mode        =
            register(new EnumSetting<>("Mode", ChamsMode.Normal));
    protected final Setting<Boolean> self          =
            register(new BooleanSetting("Self", false));
    protected final Setting<Boolean> players       =
            register(new BooleanSetting("Players", true));
    public final Setting<Boolean> disableanimations      =
            register(new BooleanSetting("AnimationDisable", false));
    protected final Setting<Boolean> animals       =
            register(new BooleanSetting("Animals", false));
    protected final Setting<Boolean> monsters      =
            register(new BooleanSetting("Monsters", false));
    protected final Setting<Boolean> texture       =
            register(new BooleanSetting("Texture", false));
    protected final Setting<Boolean> xqz           =
            register(new BooleanSetting("XQZ", true));
    protected final Setting<Boolean> armor         =
            register(new BooleanSetting("Armor", true));
    protected final Setting<Float> speedfill = register(new NumberSetting<>("SpeedFill", 10f, 1f, 100f));
    protected final Setting<Float> z             =
            new NumberSetting<>("Z", -2000.0f, -5000.0f, 5000.0f); // not registered until we need it later
    protected final Setting<Float> mixFactor      =
            register(new NumberSetting<>("MixFactor", 1.0f, 0.0f, 1.0f));
    protected final Setting<String> image      =
            register(new StringSetting("Image", "None!"));
    protected final Setting<Color> color           =
            register(new ColorSetting("Color", new Color(255, 255, 255, 255)));
    protected final Setting<Color> wallsColor      =
            register(new ColorSetting("WallsColor", new Color(255, 255, 255, 255)));
    protected final Setting<Color> friendColor     =
            register(new ColorSetting("FriendColor", new Color(255, 255, 255, 255)));
    protected final Setting<Color> friendWallColor =
            register(new ColorSetting("FriendWallsColor", new Color(255, 255, 255, 255)));
    protected final Setting<Color> enemyColor      =
            register(new ColorSetting("EnemyColor", new Color(255, 255, 255, 255)));
    protected final Setting<Color> enemyWallsColor =
            register(new ColorSetting("EnemyWallsColor", new Color(255, 255, 255, 255)));
    protected final Setting<Color> armorColor           =
            register(new ColorSetting("ArmorColor", new Color(255, 255, 255, 255)));
    protected final Setting<Color> armorFriendColor     =
            register(new ColorSetting("ArmorFriendColor", new Color(255, 255, 255, 255)));
    protected final Setting<Color> armorEnemyColor      =
            register(new ColorSetting("ArmorEnemyColor", new Color(255, 255, 255, 255)));

    public final Setting<WireFrameMode> wireframe    =
        register(new EnumSetting<>("Wireframe", WireFrameMode.None));
    public final Setting<Boolean> wireWalls    =
        register(new BooleanSetting("WireThroughWalls", false));
    public final NumberSetting<Float> lineWidth =
        register(new NumberSetting<>("LineWidth" , 1f , 0.1f , 4f));
    public final Setting<Color> wireFrameColor =
        register(new ColorSetting("WireframeColor", new Color(255, 255, 255, 255)));
    protected final Setting<String> customShaderLocation      =
        register(new StringSetting("CustomShaderLocation", "None!"));
    protected final Setting<Boolean> refreshCustomShader =
        register(new BooleanSetting("RefreshCustomShader", false));
    public final Setting<Boolean> lightning =
            register(new BooleanSetting("Lightning", false));
    protected final Setting<Float> lightningScale = register(new NumberSetting<>("lightningScale", 1f, 0f, 15f));
    public final Setting<Color> lightningColor =
            register(new ColorSetting("LightningColor", new Color(255, 255, 255, 255)));


    protected boolean force;
    protected boolean hasImageChammed;
    protected boolean renderLayers;
    protected boolean renderModels;
    protected final GlShader fireShader = GlShader.createShader("chams");
    protected final GlShader galaxyShader = GlShader.createShader("stars");
    protected final GlShader waterShader = GlShader.createShader("water");
    protected final GlShader alphaShader = GlShader.createShader("alpha");
    protected final GlShader imageShader = GlShader.createShader("image");
    protected GlShader customShader = GlShader.createShader("stars");
    protected final long initTime = System.currentTimeMillis();
    protected boolean gif = false;

    protected DynamicTexture dynamicTexture;


    public Chams()
    {
        super("Chams", Category.Render);
        this.listeners.add(new ListenerModelPre(this));
        this.listeners.add(new ListenerModelPost(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerRenderEntity(this));
        this.listeners.add(new ListenerRenderLayers(this));
        this.setData(new ChamsData(this));
        mc.getTextureManager().loadTexture(Chams.GALAXY_LOCATION, new SimpleTexture(Chams.GALAXY_LOCATION));
        this.customShaderLocation.addObserver(e -> {
            if (!e.isCancelled() && !"None!".equalsIgnoreCase(e.getValue())) {
                loadCustomShader(e.getValue());
            }
        });
        this.refreshCustomShader.addObserver(e -> {
            if (e.getValue()) {
                e.setCancelled(true);
                loadCustomShader(customShaderLocation.getValue());
            }
        });
    }

    private void loadCustomShader(String location) {
        File file = new File(location);
        try (FileInputStream fis = new FileInputStream(file)) {
            GlShader shader = GlShader.createShader(file.getName(), fis);
            if (shader == null) {
                ChatUtil.sendMessage(TextColor.RED + "Could not load custom shader! Check the logs.");
            } else {
                ChatUtil.sendMessage(TextColor.GREEN + "Custom shader loaded successfully.");
                this.customShader = shader;
            }
        } catch (IOException ex) {
            ChatUtil.sendMessage(TextColor.RED + "Can not load custom shader: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    protected void doWireFrame(ModelRenderEvent event) {
        if (!isValid(event.getEntity())) {
            return;
        }
        
        Color wireColor = wireFrameColor.getValue();
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glLineWidth(lineWidth.getValue());
        if (wireWalls.getValue()) {
            glDepthMask(false);
            glDisable(GL_DEPTH_TEST);
        }

        glColor4f(wireColor.getRed() / 255.0f,
                  wireColor.getGreen() / 255.0f,
                  wireColor.getBlue() / 255.0f,
                  wireColor.getAlpha() / 255.0f);
        event.getModel().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(),
                                event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScale());
        glPopAttrib();
    }

    public boolean isValid(Entity entity, ChamsMode modeIn)
    {
        return this.isEnabled() && modeIn == mode.getValue() && isValid(entity);
    }

    public boolean isValid(Entity entity)
    {
        Entity renderEntity = RenderUtil.getEntity();
        if (entity == null)
        {
            return false;
        }
        else if (!self.getValue() && entity.equals(renderEntity))
        {
            return false;
        }
        else if (players.getValue() && entity instanceof EntityPlayer)
        {
            return true;
        }
        else if (!monsters.getValue()
                    || !EntityType.isMonster(entity)
                && !EntityType.isBoss(entity))
        {
            return animals.getValue()
                    && (EntityType.isAngry(entity)
                        || EntityType.isAnimal(entity));
        }
        else
        {
            return true;
        }
    }
    public boolean shouldLightning(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
                return lightning.getValue();
        }
        return false;
    }

    protected Color getVisibleColor(Entity entity) {
        if (Managers.FRIENDS.contains(entity)) {
            return friendColor.getValue();
        } else if (Managers.ENEMIES.contains(entity)) {
            return enemyColor.getValue();
        } else {
            return color.getValue();
        }
    }

    protected Color getWallsColor(Entity entity) {
        if (Managers.FRIENDS.contains(entity)) {
            return friendWallColor.getValue();
        } else if (Managers.ENEMIES.contains(entity)) {
            return enemyWallsColor.getValue();
        } else {
            return wallsColor.getValue();
        }
    }

    public Color getArmorVisibleColor(Entity entity) {
        if (Managers.FRIENDS.contains(entity)) {
            return armorFriendColor.getValue();
        } else if (Managers.ENEMIES.contains(entity)) {
            return armorEnemyColor.getValue();
        } else {
            return armorColor.getValue();
        }
    }

    protected void checkSetupFBO() {
        Framebuffer fbo = mc.getFramebuffer();
        if (fbo.depthBuffer > -1) {
            setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }

    protected void setupFBO(Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
        int stencilDepthBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
        EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT, mc.displayWidth, mc.displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencilDepthBufferID);
    }
    public boolean shouldWallsLightning(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            return xqz.getValue();
        }
        return false;
    }
    public boolean shouldArmorChams() {
        return armor.getValue();
    }

    public boolean isImageChams() {
        return mode.getValue() == ChamsMode.Image;
    }
    public void runPreFill() {
        float ticks = mc.getRenderPartialTicks();
        if(mode.getValue() == ChamsMode.ShaderFill){
             FillShader.INSTANCE.startDraw(ticks);
        }
    }
    public void runPostFill() {
        if(mode.getValue() == ChamsMode.ShaderFill){
            FillShader.INSTANCE.stopDraw(color.getValue());
            FillShader.INSTANCE.update(speedfill.getValue() / 1000.0f);
        }
    }
    public float getAlpha() {
        return color.getValue().getAlpha() / 255.0f;
    }


    protected void renderLightning(ModelBase modelBase, EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, Color color)
    {
        Render<? extends EntityLivingBase> render = mc.getRenderManager().getEntityRenderObject(entitylivingbaseIn);
        RenderLivingBase<?> renderLivingBase = (RenderLivingBase<?>) render;
        assert renderLivingBase != null;
        boolean flag = entitylivingbaseIn.isInvisible();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(!flag);
        mc.getTextureManager().bindTexture(LIGHTNING_TEXTURE);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float f = (float)entitylivingbaseIn.ticksExisted + partialTicks;
        GlStateManager.translate(f * 0.01F, f * 0.01F, 0.0F);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        float f1 = 0.5F;
        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        modelBase.setModelAttributes(renderLivingBase.getMainModel());
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        modelBase.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(flag);
    }
    private static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("earthhack:textures/client/lightning.png");

    private static void renderLightning(ModelBase modelBase, EntityEnderCrystal entityEnderCrystal, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, Color color)
    {
        Render<? extends EntityEnderCrystal> render = mc.getRenderManager().getEntityRenderObject(entityEnderCrystal);
        RenderEnderCrystal renderLivingBase = (RenderEnderCrystal) render;
        assert renderLivingBase != null;
        boolean flag = entityEnderCrystal.isInvisible();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(!flag);
        mc.getTextureManager().bindTexture(LIGHTNING_TEXTURE);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float f = (float)entityEnderCrystal.ticksExisted + partialTicks;
        GlStateManager.translate(f * 0.01F, f * 0.01F, 0.0F);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        float f1 = 0.5F;
        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        modelBase.setModelAttributes(((IRenderEnderCrystal) renderLivingBase).getModelEnderCrystal());
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        modelBase.render(entityEnderCrystal, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(flag);
    }
}
