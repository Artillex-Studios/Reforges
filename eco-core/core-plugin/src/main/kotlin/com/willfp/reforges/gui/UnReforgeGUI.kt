package com.willfp.reforges.gui

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.updating.ConfigUpdater
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.gui.captiveSlot
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.player
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.eco.core.items.Items
import com.willfp.ecomponent.CaptiveItem
import com.willfp.ecomponent.setSlot
import com.willfp.reforges.util.reforge
import org.bukkit.entity.Player

object UnReforgeGUI {
    private lateinit var menu: Menu
    private lateinit var itemToReforge: CaptiveItem

    @JvmStatic
    fun open(player: Player) {
        menu.open(player)
    }

    @JvmStatic
    @ConfigUpdater
    fun update(plugin: EcoPlugin) {
        itemToReforge = CaptiveItem()

        val maskPattern = plugin.configYml.getStrings("unreforge-gui.mask.pattern").toTypedArray()

        val maskItems = plugin.configYml.getStrings("unreforge-gui.mask.materials")
            .mapNotNull { Items.lookup(it) }
            .toTypedArray()

        menu = menu(plugin.configYml.getInt("unreforge-gui.rows")) {
            title = plugin.langYml.getFormattedString("unreforge-gui.title")
            setMask(FillerMask(MaskItems(*maskItems), *maskPattern))
            allowChangingHeldItem()

            setSlot(
                plugin.configYml.getInt("unreforge-gui.accept-slot.row"),
                plugin.configYml.getInt("unreforge-gui.accept-slot.column"),
                slot(Items.lookup(plugin.configYml.getString("unreforge-gui.accept-slot.material")).item) {
                    onLeftClick { event,  _,  ->
                        val reforge = itemToReforge[event.player]?.reforge ?: return@onLeftClick
                        val stone = reforge.stone
                        itemToReforge[event.player].reforge = null

                        DropQueue(event.player)
                            .addItem(stone)
                            .setLocation(event.player.eyeLocation)
                            .forceTelekinesis()
                            .push()
                    }
                }
            )

            setSlot(
                plugin.configYml.getInt("gui.item-slot.row"),
                plugin.configYml.getInt("gui.item-slot.column"),
                captiveSlot(),
                bindCaptive = itemToReforge
            )

            onClose { event, menu ->
                DropQueue(event.player as Player)
                    .addItems(menu.getCaptiveItems(event.player as Player))
                    .setLocation(event.player.eyeLocation)
                    .forceTelekinesis()
                    .push()
            }
        }
    }
}