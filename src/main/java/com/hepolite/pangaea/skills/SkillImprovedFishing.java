package com.hepolite.pangaea.skills;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import com.hepolite.mmob.handlers.LootDropHandler;
import com.hepolite.pangaea.utility.LootHelper;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillImprovedFishing extends Skill
{
	public SkillImprovedFishing()
	{
		super("Improved Fishing");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerFish(PlayerFishEvent event)
	{
		if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH || !(event.getCaught() instanceof Item))
			return;
		Player player = event.getPlayer();
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (race == null || skill == null)
			return;
		Item item = (Item) event.getCaught();
		ItemStack stack = item.getItemStack();
		String path = race.getData().getName() + "." + getName() + ".";

		Material type = stack.getType();
		if (type == Material.RAW_FISH)
		{
			int fishAmount = getSettings().getInt(path + "fishAmount");
			stack.setAmount(stack.getAmount() + random.nextInt(fishAmount));
		}
		else
		{
			String section = LootHelper.getLootSection(getSettings(), path + "Loot");
			if (section.equalsIgnoreCase("default"))
				;
			else if (section.equalsIgnoreCase("mmob"))
			{
				String mainGroup = LootHelper.getLootSection(getSettings(), path + "Loot.mmob");
				String negativeGroup = getSettings().getString(path + "Loot.mmob.negativeGroup");
				stack = LootDropHandler.getRandomItem(mainGroup);
				if (random.nextFloat() < getSettings().getFloat(path + "Loot.mmob.negativeChance"))
					LootDropHandler.applyRandomItemEffect(stack, negativeGroup);
				if (random.nextFloat() < getSettings().getFloat(path + "Loot.mmob.negativeChanceDouble"))
					LootDropHandler.applyRandomItemEffect(stack, negativeGroup);
			}
			else if (section.equalsIgnoreCase("item"))
				stack = getSettings().getItem(path + "Loot.item." + LootHelper.getLootSection(getSettings(), path + "Loot.item"));
		}

		if (stack != null)
			item.setItemStack(stack);
	}
}
