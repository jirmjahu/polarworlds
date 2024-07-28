package net.jirmjahu.polarworlds.command;

import org.bukkit.entity.Player;

public interface SubCommand {


    boolean execute(Player player, String[] args);

}
