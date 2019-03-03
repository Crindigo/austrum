-- double fire damage

for attack_type, dmg_range in damage do
    if attack_type == 'fire' then
        dmg_range = dmg_range * 2
    end
end

damage.adjust('fire', function(range)
    return range * 2
end)