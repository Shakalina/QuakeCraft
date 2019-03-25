package com.quake;

import com.quake.block.Block;
import com.quake.block.ItemSpawnBlock;
import com.quake.block.JumpBlock;
import com.quake.block.PlayerSpawnBlock;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.BlockFace;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.Location;

import java.util.ArrayList;

public class CommandController {
    private Player player;

    public CommandController(Player player) {
        this.player = player;
    }

    public JumpBlock createJumpBlock(boolean free, double power, String name) {
        JumpBlock jumpBlock;
        Vector direction = null;

        if (!free) {
            direction = player.getEyeLocation().getDirection();
        }

        if (power <= 0d) {
            power = 1d;
        } else if (power > 50) {
            power = 50;
        }

        jumpBlock = new JumpBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN), direction, power, name);
        return jumpBlock;
    }

    public ItemSpawnBlock createItemBlock(int delay, ItemStack itemStack, String name) {
        if (itemStack == null) {
            return null;
        }
        return new ItemSpawnBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN), delay, itemStack, name);

    }

    public PlayerSpawnBlock createSpawnBlock(String name) {
        return new PlayerSpawnBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN), name);
    }

    public void chatList(ArrayList<? extends Block> list, int page) {
        int listLength = 8;
        if (list.size() == 0) {
            player.sendMessage(ChatColor.RED + "Empty!");
            return;
        }

        int count = list.size() / listLength; //9 -> 10
        if (count < page) {
            return;
        }
        printList(list, page * listLength, (page * listLength) + listLength);
        player.sendMessage("Total number of blocks: " + list.size());
        player.sendMessage("Page" + page + "/"  + list.size()/listLength);
        return;
    }

    private void printList(ArrayList<? extends Block> list, int start, int end) {
        if (start < 0) {
            errorMessage("Incorrect index!");
            return;
        }
        if (end >= list.size()) {
            end = list.size() - 1;
        }

        if (start > end) {
            errorMessage("Incorrect index");
            return;
        }

        player.sendMessage(ChatColor.DARK_GREEN + "Blocks:");
        for (int a = start; a < end; a++) {
            Block block = list.get(a);
            Location loc = block.getBlock().getLocation();
            player.sendMessage(ChatColor.YELLOW + block.getName() + ":" + ChatColor.BLUE + " X = " + loc.getX() + "; Y = " + loc.getY() + "; Z = " + loc.getZ() + ";");
        }
    }

    public void errorMessage(String message) {
        player.sendMessage(ChatColor.RED + message);
    }

    public void successCreatedBlock(String block) {
        player.sendMessage(ChatColor.BLUE + block + ChatColor.GREEN + " was created successfully.");
    }
}