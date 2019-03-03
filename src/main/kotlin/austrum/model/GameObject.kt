package austrum.model

import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt

abstract class GameObject
{
    val uuid = UUID.randomUUID()
}

// many attacks are hybrid-type.
// like a silver sword being slash + silver, or an ice needle spell being pierce + cold.
enum class AttackCategory(val title: String)
{
    PHYSICAL("Physical"),
    MAGICAL("Magical")
}

data class DamageType(val id: String, val name: String, val category: AttackCategory): GameObject()

// this will be dynamic
object DamageTypes
{
    val slash = DamageType("slash", "Slash", AttackCategory.PHYSICAL)
    val pierce = DamageType("pierce", "Pierce", AttackCategory.PHYSICAL)
    val strike = DamageType("strike", "Strike", AttackCategory.PHYSICAL)

    val fire = DamageType("fire", "Fire", AttackCategory.MAGICAL)
    val cold = DamageType("cold", "Cold", AttackCategory.MAGICAL)
}

data class Item(val name: String,
                val weaponDamage: List<RandomDamage>): GameObject()

// main "character" object
data class Actor(val name: String)

// character object in-battle. can override stats like current HP, strength, status effects, etc.
data class BattleActor(val actor: Actor)

// an action that was taken in battle. an "in-progress" action that is "between" source and target(s).
// immutable? that way some type of middleware could execute for each target and it would pass a NEW
// battle action down the line to affect the target.
data class BattleAction(val source: BattleActor, val damage: DamageList)

typealias DamageList = List<RandomDamage>

fun DamageList.modify(type: DamageType, fn: (RandomDamage) -> RandomDamage): DamageList
{
    return this.map {
        if ( it.type == type ) {
            fn(it)
        } else {
            it
        }
    }
}

data class RandomDamage(val type: DamageType, val range: IntRange) {
    fun damage() = Damage(type, Random.nextInt(range))

    fun add(value: Int) =
        RandomDamage(type, IntRange(range.start + value, range.endInclusive + value))

    fun multiply(value: Double) =
        RandomDamage(type, IntRange((range.start * value).toInt(), (range.endInclusive * value).toInt()))

    fun multiply(value: Int) =
        RandomDamage(type, IntRange(range.start * value, range.endInclusive * value))
}



data class Damage(val type: DamageType, val value: Int)

enum class EquipSlot(val title: String, val categories: Array<EquipCategory>)
{
    HEAD("Head", emptyArray()),
    BODY("Body", emptyArray()),
    RHAND("Right Hand", emptyArray()),
    LHAND("Left Hand", emptyArray()),
    LEGS("Legs", emptyArray()),
    FEET("Feet", emptyArray()),

    // necklaces, charms, rings, bracelets, belts, gloves, capes
    ACC1("Accessory 1", emptyArray()),
    ACC2("Accessory 2", emptyArray()),
    ACC3("Accessory 3", emptyArray()),
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