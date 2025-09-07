package org.beobma.stardewvalleyproject.manager

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.manager.DataManager.gameData
import org.beobma.stardewvalleyproject.manager.DataManager.interactionFarmlands
import org.beobma.stardewvalleyproject.manager.DataManager.plantList
import org.beobma.stardewvalleyproject.manager.DataManager.playerList
import org.beobma.stardewvalleyproject.manager.FarmingManager.growth
import org.beobma.stardewvalleyproject.manager.MineManager.nextDay
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.plant.list.DeadGrassPlant
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.type.Farmland
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

interface TimeHandler {
    fun timePlay()
    fun timePause()
    fun getHour(): Int
    fun getMinutes(): Int
    fun getSeason(): Season
}

object TimeManager : TimeHandler {
    private var timeTask: BukkitTask? = null

    override fun timePlay() {
        if (timeTask != null) return
        timeTask = object : BukkitRunnable() {
            override fun run() {
                advanceTime()

                if ((gameData.hour == 22 && gameData.minute == 0) || (gameData.hour == 23 && gameData.minute == 0)) {
                    playerList.forEach { player ->
                        player.sendActionBar(MiniMessage.miniMessage().deserialize("<bold>곧 12시입니다. 11시 50분까지 홈 모듈로 복귀하지 않으면 강제로 홈 모듈로 이동됩니다."))
                    }
                }

                if (gameData.hour == 23 && gameData.minute == 50) {
                    gameData.hour = 0
                    endOfDay()
                }
            }
        }.runTaskTimer(StardewValley.instance, 0, 125L)
    }

    override fun timePause() {
        timeTask?.cancel()
        timeTask = null
    }

    override fun getHour(): Int = gameData.hour
    override fun getMinutes(): Int = gameData.minute
    override fun getSeason(): Season = gameData.season

    private fun advanceTime() {
        gameData.minute += 10
        if (gameData.minute >= 60) {
            gameData.hour += 1
            gameData.minute = 0
        }

        bossBar()
    }

    var timeBossBar: BossBar = BossBar.bossBar(
        MiniMessage.miniMessage().deserialize("보스바"),
        1.0f,
        BossBar.Color.YELLOW,
        BossBar.Overlay.PROGRESS
    )
    private fun bossBar() {
        timeBossBar.name(MiniMessage.miniMessage().deserialize("${gameData.day}일 | ${gameData.hour}시 ${gameData.minute}분"))
    }

    fun showTimeBossBar(player: Player) {
        timeBossBar.addViewer(player)
    }

    fun unShowTimeBossBar(player: Player) {
        timeBossBar.removeViewer(player)
    }

    fun endOfDay() {
        gameData.day++
        gameData.hour = 0
        timePause()

        plantList.forEach { it.growth() }

        val blocksToDry = hashSetOf<Location>()

        interactionFarmlands.forEach { block ->
            val farmland = block.block.blockData as? Farmland ?: return@forEach
            val plant = plantList.find { it.farmlandLocation == block.block.location }

            if (plant is DeadGrassPlant) return@forEach
            if (plant !is Plant && farmland.moisture != farmland.maximumMoisture) {
                block.block.type = Material.DIRT
                blocksToDry.add(block)
                return@forEach
            }

            farmland.moisture = 0
            block.block.blockData = farmland
        }

        interactionFarmlands.removeAll(blocksToDry)

        // 모든 플레이어 홈 모듈로 이동.
        nextDay()
        timePlay()
    }
}