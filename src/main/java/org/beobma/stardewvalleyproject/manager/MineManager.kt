package org.beobma.stardewvalleyproject.manager

import org.beobma.stardewvalleyproject.manager.DataManager.gameData
import org.beobma.stardewvalleyproject.mine.Mine
import org.beobma.stardewvalleyproject.mine.MineTemplate
import org.beobma.stardewvalleyproject.mine.MineType
import org.beobma.stardewvalleyproject.resource.Resource
import org.beobma.stardewvalleyproject.resource.ResourceType
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import kotlin.math.min
import kotlin.random.Random

interface MineHandler {
    fun reset()
    fun Player.approach(currentMine: Mine?, floor: Int)
    fun Player.gathering(resources: Resource)
}

object MineManager : MineHandler {
    override fun reset() {
        // 하루가 넘어가거나, 기타 이유로 광산 내부를 리셋해야할 경우
        gameData.mines.clear()

        val mines = HashSet<Mine>()
        val world = Bukkit.getWorlds().first()

        for (i in 1..60) {
            val templates = arrayOf(MineTemplate.M, MineTemplate.N, MineTemplate.R)
            val types = arrayOf(MineType.A, MineType.B, MineType.C, MineType.D, MineType.E)
            val mineTemplate = templates[(i - 1) / 5 % templates.size]
            val mineType = types[(i - 1) % types.size]
            val xInterpolation = ((i / 15).toInt() + 1) * 174
            val startLocation = Location(world, mineTemplate.x + xInterpolation + mineType.xInterpolation, mineTemplate.y + mineType.yInterpolation, mineTemplate.z + mineType.zInterpolation)
            val mine = Mine(i, mineTemplate, mineType, startLocation)
            mine.startBlock = startLocation.add(0.0, -1.0, 0.0).block
            mine.startBlock?.type = Material.GOLD_BLOCK // 타입 수정
            mine.addResource()
            mine.enemySummoning()
            mines.add(mine)
        }
        gameData.mines.addAll(mines)
    }

    private fun Mine.addResource() {
        when (floor) {
            in 1..8 -> {
                mineType.resourcesLocations.forEach { location ->
                    val randomInt = Random.nextInt(0, 3)

                    when (randomInt) {
                        1 -> {
                            val resource = Resource(ResourceType.Magnesium, location)
                            summoningResource(resource)
                        }
                        else -> {
                            val resource = Resource(ResourceType.Aluminum, location)
                            summoningResource(resource)
                        }
                    }
                }
            }
            in 9..16 -> {
                mineType.resourcesLocations.forEach { location ->
                    val randomInt = Random.nextInt(0, 11)

                    when (randomInt) {
                        in 1..3 -> {
                            val resource = Resource(ResourceType.Magnesium, location)
                            summoningResource(resource)
                        }
                        in 4..6 -> {
                            val resource = Resource(ResourceType.Aluminum, location)
                            summoningResource(resource)
                        }
                        else -> {
                            val resource = Resource(ResourceType.Iron, location)
                            summoningResource(resource)
                        }
                    }
                }
            }
            in 17..24 -> {
                mineType.resourcesLocations.forEach { location ->
                    val randomInt = Random.nextInt(0, 11)

                    when (randomInt) {
                        in 1..2 -> {
                            val resource = Resource(ResourceType.Magnesium, location)
                            summoningResource(resource)
                        }
                        in 3..4 -> {
                            val resource = Resource(ResourceType.Aluminum, location)
                            summoningResource(resource)
                        }
                        in 5..7 -> {
                            val resource = Resource(ResourceType.Iron, location)
                            summoningResource(resource)
                        }
                        else -> {
                            val resource = Resource(ResourceType.Copper, location)
                            summoningResource(resource)
                        }
                    }
                }
            }
            in 25..32 -> {
                mineType.resourcesLocations.forEach { location ->
                    val randomInt = Random.nextInt(0, 11)

                    when (randomInt) {
                        1 -> {
                            val resource = Resource(ResourceType.Magnesium, location)
                            summoningResource(resource)
                        }
                        2 -> {
                            val resource = Resource(ResourceType.Aluminum, location)
                            summoningResource(resource)
                        }
                        in 3..4 -> {
                            val resource = Resource(ResourceType.Iron, location)
                            summoningResource(resource)
                        }
                        in 5..6 -> {
                            val resource = Resource(ResourceType.Copper, location)
                            summoningResource(resource)
                        }
                        else -> {
                            val resource = Resource(ResourceType.Lithium, location)
                            summoningResource(resource)
                        }
                    }
                }
            }
            in 33..40 -> {
                mineType.resourcesLocations.forEach { location ->
                    val randomInt = Random.nextInt(0, 11)

                    when (randomInt) {
                        in 1..3 -> {
                            val resource = Resource(ResourceType.Iron, location)
                            summoningResource(resource)
                        }
                        in 4..6 -> {
                            val resource = Resource(ResourceType.Copper, location)
                            summoningResource(resource)
                        }
                        else -> {
                            val resource = Resource(ResourceType.Lithium, location)
                            summoningResource(resource)
                        }
                    }
                }
            }
            in 41..46 -> {
                mineType.resourcesLocations.forEach { location ->
                    val randomInt = Random.nextInt(0, 101)

                    when (randomInt) {
                        in 1..15 -> {
                            val resource = Resource(ResourceType.Magnesium, location)
                            summoningResource(resource)
                        }
                        in 16..30 -> {
                            val resource = Resource(ResourceType.Aluminum, location)
                            summoningResource(resource)
                        }
                        in 31..60 -> {
                            val resource = Resource(ResourceType.Iron, location)
                            summoningResource(resource)
                        }
                        in 61..80 -> {
                            val resource = Resource(ResourceType.Gold, location)
                            summoningResource(resource)
                        }
                        else -> {
                            val resource = Resource(ResourceType.Platinum, location)
                            summoningResource(resource)
                        }
                    }
                }
            }
            in 47..52 -> {
                mineType.resourcesLocations.forEach { location ->
                    val randomInt = Random.nextInt(0, 101)

                    when (randomInt) {
                        in 1..15 -> {
                            val resource = Resource(ResourceType.Magnesium, location)
                            summoningResource(resource)
                        }
                        in 16..30 -> {
                            val resource = Resource(ResourceType.Aluminum, location)
                            summoningResource(resource)
                        }
                        in 31..60 -> {
                            val resource = Resource(ResourceType.Iron, location)
                            summoningResource(resource)
                        }
                        in 61..80 -> {
                            val resource = Resource(ResourceType.Platinum, location)
                            summoningResource(resource)
                        }
                        else -> {
                            val resource = Resource(ResourceType.Nickel, location)
                            summoningResource(resource)
                        }
                    }
                }
            }
            in 53..60 -> {
                mineType.resourcesLocations.forEach { location ->
                    val randomInt = Random.nextInt(0, 101)

                    when (randomInt) {
                        in 1..15 -> {
                            val resource = Resource(ResourceType.Magnesium, location)
                            summoningResource(resource)
                        }
                        in 16..30 -> {
                            val resource = Resource(ResourceType.Aluminum, location)
                            summoningResource(resource)
                        }
                        in 31..60 -> {
                            val resource = Resource(ResourceType.Iron, location)
                            summoningResource(resource)
                        }
                        in 61..80 -> {
                            val resource = Resource(ResourceType.Nickel, location)
                            summoningResource(resource)
                        }
                        else -> {
                            val resource = Resource(ResourceType.Titanium, location)
                            summoningResource(resource)
                        }
                    }
                }
            }
        }
    }

    private fun Mine.summoningResource(resource: Resource) {
        resource.location.block.type = resource.resourcesType.material
        resources.add(resource)
    }

    private fun Mine.enemySummoning() {
        //TODO("예정")
    }

    override fun Player.approach(currentMine: Mine?, floor: Int) {
        val mines = gameData.mines
        if (floor == 0) {
            mines[1].players.remove(this)
            // teleport()
            // 광산 퇴장
            return
        }

        if (currentMine == null) {
            // 광산 최초 입장
            mines[0].players.add(this)
            teleport(mines[0].startLocation)
            return
        }
        // 원래 10층에 있었는데, 9층으로 가려 함.
        if (currentMine.floor > floor) {
            currentMine.players.remove(this)
            mines[currentMine.floor - 2].players.add(this)
            val exitBlock = mines[currentMine.floor - 2].exitBlock ?: return
            teleport(exitBlock.location)
            return
        }

        currentMine.players.remove(this)
        mines[currentMine.floor].players.add(this)
        val exitBlock = mines[currentMine.floor].exitBlock ?: return
        teleport(exitBlock.location)
    }

    override fun Player.gathering(resources: Resource) {
        if (resources.isGathering) return
        val mine = gameData.mines.find { it.players.contains(this) } ?: return

        inventory.addItem(resources.resourcesType.dropItem)
        mine.resources.remove(mine.resources.find { it == resources })
        resources.location.block.type = Material.AIR

        if (mine.exitBlock == null && mine.resources.count { it.isGathering } >= mine.resources.size * 0.7) {
            resources.location.block.setExit(mine)
        }
    }

    private fun Block.setExit(mine: Mine) {
        type = Material.DARK_OAK_DOOR // 추후 타입 수정
        mine.exitBlock = this
        // 내려가는 길(출구) 생성
    }
}