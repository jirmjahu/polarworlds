package net.jirmjahu.squidworlds.command;

import net.jirmjahu.squidworlds.world.SquidWorld;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        SquidWorld world = new SquidWorld("test", World.Environment.NORMAL, Difficulty.EASY, null, null, WorldType.NORMAL, true, true, true, true, true);

        if (args.length == 0) {
            world.create();
            commandSender.sendMessage("erstellt");
            return true;
        }


        if (args[0].equalsIgnoreCase("2")) {
            world.delete();
            commandSender.sendMessage("gelöscht ");
            return true;
        }

        return false;
    }
}
