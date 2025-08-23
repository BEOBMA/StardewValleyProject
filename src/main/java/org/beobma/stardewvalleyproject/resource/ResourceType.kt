package org.beobma.stardewvalleyproject.resource

enum class ResourceType(
    val displayName: String,
    val customModelData: Int
) {
    Lithium("리튬", 1),
    Magnesium("마그네슘", 2),
    Nickel("니켈", 3),
    Platinum("백금", 4),
    Aluminum("알루미늄", 5),
    Gold("금", 6),
    Copper("구리", 7),
    Iron("철", 8),
    Titanium("티타늄", 9);
}
