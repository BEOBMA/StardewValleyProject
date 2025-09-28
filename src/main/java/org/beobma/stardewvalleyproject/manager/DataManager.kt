package org.beobma.stardewvalleyproject.manager

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.beobma.stardewvalleyproject.StardewValley
import org.beobma.stardewvalleyproject.data.GameData
import org.beobma.stardewvalleyproject.data.LocationListSerializer
import org.beobma.stardewvalleyproject.mine.Mine
import org.beobma.stardewvalleyproject.plant.Plant
import org.beobma.stardewvalleyproject.util.Season
import org.bukkit.Location
import org.bukkit.entity.Player
import java.io.File
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object DataManager {
    private val dataFolder = File(StardewValley.instance.dataFolder, "data")
    private val json = Json { prettyPrint = true; encodeDefaults = true; ignoreUnknownKeys = true }

    private val secretKey: SecretKey = SecretKeySpec("SJKWP1245434321O".toByteArray(Charsets.UTF_8), "AES")
    private const val CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val IV_SIZE = 16

    // 데이터
    var gameData: GameData = defaultGameMeta()
    var mines: MutableList<Mine> = mutableListOf()
    var plantList: MutableList<Plant> = mutableListOf()
    var interactionFarmlands: MutableList<Location> = mutableListOf()
    var playerList: MutableList<Player> = mutableListOf()

    /** 전체 저장 */
    fun saveAll() {
        saveEncrypted("data.dat", gameData)
        saveEncrypted("mines.dat", mines)
        saveEncrypted("plants.dat", plantList)
        saveEncrypted("interaction_locs.dat", interactionFarmlands, LocationListSerializer)
    }

    /** 전체 로드 */
    fun loadAll() {
        gameData = loadEncrypted("data.dat") ?: defaultGameMeta()
        mines = loadEncrypted("mines.dat") ?: mutableListOf()
        plantList = loadEncrypted("plants.dat") ?: mutableListOf()
        interactionFarmlands = (loadEncrypted("interaction_locs.dat", LocationListSerializer) ?: mutableListOf()).toMutableList()
    }

    /** 저장 */
    private inline fun <reified T> saveEncrypted(
        fileName: String,
        data: T,
        serializer: KSerializer<T>? = null
    ) {
        if (!dataFolder.exists()) dataFolder.mkdirs()
        val file = File(dataFolder, fileName)

        val plain = if (serializer != null) json.encodeToString(serializer, data) else json.encodeToString(data)

        val iv = ByteArray(IV_SIZE).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM).apply {
            init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))
        }
        val encrypted = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))

        file.writeBytes(iv + encrypted)
        StardewValley.instance.loggerMessage("Encrypted save '$fileName' (${plain.toByteArray().size}→${encrypted.size} bytes)")
    }

    /** 로드 */
    private inline fun <reified T> loadEncrypted(
        fileName: String,
        serializer: KSerializer<T>? = null
    ): T? {
        val file = File(dataFolder, fileName)
        if (!file.exists()) return null

        val raw = file.readBytes()
        if (raw.size < IV_SIZE + 1) return null

        val iv = raw.copyOfRange(0, IV_SIZE)
        val encrypted = raw.copyOfRange(IV_SIZE, raw.size)

        val cipher = Cipher.getInstance(CIPHER_ALGORITHM).apply {
            init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
        }
        val plainBytes = cipher.doFinal(encrypted)
        val plainText = plainBytes.toString(Charsets.UTF_8)

        val result: T = if (serializer != null) json.decodeFromString(serializer, plainText) else json.decodeFromString(plainText)

        StardewValley.instance.loggerMessage("Decrypted load '$fileName' (${encrypted.size}→${plainBytes.size} bytes)")
        return result
    }

    /** 기본 메타 데이터 생성 */
    private fun defaultGameMeta() = GameData(6, 0, Season.Spring, 1)
}