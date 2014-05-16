package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.result.Result;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HasItemRequirement extends Requirement {

	ItemStack item = null;
	private boolean optional = false;
	private boolean autoComplete = false;
	private boolean showShortValue = false;
	private String displayName = null;
	private int reqId;
	List<Result> results = new ArrayList<Result>();

	@SuppressWarnings("deprecation")
	@Override
	public boolean setOptions(final String[] options, final boolean optional,
			final List<Result> results, final boolean autoComplete,
			final int reqId) {
		int id = 0;
		int amount = 1;
		short data = 0;
		this.reqId = reqId;

		this.optional = optional;
		this.results = results;
		this.autoComplete = autoComplete;

		if (options.length > 0)
			id = AutorankTools.stringtoInt(options[0]);
		if (options.length > 1)
			amount = AutorankTools.stringtoInt(options[1]);
		if (options.length > 2) {
			data = (short) AutorankTools.stringtoInt(options[2]);
			// Short value can make a difference, thus we show it.
			showShortValue = true;
		}
		if (options.length > 3) {
			// Displayname
			displayName = options[3];
		}

		//item = new ItemStack(id, 1, (short) 0, data);
		item = new ItemStack(id, amount, data);

		return item != null;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		return item != null && player.getInventory().containsAtLeast(item, item.getAmount());
	}

	@Override
	public String getDescription() {
		StringBuilder arg = new StringBuilder(item.getAmount() + " ");
		
		if (displayName != null) {
			// Show displayname instead of material name
			arg.append(displayName);
		} else {
		
			arg.append(item.getType().toString());	
			
			if (showShortValue) {
				arg.append(" (Dam. value: " + item.getDurability() + ")");
			}
		}
		
		return Lang.ITEM_REQUIREMENT.getConfigValue(new String[] { arg.toString() });
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

	@Override
	public List<Result> getResults() {
		return results;
	}

	@Override
	public String getProgress(final Player player) {
		int firstSlot = player.getInventory().first(item.getType());
		int slotAmount = 0;
		
		if (firstSlot >= 0) {
			slotAmount = player.getInventory().getItem(firstSlot).getAmount();	
		}
		
		String progress = "";
		progress = progress
				.concat(slotAmount + "/" + item.getAmount());
		return progress;
	}

	@Override
	public boolean useAutoCompletion() {
		return autoComplete;
	}

	@Override
	public int getReqId() {
		return reqId;
	}
}
