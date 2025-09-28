package org.beobma.stardewvalleyproject.manager

import kr.eme.semiMission.objects.events.MissionEvent
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
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f
import java.util.*
import kotlin.math.ceil
import kotlin.random.Random

object MineManager {
    private const val MAX_MINE_FLOOR = 60
    private const val CALCULATE_OFFSET = 160.0
    private const val TICKINTERVAL = 10L

    private val world = Bukkit.getWorlds().first()
    private val miniMessage = MiniMessage.miniMessage()

    val gatheringPlayers = mutableSetOf<UUID>()

    /** 초기화 or 로드 */
    fun reset() {
        if (mines.isNotEmpty()) {
            loadData()
            return
        }
        mines.forEach { mine ->
            mine.resources.forEach { resource -> resource.getItemDisplay()?.remove() }
            removeItemDisplays(mine)
        }
        mines.clear()
        mines.addAll(generateMines())
    }

    /** 다음날 */
    fun nextDay() {
        val start = System.currentTimeMillis()

        mines.toList().forEach { mine ->
            if (mine.players.isEmpty()) return@forEach
            mine.players.toList().forEach { player ->
                player.leaveMine(mine)
            }
        }
        val newMines = generateMines()

        mines.clear()
        mines.addAll(newMines)

        val elapsed = System.currentTimeMillis() - start
        StardewValley.instance.loggerMessage("nextDay completed in ${elapsed}ms")
    }

    /** 저장 데이터 적용 */
    fun loadData() {
        mines.forEach { mine ->
            mine.startBlockLocation?.block?.type = Material.BARRIER
            mine.exitBlockLocation?.block?.type = Material.BARRIER
        }
    }

    /** 특정 층으로 이동 처리 */
    fun Player.approach(currentMine: Mine?, floor: Int, isExit: Boolean = false): Boolean {
        val home = Location(this.world, -191.0, -56.0, 95.0)

        fun teleportHome(): Boolean {
            this.teleport(home)
            return false
        }

        // 0층
        if (floor == 0) {
            currentMine?.let { leaveMine(it) }
            return teleportHome()
        }

        val nextMine = mines.firstOrNull { it.floor == floor } ?: return teleportHome()

        if (currentMine != null && currentMine !== nextMine) {
            leaveMine(currentMine)
        }

        val base = if (isExit) nextMine.exitBlockLocation else nextMine.startBlockLocation
        val target = base?.clone()?.add(0.0, 1.0, 0.0) ?: return teleportHome()

        if (this !in nextMine.players) {
            nextMine.players.add(this)
        }

        // 미션 트리거
        Bukkit.getPluginManager().callEvent(MissionEvent(this, "PLAYER_PROGRESS", "mine_module", nextMine.floor))


        this.teleport(target)
        nextMine.spawnVisuals()
        return true
    }

    /** 자원 배치 */
    private fun Mine.addResource() {
        val zOffset = calculateOffset(floor)
        mineType.resourcesLocations.forEach { (x, y, z) ->
            val location = Location(world, x, y, z - zOffset)
            val type = getRandomForFloor(floor)
            val resource = Resource(type, location)
            resources.add(resource)
        }
    }

    /** 층 자원 확률 */
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

    /** 적 배치 데이터 */
    private fun Mine.addEnemyData() {
        val zOffset = calculateOffset(floor)
        mineType.enemysLocations.forEach { (x, y, z) ->
            val spawnLocation = Location(world, x, y, z - zOffset)
            val enemy = Enemy(spawnLocation, getEntityTypeForFloor(floor))
            enemys.add(enemy)
        }
    }

    /** 층수 기반 엔티티 */
    private fun getEntityTypeForFloor(floor: Int): EntityType {
        return when ((floor - 1) % 15) {
            in 0..4 -> EntityType.DROWNED
            in 5..9 -> EntityType.HUSK
            else -> EntityType.ZOMBIE
        }
    }

    /** 퇴장 */
    fun Player.leaveMine(mine: Mine) {
        mine.players.remove(this)
        mine.removeVisuals()
        mine.enemys.filter { it.isSpawn && !it.isDead && it.enemyUUID != null }.forEach {
            it.isSpawn = false
            Bukkit.getEntity(UUID.fromString(it.enemyUUID))?.remove()
        }
    }

    /** 적 스폰 */
    private fun Mine.spawnEnemys() {
        enemys.filter { !it.isSpawn && !it.isDead }.forEach {
            val entity = world.spawnEntity(it.location, it.entityType)
            it.isSpawn = true
            it.enemyUUID = entity.uniqueId.toString()
        }
    }

    /** 자원 채굴 */
    fun Player.gathering(resource: Resource) {
        if (resource.isGathering || gatheringPlayers.contains(this.uniqueId)) return

        val mainHand = inventory.itemInMainHand
        val customModelData = mainHand.getCustomModelData()
        if (customModelData !in PICKAXE_MODEL_DATAS) return

        val mine = mines.find { it.players.contains(this) } ?: return
        val delay = getGatheringDelay(mainHand, resource.resourcesType) ?: return

        gatheringPlayers.add(uniqueId)

        // 타격 사운드/파티클 반복
        val soundTask = Bukkit.getScheduler().runTaskTimer(
            StardewValley.instance,
            Runnable {
                playSound(location, Sound.BLOCK_STONE_HIT, 1f, 1f)
                world.spawnParticle(
                    Particle.BLOCK, resource.location, 10,
                    0.2, 0.2, 0.2, Material.STONE.createBlockData()
                )
            },
            0L, TICKINTERVAL
        )

        // 지연 완료 후 채집 완료 처리
        Bukkit.getScheduler().runTaskLater(StardewValley.instance, Runnable {
            soundTask.cancel()

            val itemStack = ItemStack(Material.RED_DYE).apply {
                itemMeta = itemMeta.apply {
                    displayName(miniMessage.deserialize(resource.resourcesType.displayName))
                    setCustomModelData(resource.resourcesType.customModelData)
                }
            }

            Bukkit.getPluginManager().callEvent(MissionEvent(this, "PLAYER_PROGRESS", "mine_module", 1))

            resource.getItemDisplay()?.remove()

            inventory.addItem(itemStack)
            resource.isGathering = true
            resource.location.block.type = Material.AIR

            playSound(location, Sound.BLOCK_STONE_BREAK, 1f, 1f)
            world.spawnParticle(Particle.BLOCK, resource.location, 30, 0.3, 0.3, 0.3, Material.STONE.createBlockData())

            val gathered = mine.resources.count { it.isGathering }
            if (mine.exitBlockLocation == null && gathered >= ceil(mine.resources.size * 0.7).toInt()) {
                if (mine.floor < MAX_MINE_FLOOR) {
                    resource.location.block.setExit(mine)
                    if (gameData.maxMineFloor < mine.floor) gameData.maxMineFloor = mine.floor
                }
            }

            gatheringPlayers.remove(uniqueId)
            inventory.itemInMainHand.decreaseCustomDurability(1, this)
        }, delay)
    }

    /** 채집 지연 시간 */
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

    /** 단단한 곡괭이 판정 */
    fun isHardPickaxe(item: ItemStack): Boolean = item.getCustomModelData() == PICKAXE_MODEL_DATA

    /** 경량 드릴 판정 */
    fun isLightDrill(item: ItemStack): Boolean = item.getCustomModelData() == LIGHTDRILL_MODEL_DATA

    /** 중량 드릴 판정 */
    fun isHeavyDrill(item: ItemStack): Boolean = item.getCustomModelData() == HEAVYDRILL_MODEL_DATA

    /** 층 선택 인벤토리 */
    fun showMineFloorSelector(player: Player) {
        val accessibleFloors = mines
            .filter { it.floor == 1 || mines.any { m -> m.floor == it.floor - 1 && m.floor <= gameData.maxMineFloor } }
            .distinctBy { it.floor }
            .sortedBy { it.floor }

        val validFloors = accessibleFloors
            .map { it.floor }
            .filter { it % 5 == 1 }
            .filter { it <= gameData.maxMineFloor }
            .toMutableSet()

        validFloors.add(1)
        val sortedFloors = validFloors.sorted()

        val slotIndices = listOf(4, 5, 6, 7, 13, 14, 15, 16, 22, 23, 24, 25)
        val inventory = Bukkit.createInventory(null, 27, miniMessage.deserialize("mineShow"))

        for ((i, floor) in sortedFloors.withIndex()) {
            if (i >= slotIndices.size) break
            val item = ItemStack(Material.GLASS_PANE).apply {
                itemMeta = itemMeta?.apply {
                    displayName(miniMessage.deserialize("${floor}층"))
                    setCustomModelData(1)
                }
            }
            inventory.setItem(slotIndices[i], item)
        }

        val item = ItemStack(Material.IRON_HORSE_ARMOR).apply {
            itemMeta = itemMeta?.apply {
                displayName(miniMessage.deserialize(""))
                setCustomModelData(10)
            }
        }
        inventory.setItem(10, item)

        player.openInventory(inventory)
    }

    /** 출구 */
    private fun Block.setExit(mine: Mine) {
        type = Material.BARRIER
        mine.exitBlockLocation = location

        val display = createItemDisplay(
            location, Material.LEATHER_HORSE_ARMOR, 6,
            Vector3f(1.5f, 1.5f, 1.5f), 0.5, 0.9, 0.5
        )
        mine.exitBlockUUID = display.uniqueId.toString()

        val marker = createItemDisplay(
            location, Material.LEATHER_HORSE_ARMOR, 3,
            Vector3f(1.75f, 1.75f, 1.75f), 0.5, 2.5, 0.5
        ).apply {
            billboard = Display.Billboard.VERTICAL
            brightness = Display.Brightness(15, 15)
        }
        mine.exitBlockMarker = marker.uniqueId.toString()
    }

    /** 자원 디스플레이 조회 */
    fun Resource.getItemDisplay(): ItemDisplay? {
        if (uuidString == null) return null
        val uuid = UUID.fromString(uuidString)
        val entity = Bukkit.getEntity(uuid)
        if (entity !is ItemDisplay) return null
        return entity
    }

    /** 디스플레이 조회 */
    fun getItemDisplayToUUID(uuid: String): ItemDisplay? {
        val id = UUID.fromString(uuid)
        val entity = Bukkit.getEntity(id)
        if (entity !is ItemDisplay) return null
        return entity
    }

    /** 시각 요소 생성 */
    fun Mine.spawnVisuals() {
        val start = System.currentTimeMillis()
        if (players.size > 1) return

        startBlockLocation?.block?.type = Material.BARRIER
        if (floor != 1) {
            startBlockUUID = createItemDisplay(
                startBlockLocation!!, Material.LEATHER_HORSE_ARMOR, 5,
                Vector3f(3.0f, 3.0f, 3.0f), 0.25, 1.8, 0.25
            ).uniqueId.toString()
        }
        val marker = createItemDisplay(
            startBlockLocation!!, Material.LEATHER_HORSE_ARMOR, 4,
            Vector3f(1.75f, 1.75f, 1.75f), 0.2, 5.0, 0.2
        )
        marker.billboard = Display.Billboard.VERTICAL
        startBlockMarker = marker.uniqueId.toString()

        exitBlockLocation?.block?.type = Material.BARRIER
        exitBlockUUID = exitBlockLocation?.let {
            val display = createItemDisplay(
                it, Material.LEATHER_HORSE_ARMOR, 6,
                Vector3f(1.5f, 1.5f, 1.5f), 0.5, 0.9, 0.5
            )
            display.uniqueId.toString()
        }

        exitBlockMarker = exitBlockLocation?.let {
            val markerDisplay = createItemDisplay(
                it, Material.LEATHER_HORSE_ARMOR, 3,
                Vector3f(1.75f, 1.75f, 1.75f), 0.5, 2.5, 0.5
            )
            markerDisplay.billboard = Display.Billboard.VERTICAL
            markerDisplay.brightness = Display.Brightness(15, 15)
            markerDisplay.uniqueId.toString()
        }

        addResourceDisplays()
        spawnEnemysMine()

        val elapsed = System.currentTimeMillis() - start
        StardewValley.instance.loggerMessage("[Mine] spawnVisuals() for floor $floor took ${elapsed}ms")
    }

    /** 시각 요소 제거 */
    fun Mine.removeVisuals() {
        val start = System.currentTimeMillis()
        if (players.isNotEmpty()) return

        startBlockLocation?.block?.type = Material.AIR
        startBlockUUID?.let { getItemDisplayToUUID(it)?.remove() }
        startBlockMarker?.let { getItemDisplayToUUID(it)?.remove() }
        exitBlockLocation?.block?.type = Material.AIR
        exitBlockUUID?.let { getItemDisplayToUUID(it)?.remove() }
        exitBlockMarker?.let { getItemDisplayToUUID(it)?.remove() }

        resources.forEach {
            it.location.block.type = Material.AIR
            it.getItemDisplay()?.remove()
        }

        val elapsed = System.currentTimeMillis() - start
        StardewValley.instance.loggerMessage("[Mine] removeVisuals() for floor $floor took ${elapsed}ms")
    }

    /** 자원 디스플레이 */
    fun Mine.addResourceDisplays() {
        resources.filter { !it.isGathering }.forEach { resource ->
            resource.location.block.type = Material.BARRIER
            val itemDisplay = world.spawn(resource.location.clone().add(0.5, 0.5, 0.5), ItemDisplay::class.java)
            resource.uuidString = itemDisplay.uniqueId.toString()

            val itemStack = ItemStack(Material.GRAY_DYE).apply {
                itemMeta = itemMeta.apply { setCustomModelData(resource.resourcesType.customModelData) }
            }
            itemDisplay.setItemStack(itemStack)
        }
    }

    /** 적 스폰 실행 */
    fun Mine.spawnEnemysMine() {
        enemys.filter { !it.isSpawn && !it.isDead }.forEach {
            val entity = Bukkit.getWorlds().first().spawnEntity(it.location, it.entityType)
            it.isSpawn = true
            it.enemyUUID = entity.uniqueId.toString()
        }
    }

    /** 아이템 디스플레이 제거 */
    private fun removeItemDisplays(mine: Mine) {
        mine.startBlockLocation?.block?.type = Material.AIR
        mine.exitBlockLocation?.block?.type = Material.AIR
        listOf(mine.startBlockUUID, mine.exitBlockUUID, mine.startBlockMarker, mine.exitBlockMarker)
            .forEach { it?.let { uuid -> getItemDisplayToUUID(uuid)?.remove() } }
    }

    /** 층수 오프셋 계산 */
    private fun calculateOffset(floor: Int): Double = ((floor - 1) / 5) * CALCULATE_OFFSET

    /** 광산 일괄 생성 */
    private fun generateMines(): List<Mine> {
        val templates = arrayOf(MineTemplate.M, MineTemplate.N, MineTemplate.R)
        val types = arrayOf(MineType.A, MineType.B, MineType.C, MineType.D, MineType.E)

        return (1..MAX_MINE_FLOOR).map { floor ->
            val template = templates[(floor - 1) / 5 % templates.size]
            val type = types[(floor - 1) % types.size]
            val zOffset = calculateOffset(floor)

            val mine = Mine(floor, template, type).apply {
                val startX = type.startX
                val startY = type.startY
                val startZ = type.startZ
                startBlockLocation = Location(world, startX, startY, startZ - zOffset)

                addResource()
                addEnemyData()
            }
            mine
        }
    }

    /** 시작 지점 디스플레이 생성 */
    private fun Mine.createItemDisplays(floor: Int, startBlockLocation: Location?) {
        startBlockLocation?.let { loc ->
            val marker = createItemDisplay(
                loc, Material.LEATHER_HORSE_ARMOR, 4,
                Vector3f(1.75f, 1.75f, 1.75f), 0.2, 5.0, 0.2
            )
            marker.billboard = Display.Billboard.VERTICAL
            startBlockMarker = marker.uniqueId.toString()

            if (floor != 1) {
                startBlockUUID = createItemDisplay(
                    loc, Material.LEATHER_HORSE_ARMOR, 5,
                    Vector3f(3.0f, 3.0f, 3.0f), 0.25, 1.8, 0.25
                ).uniqueId.toString()
            }
        }
    }

    /** 아이템 디스플레이 생성 */
    private fun createItemDisplay(
        loc: Location,
        material: Material,
        customModelData: Int,
        scale: Vector3f,
        xOffset: Double,
        yOffset: Double,
        zOffset: Double
    ): ItemDisplay {
        val itemDisplay = world.spawn(loc.clone().add(xOffset, yOffset, zOffset), ItemDisplay::class.java)
        val itemStack = ItemStack(material).apply {
            itemMeta = itemMeta.apply { setCustomModelData(customModelData) }
        }
        itemDisplay.transformation = Transformation(
            Vector3f(0f, 0f, 0f),
            AxisAngle4f(0f, 0f, 0f, 0f),
            scale,
            AxisAngle4f(Math.PI.toFloat(), 0f, 1f, 0f)
        )
        itemDisplay.setItemStack(itemStack)
        return itemDisplay
    }
}