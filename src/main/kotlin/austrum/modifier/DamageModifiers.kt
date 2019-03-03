package austrum.modifier

import austrum.model.*

interface BattleActionModifier {
    fun apply(action: BattleAction): BattleAction
}

class MultiplyDamageType(private val type: DamageType,
                         private val value: Double) : BattleActionModifier
{
    override fun apply(action: BattleAction): BattleAction {
        val damage = action.damage.modify(type) { it.multiply(value) }
        return action.copy(damage = damage)
    }
}

class AddDamageType(private val type: DamageType,
                    private val value: Int) : BattleActionModifier
{
    override fun apply(action: BattleAction): BattleAction {
        val damage = action.damage.modify(type) { it.add(value) }
        return action.copy(damage = damage)
    }
}

// {"MultiplyDamageType": {"type": "fire", "value": 2}}
// {"ModifyDamageTypes": {"multiply": {"fire": 2}}}
class ModifyDamageTypes(private val multiply: Map<DamageType, Double>,
                        private val add: Map<DamageType, Int>): BattleActionModifier
{
    override fun apply(action: BattleAction): BattleAction {
        var newAction = action.copy()
        multiply.forEach { type, value ->
            newAction = MultiplyDamageType(type, value).apply(newAction)
        }
        add.forEach { type, value ->
            newAction = AddDamageType(type, value).apply(newAction)
        }

        return newAction
    }
}

interface PassiveSkill
{
    fun canExecute(actor: BattleActor) = true

    fun weaponAttackFired(): List<BattleActionModifier> {
        return emptyList()
    }
}

class BurningSlash : PassiveSkill
{
    override fun canExecute(actor: BattleActor): Boolean {
        //return actor.weapon.hasTag("sword")
        return true
    }

    override fun weaponAttackFired(): List<BattleActionModifier> {
        return listOf(
            MultiplyDamageType(DamageTypes.fire, 2.0),
            AddDamageType(DamageTypes.slash, 15)
        )
    }
}