package com.willfp.reforges.commands

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.reforges.gui.UnReforgeGUI
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class UnReforgeCommand (
    plugin: EcoPlugin
) : PluginCommand(plugin, "unreforge", "reforges.command.unreforge", true) {

    override fun onExecute(player: CommandSender, args: List<String>) {
        player as Player
        if (plugin.configYml.getBool("gui.open-sound.enabled")) {
            player.playSound(
                player.location,
                Sound.valueOf(plugin.configYml.getString("gui.open-sound.id").uppercase()),
                1f,
                plugin.configYml.getDouble("gui.open-sound.pitch").toFloat()
            )
        }

        UnReforgeGUI.open(player)
    }
}