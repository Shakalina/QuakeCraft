package com.quake;

import com.quake.block.BehaviorBlock;
import com.quake.block.ItemSpawnBlock;
import com.quake.block.JumpBlock;
import com.quake.block.PlayerSpawnBlock;
import com.quake.item.Armor;
import com.quake.item.Health;
import com.quake.item.Weapon;
import com.quake.сonfig.ReadConfig;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.logging.Logger;

import static com.quake.item.Item.valueIsExist;

public class Main extends JavaPlugin implements Listener, CommandExecutor {

    public static final Logger log = Logger.getLogger("Minecraft");
    public static final String PLUGIN_NAME = "QuakeCraft";
    public static World world;
    private ArrayList<PlayerSpawnBlock> playerSpawnBlocks;
    private int test;

    @Override
    public void onEnable() {
        this.getLogger().info("Quake!");
        world = this.getServer().getWorld("world");
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        ReadConfig readConfig = new ReadConfig(this);
        playerSpawnBlocks = readConfig.getPlayerSpawnBlocks();
        //==============================================================================
        for (BehaviorBlock s : readConfig.getBehaviorBlocks(JumpBlock.class)) {
            s.buildBehavior(this);
            log.info(s.getName() + "/n" + s.getBlock().toString());
        }
        for (BehaviorBlock s : readConfig.getBehaviorBlocks(ItemSpawnBlock.class)) {
            s.buildBehavior(this);
            log.info(s.getName() + "/n" + s.getBlock().toString());
        }
       /* for (ItemSpawnBlock block : config.getSpawnBlocks()){

            log.info("Name - " + block.getName());

            log.info("X - " + block.getBlock().getX());
            log.info("Y - " + block.getBlock().getY());
            log.info("Z - " + block.getBlock().getZ());
            block.buildBehavior(this);
        }*/
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //  ItemSpawnBlock spawnBlock = new ItemSpawnBlock(this,((Player)sender).getLocation().getBlock().getRelative(BlockFace.DOWN),"First");
        //  spawnBlock.setItemStack(new ItemStack(Material.WRITTEN_BOOK));
        //  spawnBlock.setDelay(200);
        //  spawnBlock.buildBehavior();
/*        Player player = (Player) sender;
        JumpBlock jumpBlock = new JumpBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN),null,10,"234d" + ++test);
        jumpBlock.buildBehavior(this);
        ItemSpawnBlock spawnBlock = new ItemSpawnBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN),20,new ItemStack(Material.IRON_SWORD),"Hohoh"+ ++test);
        spawnBlock.buildBehavior(this);
        WriteConfig w = new WriteConfig(this);
        w.addBehaviorBlock(jumpBlock);
        w.addBehaviorBlock(spawnBlock);*/
        Player player = (Player) sender;
        Armor armor = new Armor();
        Health health = new Health();
        Weapon weapon = new Weapon();
        Arrow item = player.launchProjectile(Arrow.class, player.getEyeLocation().getDirection().multiply(20));
        item.setDamage(999d);
        world.dropItem(player.getLocation(), weapon.getItem(Weapon.Type.DIAMOND_HOE));
        world.dropItem(player.getLocation(), weapon.getItem(Weapon.Type.DIAMOND_PICKAXE));
        world.dropItem(player.getLocation(), weapon.getItem(Weapon.Type.DIAMOND_SWORD));
        world.dropItem(player.getLocation(), weapon.getItem(Weapon.Type.DIAMOND_SHOVEL));
        world.dropItem(player.getLocation(), health.getItem(Health.Type.HUGE));
/*        PlayerSpawnBlock spawnBlock = new PlayerSpawnBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN),"SpawnBlock" + ++test);
        WriteConfig writeConfig = new WriteConfig(this);
        writeConfig.addPlayerSpawnBlock(spawnBlock);*/

        return false;
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Item item = event.getItem();
        Player player = (Player) event.getEntity();

        if (valueIsExist(Armor.Type.values(), item.getItemStack().getType().name())) {
            Armor armor = new Armor();
            armor.pickUp(player, event.getItem().getItemStack());

        } else if (valueIsExist(Health.Type.values(), item.getItemStack().getItemMeta().getDisplayName())) {
            Health health = new Health();
            health.pickUp(player, item.getItemStack());

        } else if (valueIsExist(Weapon.Type.values(), item.getItemStack().getType().name())) {
            Weapon weapon = new Weapon();
            weapon.pickUp(player, item.getItemStack());
        }
        event.setCancelled(true);
        item.remove();
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!UserInterface.createScoreBoard(event.getPlayer())) {
            event.getPlayer().kickPlayer("Developer is fool =/");
        }
    }

    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            if (event.getDamager() instanceof Snowball) {
                event.setDamage(3.333d);
            } else if (event.getDamager() instanceof Arrow) {
                event.setDamage(19d);
            } else if (event.getDamager() instanceof Fireball) {
                event.setDamage(10d);
            }
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Action a = event.getAction();
        String s = event.getItem().getType().name();
        if (a == Action.LEFT_CLICK_AIR &&
                com.quake.item.Item.valueIsExist(Weapon.Type.values(), s)) {
            if (UserInterface.getAmmo(p,Weapon.Type.valueOf(s)) > 0){
                Weapon.fire(Weapon.Type.valueOf(s), p);
                UserInterface.addAmmo(p,Weapon.Type.valueOf(s),-1);
            }
        }
    }

}

/*    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event){
        if (event.getEntity() instanceof Player) {
            Player p = (Player)event.getEntity();
            if (p.getHealth() <= event.getFinalDamage()) {
                p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                event.setCancelled(true);
                p.teleport(playerSpawnBlocks.get(0).getBlock().getLocation());
            }
        }
    }*/
