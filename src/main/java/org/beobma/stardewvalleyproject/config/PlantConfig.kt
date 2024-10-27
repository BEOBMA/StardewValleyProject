package org.beobma.stardewvalleyproject.config

import org.beobma.stardewvalleyproject.manager.PlantManager.register
import org.beobma.stardewvalleyproject.plant.BitPlant
import org.beobma.stardewvalleyproject.plant.CabbagePlant
import org.beobma.stardewvalleyproject.plant.CoffeeBeansPlant
import org.beobma.stardewvalleyproject.plant.CornerPlant
import org.beobma.stardewvalleyproject.plant.CranberryPlant
import org.beobma.stardewvalleyproject.plant.CucumberPlant
import org.beobma.stardewvalleyproject.plant.PotatoPlant
import org.beobma.stardewvalleyproject.plant.PumpkinPlant
import org.beobma.stardewvalleyproject.plant.TomatoPlant
import org.beobma.stardewvalleyproject.plant.WheatPlant

class PlantConfig {
    init {
        plantConfig()
    }

    private fun plantConfig() {
        BitPlant().register()
        CabbagePlant().register()
        CoffeeBeansPlant().register()
        CornerPlant().register()
        CranberryPlant().register()
        CucumberPlant().register()
        PotatoPlant().register()
        PumpkinPlant().register()
        TomatoPlant().register()
        WheatPlant().register()
    }
}