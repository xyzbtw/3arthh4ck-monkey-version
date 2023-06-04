package me.earth.earthhack.impl.modules.combat.automend;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;
import me.earth.earthhack.impl.modules.player.exptweaks.ExpTweaks;
import me.earth.earthhack.impl.util.math.Timer;
import me.earth.earthhack.impl.util.minecraft.DamageUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.*;


public class AutoMend extends Module
{

    protected final Setting<Float> Delay =
            register(new NumberSetting<>("DelayMend", 150.0f, 0.0f, 1000.0f));
    protected final Setting<Float> xpDelay =
            register(new NumberSetting<>("DelayXP", 150.0f, 0.0f, 1000.0f));
    protected final Setting<Float>  Pct =
            register(new NumberSetting<>("Percent", 90.0f, 0.0f, 100.0f));
    protected final Setting<Float>  pitch =
            register(new NumberSetting<>("Pitch", 90.0f, -90.0f, 90.0f));
    protected final Setting<Float>  maxdmg =
            register(new NumberSetting<>("Max-Dmg", 6.0f, 0.0f, 36.0f));
    protected final Setting<Boolean> blocks =
            register(new BooleanSetting("Block", false));

    private static final ModuleCache<AutoArmor> AutoArmor =
            Caches.getModule(AutoArmor.class);
    private static final ModuleCache<ExpTweaks> xptweaks =
            Caches.getModule(ExpTweaks.class);


    public AutoMend()
    {
        super("BetterAutoMend", Category.Combat);
        this.listeners.add(new ListenerMotion(this));
        this.setData(new AutoMendData(this));
    }

    protected Timer timer = new Timer();
    protected Timer xpTimer = new Timer();
    protected Timer internalTimer = new Timer();
    protected boolean ReadyToMend = false;
    protected boolean AllDone = false;
    protected LinkedList<MendState> SlotsToMoveTo = new LinkedList<MendState>();
    protected boolean wasEnabledAM, wasEnabledXP;
    protected EntityPlayer enemy;

    @Override
    public void onDisable(){
        super.onDisable();
        if(wasEnabledAM){
            AutoArmor.enable();
        }
        if(wasEnabledXP){
            xptweaks.enable();
        }
        wasEnabledAM=false;
        wasEnabledXP=false;
    }

    protected Vec3i[] blocking = new Vec3i[]{
            new Vec3i(1,1,0),
            new Vec3i(-1,1,0),
            new Vec3i(0,1,1),
            new Vec3i(0,1,-1)
    };

    @Override
    public void onEnable()
    {
        super.onEnable();


        if(AutoArmor.isEnabled()){
            wasEnabledAM = true;
            AutoArmor.disable();
        }
        if(xptweaks.isEnabled()){
            wasEnabledXP = true;
            xptweaks.disable();
        }
        ArrayList<ItemStack> ArmorsToMend = new ArrayList<ItemStack>();
        SlotsToMoveTo.clear();
        ReadyToMend = false;
        AllDone = false;

        int l_Slot = InventoryUtil.findInHotbar(item -> item.getItem() instanceof ItemExpBottle);

        if (l_Slot == -1)
        {
            ChatUtil.sendMessage("You don't have any XP! Disabling!");
            disable();
            return;
        }

        final Iterator<ItemStack> l_Armor = mc.player.getArmorInventoryList().iterator();

        int l_I = 0;
        boolean l_NeedMend = false;

        while (l_Armor.hasNext())
        {
            final ItemStack l_Item = l_Armor.next();
            if (l_Item != ItemStack.EMPTY && l_Item.getItem() != Items.AIR)
            {
                ArmorsToMend.add(l_Item);

                float l_Pct = GetArmorPct(l_Item);

                if (l_Pct < Pct.getValue())
                {
                    l_NeedMend = true;
                }
            }
        }

        if (ArmorsToMend.isEmpty() || !l_NeedMend)
        {
            ChatUtil.sendMessage(ChatFormatting.RED + "Nothing to mend!");
            disable();
            return;
        }

        ArmorsToMend.sort(Comparator.comparing(ItemStack::getItemDamage).reversed());

        ArmorsToMend.forEach(p_Item ->
        {

        });

        final Iterator<ItemStack> l_Itr = ArmorsToMend.iterator();

        boolean l_First = true;

        for (l_I = 0; l_I < mc.player.inventoryContainer.getInventory().size(); ++l_I)
        {
            if (l_I == 0 || l_I == 5 || l_I == 6 || l_I == 7 || l_I == 8)
                continue;

            ItemStack l_Stack = InventoryUtil.get(l_I);

            /// Slot must be empty or air
            if (!l_Stack.isEmpty() && l_Stack.getItem() != Items.AIR)
                continue;

            if (!l_Itr.hasNext())
                break;

            ItemStack l_ArmorS = l_Itr.next();

            SlotsToMoveTo.add(new MendState(l_First, l_I, GetSlotByItemStack(l_ArmorS), GetArmorPct(l_ArmorS) < Pct.getValue(), l_ArmorS.getDisplayName()));

            if (l_First)
                l_First = false;

            // SendMessage("Found free slot " + l_I + " for " + l_ArmorS.getDisplayName() + " stack here is " + l_Stack.getDisplayName());
        }
    }




    public int GetSlotByItemStack(ItemStack p_Stack)
    {
        if (p_Stack.getItem() instanceof ItemArmor)
        {
            ItemArmor l_Armor = (ItemArmor) p_Stack.getItem();

            switch (l_Armor.getEquipmentSlot())
            {
                case CHEST:
                    return 6;
                case FEET:
                    return 8;
                case HEAD:
                    return 5;
                case LEGS:
                    return 7;
                default:
                    break;
            }
        }

        return mc.player.inventory.armorInventory.indexOf(p_Stack) + 5;
    }

    protected float GetArmorPct(ItemStack p_Stack)
    {
        return ((float)(p_Stack.getMaxDamage()-p_Stack.getItemDamage()) /  (float)p_Stack.getMaxDamage())*100.0f;
    }
    protected double getDamageNoArmor(double x, double y, double z)
    {
        double distance = mc.player.getDistance(x, y, z) / 12.0;
        if (distance > 1.0)
        {
            return 0.0f;
        }
        else
        {
            double density = DamageUtil.getBlockDensity(
                    new Vec3d(x, y, z),
                    mc.player.getEntityBoundingBox(),
                    mc.world,
                    true,
                    true,
                    false,
                    false);

            double densityDistance = distance = (1.0 - distance) * density;
            float damage = DamageUtil.getDifficultyMultiplier((float)
                    ((densityDistance * densityDistance + distance)
                            / 2.0 * 7.0 * 12.0 + 1.0));

            damage = CombatRules.getDamageAfterAbsorb(damage, 3, 2.0f);

            PotionEffect resistance =
                    mc.player.getActivePotionEffect(MobEffects.RESISTANCE);
            if (resistance != null)
            {
                damage =
                        damage * (25 - (resistance.getAmplifier() + 1) * 5) / 25.0f;
            }

            return Math.max(damage, 0.0f);
        }
    }
    protected boolean willtakedmg(){
        enemy = EntityUtil.getClosestEnemy();
        if(enemy == null) return false;
        else {
            double damage = getDamageNoArmor(enemy.posX, enemy.posY, enemy.posZ);
            return damage > EntityUtil.getHealth(mc.player) + 1.0 || damage > maxdmg.getValue();
        }
    }

    protected static class MendState
    {
        public MendState(boolean p_MovedToInv, int p_SlotMovedTo, int p_ArmorSlot, boolean p_NeedMend, String p_ItemName)
        {
            MovedToInv = p_MovedToInv;
            SlotMovedTo = p_SlotMovedTo;
            ArmorSlot = p_ArmorSlot;
            NeedMend = p_NeedMend;
            ItemName = p_ItemName;
        }
        public boolean MovedToInv;
        public int SlotMovedTo;
        public boolean Reequip = false;
        public int ArmorSlot;
        public boolean DoneMending = false;
        public boolean NeedMend;
        public String ItemName;
    }
}



