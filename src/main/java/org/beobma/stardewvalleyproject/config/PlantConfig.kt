package org.beobma.stardewvalleyproject.config

import org.beobma.stardewvalleyproject.manager.PlantManager.register
import org.beobma.stardewvalleyproject.plant.list.BitPlant
import org.beobma.stardewvalleyproject.plant.list.CabbagePlant
import org.beobma.stardewvalleyproject.plant.list.CoffeeBeansPlant
import org.beobma.stardewvalleyproject.plant.list.CornerPlant
import org.beobma.stardewvalleyproject.plant.list.CranberryPlant
import org.beobma.stardewvalleyproject.plant.list.CucumberPlant
import org.beobma.stardewvalleyproject.plant.list.PotatoPlant
import org.beobma.stardewvalleyproject.plant.list.PumpkinPlant
import org.beobma.stardewvalleyproject.plant.list.TomatoPlant
import org.beobma.stardewvalleyproject.plant.list.WheatPlant

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