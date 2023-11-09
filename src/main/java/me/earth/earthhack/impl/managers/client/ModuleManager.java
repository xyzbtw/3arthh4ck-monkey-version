package me.earth.earthhack.impl.managers.client;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.register.IterationRegister;
import me.earth.earthhack.api.register.Registrable;
import me.earth.earthhack.api.register.exception.CantUnregisterException;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.event.events.client.PostInitEvent;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.anticheat.AntiCheat;
import me.earth.earthhack.impl.modules.client.autoconfig.AutoConfig;
import me.earth.earthhack.impl.modules.client.clickgui.ClickGui;
import me.earth.earthhack.impl.modules.client.colors.Colors;
import me.earth.earthhack.impl.modules.client.commands.Commands;
import me.earth.earthhack.impl.modules.client.configs.ConfigModule;
import me.earth.earthhack.impl.modules.client.customfont.FontMod;
import me.earth.earthhack.impl.modules.client.hud.HUD;
import me.earth.earthhack.impl.modules.client.management.Management;
import me.earth.earthhack.impl.modules.client.media.Media;
import me.earth.earthhack.impl.modules.client.nospoof.NoSpoof;
import me.earth.earthhack.impl.modules.client.notifications.Notifications;
import me.earth.earthhack.impl.modules.client.rotationbypass.Compatibility;
import me.earth.earthhack.impl.modules.client.safety.Safety;
import me.earth.earthhack.impl.modules.client.server.ServerModule;
import me.earth.earthhack.impl.modules.client.settings.SettingsModule;
import me.earth.earthhack.impl.modules.combat.antitrap.AntiTrap;
import me.earth.earthhack.impl.modules.combat.autoarmor.AutoArmor;
import me.earth.earthhack.impl.modules.combat.autocrystal.AutoCrystal;
import me.earth.earthhack.impl.modules.combat.autotrap.AutoTrap;
import me.earth.earthhack.impl.modules.combat.bowkill.BowKiller;
import me.earth.earthhack.impl.modules.combat.bowspam.BowSpam;
import me.earth.earthhack.impl.modules.combat.criticals.Criticals;
import me.earth.earthhack.impl.modules.combat.blocker.Blocker;
import me.earth.earthhack.impl.modules.combat.holefiller.HoleFiller;
import me.earth.earthhack.impl.modules.combat.killaura.KillAura;
import me.earth.earthhack.impl.modules.combat.offhand.Offhand;
import me.earth.earthhack.impl.modules.combat.automend.AutoMend;
import me.earth.earthhack.impl.modules.combat.privatecevbreaker.PrivateCevBreaker;
import me.earth.earthhack.impl.modules.combat.selftrap.SelfTrap;
import me.earth.earthhack.impl.modules.combat.surround.Surround;
import me.earth.earthhack.impl.modules.misc.antiaim.AntiAim;
import me.earth.earthhack.impl.modules.misc.autoeat.AutoEat;
import me.earth.earthhack.impl.modules.misc.autolog.AutoLog;
import me.earth.earthhack.impl.modules.misc.chat.Chat;
import me.earth.earthhack.impl.modules.misc.extratab.ExtraTab;
import me.earth.earthhack.impl.modules.misc.holdmodules.*;
import me.earth.earthhack.impl.modules.misc.noafk.NoAFK;
import me.earth.earthhack.impl.modules.movement.idealtick.IdealTick;
import me.earth.earthhack.impl.modules.movement.nomovesneak.NoMoveSneak;
import me.earth.earthhack.impl.modules.misc.nosoundlag.NoSoundLag;
import me.earth.earthhack.impl.modules.misc.packets.Packets;
import me.earth.earthhack.impl.modules.misc.pingspoof.PingSpoof;
import me.earth.earthhack.impl.modules.misc.portals.Portals;
import me.earth.earthhack.impl.modules.misc.spammer.Spammer;
import me.earth.earthhack.impl.modules.misc.tooltips.ToolTips;
import me.earth.earthhack.impl.modules.misc.truedurability.TrueDurability;
import me.earth.earthhack.impl.modules.movement.anchor.Anchor;
import me.earth.earthhack.impl.modules.movement.autosprint.AutoSprint;
import me.earth.earthhack.impl.modules.movement.badanchor.BadAnchor;
import me.earth.earthhack.impl.modules.movement.blocklag.BlockLag;
import me.earth.earthhack.impl.modules.movement.noslowdown.NoSlowDown;
import me.earth.earthhack.impl.modules.movement.packetfly.PacketFly;
import me.earth.earthhack.impl.modules.movement.reversestep.ReverseStep;
import me.earth.earthhack.impl.modules.movement.smartblocklag.SmartBlockLag;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.step.Step;
import me.earth.earthhack.impl.modules.movement.tickshift.TickShift;
import me.earth.earthhack.impl.modules.movement.velocity.Velocity;
import me.earth.earthhack.impl.modules.player.arrows.Arrows;
import me.earth.earthhack.impl.modules.player.arrows.MMQuiver;
import me.earth.earthhack.impl.modules.player.autochorus.AutoChorus;
import me.earth.earthhack.impl.modules.player.automine.AutoMine;
import me.earth.earthhack.impl.modules.player.blink.Blink;
import me.earth.earthhack.impl.modules.player.blocktweaks.BlockTweaks;
import me.earth.earthhack.impl.modules.player.exptweaks.ExpTweaks;
import me.earth.earthhack.impl.modules.player.fakeplayer.FakePlayer;
import me.earth.earthhack.impl.modules.player.fasteat.FastEat;
import me.earth.earthhack.impl.modules.player.fastplace.FastPlace;
import me.earth.earthhack.impl.modules.player.foreverspeedmine.ForeverSpeedMine;
import me.earth.earthhack.impl.modules.player.multitask.MultiTask;
import me.earth.earthhack.impl.modules.player.replenish.Replenish;
import me.earth.earthhack.impl.modules.player.scaffold.Scaffold;
import me.earth.earthhack.impl.modules.player.sorter.Sorter;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.modules.player.suicide.Suicide;
import me.earth.earthhack.impl.modules.player.swing.Swing;
import me.earth.earthhack.impl.modules.player.timer.Timer;
import me.earth.earthhack.impl.modules.player.xcarry.XCarry;
import me.earth.earthhack.impl.modules.render.ambience.Ambience;
import me.earth.earthhack.impl.modules.render.blockhighlight.BlockHighlight;
import me.earth.earthhack.impl.modules.render.breakhighlight.BreakHighlight;
import me.earth.earthhack.impl.modules.render.chams.Chams;
import me.earth.earthhack.impl.modules.render.crosshair.CrossHair;
import me.earth.earthhack.impl.modules.render.crystalchams.CrystalChams;
import me.earth.earthhack.impl.modules.render.crystalscale.CrystalScale;
import me.earth.earthhack.impl.modules.render.esp.ESP;
import me.earth.earthhack.impl.modules.render.fullbright.Fullbright;
import me.earth.earthhack.impl.modules.render.handchams.HandChams;
import me.earth.earthhack.impl.modules.render.holeesp.HoleESP;
import me.earth.earthhack.impl.modules.render.itemchams.ItemChams;
import me.earth.earthhack.impl.modules.render.logoutspots.LogoutSpots;
import me.earth.earthhack.impl.modules.render.nametags.Nametags;
import me.earth.earthhack.impl.modules.render.newchunks.NewChunks;
import me.earth.earthhack.impl.modules.render.newchunks.OIdChunks;
import me.earth.earthhack.impl.modules.render.norender.NoRender;
import me.earth.earthhack.impl.modules.render.popchams.PopChams;
import me.earth.earthhack.impl.modules.render.search.Search;
import me.earth.earthhack.impl.modules.render.skeleton.Skeleton;
import me.earth.earthhack.impl.modules.render.trails.Trails;
import me.earth.earthhack.impl.modules.render.trajectories.Trajectories;
import me.earth.earthhack.impl.modules.render.viewclip.CameraClip;
import me.earth.earthhack.impl.modules.render.viewmodel.ViewModel;
import me.earth.earthhack.impl.modules.render.weather.Weather;

import java.util.ArrayList;

public class ModuleManager extends IterationRegister<Module> {
    public void init() {
        Earthhack.getLogger().info("Initializing Modules.");
        //this.forceRegister(new AccountSpoof());
        this.forceRegister(new AntiCheat());
        this.forceRegister(new AutoConfig());
        this.forceRegister(new ClickGui());
        this.forceRegister(new Colors());
        this.forceRegister(new Commands());
        this.forceRegister(new ConfigModule());
       // this.forceRegister(new Debug());
        this.forceRegister(new IdealTick());
        this.forceRegister(new FontMod());
        this.forceRegister(new HUD());
        this.forceRegister(new PrivateCevBreaker());
        this.forceRegister(new Management());
        this.forceRegister(new NoSpoof());
        this.forceRegister(new Notifications());
        this.forceRegister(new Compatibility());
        this.forceRegister(new Safety());
        this.forceRegister(new AutoChorus());
        this.forceRegister(new ServerModule());
        this.forceRegister(new Skeleton());
        this.forceRegister(new PingSpoof());
        //this.forceRegister(new PbGui());
      //  this.forceRegister(new PbTeleport());
        this.forceRegister(new SettingsModule());
        //this.forceRegister(new TabModule());
        this.forceRegister(new Media());


        //this.forceRegister(new AntiSurround());
        this.forceRegister(new AntiTrap());
        //this.forceRegister(new Auto32k());
        //this.forceRegister(new AnvilAura());
        this.forceRegister(new AutoArmor());
        this.forceRegister(new AutoCrystal());
        this.forceRegister(new AutoTrap());
        //this.forceRegister(new BedBomb());
        this.forceRegister(new BowSpam());
        this.forceRegister(new BowKiller());
        this.forceRegister(new Blocker());
        this.forceRegister(new Criticals());
        this.forceRegister(new HoleFiller());
        this.forceRegister(new KillAura());
        this.forceRegister(new Offhand());
       // this.forceRegister(new LegSwitch());
        //this.forceRegister(new PistonAura());
        this.forceRegister(new Surround());
       // this.forceRegister(new AimBot());
       //this.forceRegister(new Snowballer());
        this.forceRegister(new AutoMend());
        this.forceRegister(new ForeverSpeedMine());

        this.forceRegister(new AntiAim());
        this.forceRegister(new SelfTrap());
       // this.forceRegister(new Announcer());
        //this.forceRegister(new AntiPackets());
        //this.forceRegister(new AntiPotion());
        //this.forceRegister(new AntiVanish());
        this.forceRegister(new AutoEat());
       // this.forceRegister(new AutoFish());
        //this.forceRegister(new AutoLog());
        //this.forceRegister(new BuildHeight());
        this.forceRegister(new AutoLog());
        this.forceRegister(new TickShift());
        this.forceRegister(new HoldModule4());
        this.forceRegister(new Chat());
       // this.forceRegister(new CoordAnnouncer());
        this.forceRegister(new ExtraTab());
       // this.forceRegister(new Logger());
       // this.forceRegister(new MCF());
       // this.forceRegister(new MobOwner());
        this.forceRegister(new NoAFK());
       // this.forceRegister(new NoHandShake());
       // this.forceRegister(new NoInteract());
        //this.forceRegister(new NoInterp());
        this.forceRegister(new NoSoundLag());
        //this.forceRegister(new Nuker());
        this.forceRegister(new Packets());
        //this.forceRegister(new PingSpoof());
        this.forceRegister(new Portals());
        //this.forceRegister(new SettingSpoof());
        //this.forceRegister(new SkinBlink());
        this.forceRegister(new Spammer());
        this.forceRegister(new ToolTips());
      //  this.forceRegister(new TpsSync());
       // this.forceRegister(new Undead());
        //this.forceRegister(new Tracker());
        this.forceRegister(new TrueDurability());
        this.forceRegister(new NoMoveSneak());
        //this.forceRegister(new ChorusControl());
       // this.forceRegister(new TargetHud());

        /*  if (Environment.hasForge()) {
            this.forceRegister(new AutoCraft());
        }

       */

       // this.forceRegister(new AutoRegear());
        //this.forceRegister(new PacketDelay());
        //this.forceRegister(new RPC());

        this.forceRegister(new Anchor());
        this.forceRegister(new AutoSprint());
        //this.forceRegister(new Avoid());
      //  this.forceRegister(new AutoWalk());
        this.forceRegister(new BlockLag());
        //this.forceRegister(new BoatFly());
       // this.forceRegister(new EntitySpeed());
        //this.forceRegister(new Clip());
        //this.forceRegister(new FastSwim());
        //this.forceRegister(new Flight());
        this.forceRegister(new ReverseStep());
        //this.forceRegister(new IceSpeed());
        //this.forceRegister(new Jesus());
       // this.forceRegister(new LongJump());
        this.forceRegister(new BreakHighlight());
       // this.forceRegister(new NoFall());
        //this.forceRegister(new NoMove());
        this.forceRegister(new NoSlowDown());
        this.forceRegister(new PacketFly());
       // this.forceRegister(new SafeWalk());
        this.forceRegister(new Speed());
       // this.forceRegister(new Stairs());
        this.forceRegister(new Step());
        this.forceRegister(new BadAnchor());
        this.forceRegister(new Velocity());
        this.forceRegister(new SmartBlockLag());

        this.forceRegister(new AutoMine());
       // this.forceRegister(new AutoTool());
        this.forceRegister(new Blink());
        this.forceRegister(new BlockTweaks());
       // this.forceRegister(new Cleaner());
        this.forceRegister(new ExpTweaks());
        this.forceRegister(new FakePlayer());
        this.forceRegister(new FastPlace());
        this.forceRegister(new FastEat());
        this.forceRegister(new MultiTask());
        //this.forceRegister(new NCPTweaks());
        //this.forceRegister(new NoGlitchBlocks());
       // this.forceRegister(new InventorySync());
        this.forceRegister(new Arrows());
       // this.forceRegister(new Reach());
        this.forceRegister(new Replenish());
        this.forceRegister(new Scaffold());
        this.forceRegister(new Sorter());
        //this.forceRegister(new Spectate());
        this.forceRegister(new Swing());
        this.forceRegister(new Speedmine());
        this.forceRegister(new Suicide());
        this.forceRegister(new Timer());
        this.forceRegister(new XCarry());

        this.forceRegister(new BlockHighlight());
        //this.forceRegister(new BreadCrumbs());
        this.forceRegister(new Chams());
        this.forceRegister(new ESP());
        this.forceRegister(new Fullbright());
        this.forceRegister(new HoleESP());
        //this.forceRegister(new LagOMeter());
        this.forceRegister(new LogoutSpots());
        this.forceRegister(new Nametags());
        this.forceRegister(new NewChunks());
        this.forceRegister(new NoRender());
        this.forceRegister(new Search());
        this.forceRegister(new OIdChunks());
       // this.forceRegister(new Skeleton());
        //this.forceRegister(new Sounds());
       // this.forceRegister(new Tracers());
        this.forceRegister(new CameraClip());
        this.forceRegister(new ViewModel());
        this.forceRegister(new HoldModule());
        this.forceRegister(new MMQuiver());
        this.forceRegister(new CrystalScale());
        this.forceRegister(new Trails());
        this.forceRegister(new Trajectories());
       // this.forceRegister(new Penis());
       // this.forceRegister(new WayPoints());
        this.forceRegister(new Weather());
        this.forceRegister(new HandChams());
        this.forceRegister(new CrystalChams());
       // this.forceRegister(new RainbowEnchant());
        this.forceRegister(new CrossHair());
        this.forceRegister(new PopChams());
        this.forceRegister(new ItemChams());
        this.forceRegister(new Ambience());
       // this.forceRegister(new HitEffects());
        //this.forceRegister(new CevBreaker());

      // this.forceRegister(new PingBypassModule());

        Bus.EVENT_BUS.post(new PostInitEvent());
    }

    public void load() {
        Caches.setManager(this);
        for (Module module : getRegistered()) {
            module.load();
        }
    }

    @Override
    public void unregister(Module module) throws CantUnregisterException {
        super.unregister(module);
        module.setRegistered(false);
        Bus.EVENT_BUS.unsubscribe(module);
    }

    protected void forceRegister(Module module) {
        registered.add(module);
        module.setRegistered(true);
        if (module instanceof Registrable) {
            ((Registrable) module).onRegister();
        }
    }

    public ArrayList<Module> getModulesFromCategory(Category moduleCategory) {
        final ArrayList<Module> iModules = new ArrayList<>();
        for (Module iModule : getRegistered()) {
            if (iModule.getCategory() == moduleCategory)
                iModules.add(iModule);
        }
        return iModules;
    }

    public ArrayList<Module> getHiddenModules() {
        ArrayList<Module> hiddenModules = new ArrayList<>();
        for (Module hidModule : getRegistered()) {
            if (hidModule.getHiddenState())
                hiddenModules.add(hidModule);
        }
        return hiddenModules;
    }
}
