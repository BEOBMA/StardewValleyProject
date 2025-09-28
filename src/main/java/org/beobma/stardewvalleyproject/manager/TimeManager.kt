package org.beobma.stardewvalleyproject.manager

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.format.ShadowColor
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
                        player.sendActionBar(
                            MiniMessage.miniMessage().deserialize(
                                "<bold>곧 12시입니다. 11시 50분까지 홈 모듈로 복귀하지 않으면 강제로 홈 모듈로 이동됩니다."
                            )
                        )
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
        MiniMessage.miniMessage().deserialize("보스바").shadowColor(ShadowColor.shadowColor(0x00FFFFFF)),
        1.0f,
        BossBar.Color.YELLOW,
        BossBar.Overlay.PROGRESS
    )

    private fun bossBar() {
        timeBossBar.name(
            MiniMessage.miniMessage().deserialize("${gameData.day}일 | ${gameData.hour}시 ${gameData.minute}분")
        )
    }

    /** 보스바 표시 추가 */
    fun showTimeBossBar(player: Player) {
        timeBossBar.addViewer(player)
    }

    /** 보스바 표시 제거 */
    fun unShowTimeBossBar(player: Player) {
        timeBossBar.removeViewer(player)
    }

    /** 하루 종료 처리: 날짜 증가, 시간 초기화, 타이머 일시정지, 작물 성장 처리, 경작지 수분 초기화, 광산 갱신, 타이머 재개 */
    fun endOfDay() {
        gameData.day++
        gameData.hour = 0
        timePause()

        plantList.forEach { it.growth() }

        val blocksToDry = hashSetOf<Location>()

        interactionFarmlands.forEach { loc ->
            val farmland = loc.block.blockData as? Farmland ?: return@forEach
            val plant = plantList.find { it.farmlandLocation == loc.block.location }

            if (plant is DeadGrassPlant) return@forEach
            if (plant !is Plant && farmland.moisture != farmland.maximumMoisture) {
                loc.block.type = Material.DIRT
                blocksToDry.add(loc)
                return@forEach
            }

            farmland.moisture = 0
            loc.block.blockData = farmland
        }

        interactionFarmlands.removeAll(blocksToDry)

        // 모든 플레이어 홈 모듈 이동
        nextDay()
        timePlay()
    }
}