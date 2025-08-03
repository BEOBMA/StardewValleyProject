package org.beobma.stardewvalleyproject.manager

import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.entity.Enemy
import org.beobma.stardewvalleyproject.manager.CustomModelDataManager.getCustomModelData
import org.beobma.stardewvalleyproject.manager.DataManager.gameData
import org.beobma.stardewvalleyproject.manager.DataManager.mines
import org.beobma.stardewvalleyproject.manager.ToolManager.HEAVYDRILL_MODEL_DATA
import org.beobma.stardewvalleyproject.manager.ToolManager.LIGHTDRILL_MODEL_DATA
import org.beobma.stardewvalleyproject.manager.ToolManager.PICKAXE_MODEL_DATA
import org.beobma.stardewvalleyproject.manager.ToolManager.PICKAXE_MODEL_DATAS
import org.beobma.stardewvalleyproject.manager.ToolManager.decreaseCustomDurability
import org.beobma.stardewvalleyproject.mine.Mine
import org.beobma.stardewvalleyproject.mine.MineTemplate
import org.beobma.stardewvalleyproject.mine.MineType
import org.beobma.stardewvalleyproject.resource.Resource
import org.beobma.stardewvalleyproject.resource.ResourceType
import org.beobma.stardewvalleyproject.resource.ResourceType.*
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import java.util.*
import kotlin.math.ceil
import kotlin.random.Random

object MineManager {
    private const val MAX_MINE_FLOOR = 60
    private const val CALCULATE_OFFSET = 58.0
    private const  val TICKINTERVAL = 10L

    private val world = Bukkit.getWorlds().first()
    private val miniMessage = MiniMessage.miniMessage()

    val gatheringPlayers = mutableSetOf<UUID>()

    fun reset() {
        if (mines.isNotEmpty()) {
            loadData()
            return
        }
        mines.forEach { mine ->
            mine.resources.forEach { resource ->
                val itemDisplay = resource.getItemDisplay() ?: return@forEach
                itemDisplay.remove()
            }
        }
        mines.clear()
        mines.addAll(generateMines())
    }

    fun nextDay() {
        mines.forEach { mine ->
            mine.resources.forEach { resource ->
                val itemDisplay = resource.getItemDisplay() ?: return@forEach
                itemDisplay.remove()
            }
        }
        mines.clear()
        mines.addAll(generateMines(true))
    }

    fun loadData() {
        mines.forEach { mine ->
            mine.startBlockLocation?.block?.type = Material.GOLD_BLOCK
            mine.exitBlockLocation?.block?.type = Material.COPPER_BLOCK
        }
    }

    fun Player.approach(currentMine: Mine?, floor: Int) {
        if (floor == 0) {
            currentMine?.let { leaveMine(it) }
            teleport(Location(world, -191.0, -56.0, 95.0))
            return
        }

        val nextMine = mines.find { it.floor == floor } ?: return
        currentMine?.let { leaveMine(it) }

        nextMine.players.add(this)
        teleport(nextMine.startBlockLocation?.clone()?.add(0.0, 1.0, 0.0) ?: return)
        nextMine.spawnEnemys()
    }


    private fun calculateOffset(floor: Int): Double = ((floor - 1) / 5) * CALCULATE_OFFSET

    private fun generateMines(useTemplateOffset: Boolean = false): List<Mine> {
        val templates = arrayOf(MineTemplate.M, MineTemplate.N, MineTemplate.R)
        val types = arrayOf(MineType.A, MineType.B, MineType.C, MineType.D, MineType.E)

        return (1..MAX_MINE_FLOOR).map { floor ->
            val template = templates[(floor - 1) / 5 % templates.size]
            val type = types[(floor - 1) % types.size]
            val xOffset = calculateOffset(floor)

            val mine = Mine(floor, template, type).apply {
                val startX = if (useTemplateOffset) template.x + type.xInterpolation else type.startX
                val startY = if (useTemplateOffset) template.y + type.yInterpolation else type.startY - 1.0
                val startZ = if (useTemplateOffset) template.z + type.zInterpolation else type.startZ

                startBlockLocation = Location(world, startX + xOffset, startY, startZ)
                startBlockLocation?.block?.type = Material.GOLD_BLOCK
                addResource()
                addEnemyData()
            }
            mine
        }
    }

    private fun Mine.addResource() {
        val xOffset = calculateOffset(floor)
        mineType.resourcesLocations.forEach { (x, y, z) ->
            val location = Location(world, x + xOffset, y, z)
            val type = getRandomForFloor(floor)
            summoningResource(Resource(type, location))
        }
    }

    private fun getRandomForFloor(floor: Int): ResourceType {
        val r = Random.nextInt(0, 101)
        return when (floor) {
            in 1..8 -> if (r < 33) Magnesium else Aluminum

            in 9..16 -> when (r) {
                in 0..30 -> Magnesium
                in 31..60 -> Aluminum
                else -> Iron
            }

            in 17..24 -> when (r) {
                in 0..20 -> Magnesium
                in 21..40 -> Aluminum
                in 41..70 -> Iron
                else -> Copper
            }

            in 25..32 -> when (r) {
                0 -> Magnesium
                1 -> Aluminum
                in 2..30 -> Iron
                in 31..50 -> Copper
                else -> Lithium
            }

            in 33..40 -> when (r) {
                in 0..30 -> Iron
                in 31..60 -> Copper
                else -> Lithium
            }

            in 41..46 -> when (r) {
                in 0..15 -> Magnesium
                in 16..30 -> Aluminum
                in 31..60 -> Iron
                in 61..80 -> Gold
                else -> Platinum
            }

            in 47..52 -> when (r) {
                in 0..15 -> Magnesium
                in 16..30 -> Aluminum
                in 31..60 -> Iron
                in 61..80 -> Platinum
                else -> Nickel
            }

            else -> when (r) {
                in 0..15 -> Magnesium
                in 16..30 -> Aluminum
                in 31..60 -> Iron
                in 61..80 -> Nickel
                else -> Titanium
            }
        }
    }

    private fun Mine.addEnemyData() {
        val xOffset = calculateOffset(floor)
        mineType.enemysLocations.forEach { (x, y, z) ->
            val spawnLocation = Location(world, x + xOffset, y, z)
            val enemy = Enemy(spawnLocation, getEntityTypeForFloor(floor))
            enemys.add(enemy)
        }
    }

    private fun getEntityTypeForFloor(floor: Int): EntityType {
        return when ((floor - 1) % 15) {
            in 0..4 -> EntityType.DROWNED
            in 5..9 -> EntityType.HUSK
            else -> EntityType.ZOMBIE
        }
    }

    private fun Mine.summoningResource(resource: Resource) {
        resource.location.block.type = Material.BARRIER
        val itemDisplay = world.spawn(resource.location.add(0.5, 0.5, 0.5), ItemDisplay::class.java)
        val uuidString = itemDisplay.uniqueId.toString()
        resource.uuidString = uuidString
        val itemStack = ItemStack(Material.GRAY_DYE).apply {
            itemMeta = itemMeta.apply {
                setCustomModelData(resource.resourcesType.customModelData)
            }
        }
        itemDisplay.setItemStack(itemStack)
        resources.add(resource)
    }

    fun Player.leaveMine(mine: Mine) {
        mine.players.remove(this)
        mine.enemys.filter { it.isSpawn && !it.isDead && it.enemyUUID != null }.forEach {
            it.isSpawn = false
            Bukkit.getEntity(UUID.fromString(it.enemyUUID))?.remove()
        }
    }

    private fun Mine.spawnEnemys() {
        enemys.filter { !it.isSpawn && !it.isDead }.forEach {
            val entity = world.spawnEntity(it.location, it.entityType)
            it.isSpawn = true
            it.enemyUUID = entity.uniqueId.toString()
        }
    }

    fun Player.gathering(resource: Resource) {
        if (resource.isGathering) return
        if (gatheringPlayers.contains(this.uniqueId)) return

        val mainHand = inventory.itemInMainHand
        val customModelData = mainHand.getCustomModelData()
        if (customModelData !in PICKAXE_MODEL_DATAS) return

        val mine = mines.find { it.players.contains(this) } ?: return
        val delay = getGatheringDelay(mainHand, resource.resourcesType) ?: return

        gatheringPlayers.add(uniqueId)

        val soundTask = Bukkit.getScheduler().runTaskTimer(
            StardewValley.instance,
            Runnable {
                playSound(location, Sound.BLOCK_STONE_HIT, 1f, 1f)
                world.spawnParticle(Particle.BLOCK, resource.location, 10, 0.2, 0.2, 0.2, Material.STONE.createBlockData())
            },
            0L, TICKINTERVAL
        )

        Bukkit.getScheduler().runTaskLater(StardewValley.instance, Runnable {
            soundTask.cancel()

            val itemStack = ItemStack(Material.RED_DYE).apply {
                itemMeta = itemMeta.apply {
                    displayName(miniMessage.deserialize(resource.resourcesType.displayName))
                    setCustomModelData(resource.resourcesType.customModelData)
                }
            }

            val itemDisplay = resource.getItemDisplay() ?: return@Runnable
            itemDisplay.remove()

            inventory.addItem(itemStack)
            resource.isGathering = true
            resource.location.block.type = Material.AIR

            playSound(location, Sound.BLOCK_STONE_BREAK, 1f, 1f)
            world.spawnParticle(Particle.BLOCK, resource.location, 30, 0.3, 0.3, 0.3, Material.STONE.createBlockData())

            val gathered = mine.resources.count { it.isGathering }
            if (mine.exitBlockLocation == null && gathered >= ceil(mine.resources.size * 0.7).toInt()) {
                resource.location.block.setExit(mine)

                if (gameData.maxMineFloor < mine.floor) {
                    gameData.maxMineFloor = mine.floor
                }
            }

            gatheringPlayers.remove(uniqueId)
            inventory.itemInMainHand.decreaseCustomDurability(1, this)
        }, delay)
    }

    fun getGatheringDelay(pickaxe: ItemStack, type: ResourceType): Long? {
        val sec = when (type) {
            Magnesium, Aluminum -> when {
                isHardPickaxe(pickaxe) -> 4.0
                isLightDrill(pickaxe) -> 4.0
                isHeavyDrill(pickaxe) -> 2.5
                else -> return null
            }

            Iron, Copper, Lithium -> when {
                isHardPickaxe(pickaxe) -> 6.0
                isLightDrill(pickaxe) -> 6.0
                isHeavyDrill(pickaxe) -> 4.0
                else -> return null
            }

            Gold -> when {
                isHardPickaxe(pickaxe) -> 8.0
                isLightDrill(pickaxe) -> 8.0
                isHeavyDrill(pickaxe) -> 6.0
                else -> return null
            }

            Platinum -> when {
                isLightDrill(pickaxe) -> 8.0
                isHeavyDrill(pickaxe) -> 6.0
                else -> return null
            }

            Nickel, Titanium -> when {
                isLightDrill(pickaxe) -> 10.0
                isHeavyDrill(pickaxe) -> 6.0
                else -> return null
            }
        }
        return (sec * 20).toLong()
    }
    fun isHardPickaxe(item: ItemStack): Boolean {
        return item.getCustomModelData() == PICKAXE_MODEL_DATA
    }
    fun isLightDrill(item: ItemStack): Boolean {
        return item.getCustomModelData() == LIGHTDRILL_MODEL_DATA
    }
    fun isHeavyDrill(item: ItemStack): Boolean {
        return item.getCustomModelData() == HEAVYDRILL_MODEL_DATA
    }

    fun showMineFloorSelector(player: Player, page: Int = 0) {
        val floorsPerPage = 45

        val accessibleFloors = mines.filter {
            it.floor == 1 || mines.any { m -> m.floor == it.floor - 1 && m.floor <= gameData.maxMineFloor }
        }.distinctBy { it.floor }.sortedBy { it.floor }

        val totalPages = (accessibleFloors.size - 1) / floorsPerPage + 1
        val inventory = Bukkit.createInventory(null, 54, "탐험할 층을 선택하세요 - ${page + 1}/$totalPages")

        accessibleFloors.drop(page * floorsPerPage).take(floorsPerPage).forEachIndexed { index, mine ->
            val item = ItemStack(Material.STONE).apply {
                itemMeta = itemMeta?.apply { setDisplayName("${mine.floor}층") }
            }
            inventory.setItem(index, item)
        }

        if (page > 0) inventory.setItem(45, ItemStack(Material.ARROW).apply {
            itemMeta = itemMeta?.apply { setDisplayName("이전 페이지") }
        })

        if (page < totalPages - 1) inventory.setItem(53, ItemStack(Material.ARROW).apply {
            itemMeta = itemMeta?.apply { setDisplayName("다음 페이지") }
        })

        player.setMetadata("mine_select_page", FixedMetadataValue(StardewValley.instance, page))
        player.openInventory(inventory)
    }

    private fun Block.setExit(mine: Mine) {
        type = Material.WAXED_COPPER_BULB
        mine.exitBlockLocation = location
    }

    fun Resource.getItemDisplay(): ItemDisplay? {
        val uuid = UUID.fromString(uuidString)
        val entity = Bukkit.getEntity(uuid)
        if (entity !is ItemDisplay) return null
        return entity
    }
}
