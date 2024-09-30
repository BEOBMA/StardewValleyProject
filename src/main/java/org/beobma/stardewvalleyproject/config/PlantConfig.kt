package org.beobma.stardewvalleyproject.config

import org.beobma.stardewvalleyproject.manager.PlantManager.register
import org.beobma.stardewvalleyproject.plant.WheatPlant

class PlantConfig {
    init {
        plantConfig()
    }

    private fun plantConfig() {
        WheatPlant().register()
    }
}