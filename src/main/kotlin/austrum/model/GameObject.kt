package austrum.model

import java.util.*

abstract class GameObject
{
    val uuid = UUID.randomUUID()
}

enum class EquipSlot(val title: String, val categories: Array<EquipCategory>)
{
    HEAD("Head"),
    BODY("Body"),
    RHAND("Right Hand"),
    LHAND("Left Hand"),
    LEGS("Legs"),
    FEET("Feet"),

    // necklaces, charms, rings, bracelets, belts, gloves, capes
    ACC1("Accessory 1"),
    ACC2("Accessory 2"),
    ACC3("Accessory 3"),
}

enum class EquipCategory(val title: String, val slotBlock: Array<EquipSlot> = emptyArray())
{
    CAP("Cap"),
    HEADGEAR("Headgear"),
    HELMET("Helmet"),

    SHIRT("Shirt"),
    ROBE("Robe"),
    LIGHTARMOR("Light Armor"),
    HEAVYARMOR("Heavy Armor"),

    SHORTSWORD("Sword"),
    SWORD2H("2-Handed Sword", arrayOf(EquipSlot.LHAND)),


    // greaves, cuisses. yeah they separate shins and thighs separately but w/e.
    PANTS("Pants"),
    LIGHTLEGGINGS("Light Leggings"),
    HEAVYLEGGINGS("Heavy Leggings"),

    SHOES("Shoes"),
    LIGHTBOOTS("Light Boots"),
    HEAVYBOOTS("Heavy Boots"),

    NECKLACE("Necklace"),
    CHARM("Charm"),
    RING("Ring"),
    BRACELET("Bracelet"),
    BELT("Belt"),
    GLOVES("Gloves"),
    CAPE("Cape"),
}