package org.beobma.stardewvalleyproject.config

import org.beobma.stardewvalleyproject.manager.DefaultPlantHanler
import org.beobma.stardewvalleyproject.manager.PlantManager
import org.beobma.stardewvalleyproject.plant.WheatPlant

class PlantConfig {
    private val plantManager = PlantManager(DefaultPlantHanler())
    
    init {
        plantConfig()
    }

    private fun plantConfig() {
        plantManager.run {
            WheatPlant().register()
            // 다른 작물 등록
        }
    }
}