package com.quake.listener;

import com.quake.Main;
import com.quake.UserInterface;
import com.quake.item.Armor;
import com.quake.item.Health;
import com.quake.item.Weapon;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static com.quake.item.Item.valueIsExist;

public class ItemListener implements Listener {

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

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL){
            event.setDamage(0.5d);
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Action a = event.getAction();
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        String s = item.getType().name();
        if (a == Action.LEFT_CLICK_AIR &&
                com.quake.item.Item.valueIsExist(Weapon.Type.values(), s)
                && event.getItem().getItemMeta().getDisplayName().equals(Weapon.Type.valueOf(s).toString())) {
            if (UserInterface.getAmmo(p, Weapon.Type.valueOf(s)) > 0) {
                Weapon w = new Weapon(Main.main);
                w.fire(Weapon.Type.valueOf(s), p);
                UserInterface.addAmmo(p, Weapon.Type.valueOf(s), -1);
            }
        }
    }


    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        org.bukkit.entity.Item item = event.getItem();
        Player player = (Player) event.getEntity();

        if (valueIsExist(Armor.Type.values(), item.getItemStack().getType().name())) {
            Armor armor = new Armor();
            armor.pickUp(player, event.getItem().getItemStack());

        } else if (valueIsExist(Health.Type.values(), item.getItemStack().getItemMeta().getDisplayName())) {
            Health health = new Health();
            health.pickUp(player, item.getItemStack());

        } else if (valueIsExist(Weapon.Type.values(), item.getItemStack().getType().name())) {
            Weapon weapon = new Weapon(Main.main);
            weapon.pickUp(player, item.getItemStack());
        }

        Weapon.setAmmo(item.getItemStack(),player);
        event.setCancelled(true);
        item.remove();
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}
