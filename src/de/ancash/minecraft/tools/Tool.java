package de.ancash.minecraft.tools;

import java.io.Serializable;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a tool. A tool is a simple ItemStack that is registered within the
 * plugin and fires automatic events
 */
public abstract class Tool implements Serializable{

	private static final long serialVersionUID = 5631810102502372807L;

	/**
	 * Create a new tool
	 */
	protected Tool() {

		// A hacky way of automatically registering it AFTER the parent constructor, assuming all went okay
		new Thread(() -> {

			try {
				Thread.sleep(3);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}

			final Tool instance = Tool.this;

			if (!ToolRegistry.isRegistered(instance))
				ToolRegistry.register(instance);
		}).start();
	}

	/**
	 * Evaluates the given itemstack whether it is this tool
	 *
	 * @param item the itemstack
	 * @return true if this tool is the given itemstack
	 */
	public boolean isTool(final ItemStack item) {
		return item.isSimilar(getItem());
	}

	/**
	 * Return true if the given player holds this tool in his main hand
	 *
	 * @param player
	 * @return
	 */
	public boolean hasToolInHand(final Player player) {
		return isTool(player.getItemInHand());
	}

	/**
	 * Get the tool item
	 * <p>
	 * TIP: Use {@link ItemCreator}
	 *
	 * @return the tool item
	 */
	public abstract ItemStack getItem();

	/**
	 * Called automatically when a player holding this tool clicks
	 * a block. The {@link ClickType} can only be RIGHT or LEFT here.
	 *
	 * @param player
	 * @param click
	 * @param block
	 */
	protected void onBlockClick(Player player, ClickType click, Block block) {
	}

	/**
	 * Called automatically when a player clicks the air
	 *
	 * @param player
	 * @param click
	 */
	protected void onAirClick(final Player player, final ClickType click) {
	}

	/**
	 * Called when the player swap items in their hotbar and the new slot matches
	 * this tool.
	 *
	 * @param player the player
	 */
	protected void onHotbarFocused(final Player player) {
	}

	/**
	 * Called when the player the tool is out of focus at hotbar
	 *
	 * @param player the player
	 */
	protected void onHotbarDefocused(final Player player) {
	}

	/**
	 * Should we fire {@link #onBlockClick(PlayerInteractEvent)} even on cancelled
	 * events?
	 * <p>
	 * True by default. Set to false if you want to catch clicking air.
	 *
	 * @return true if we should ignore the click event if it was cancelled
	 */
	protected boolean ignoreCancelled() {
		return true;
	}

	/**
	 * A convenience method, should we automatically cancel the
	 * {@link PlayerInteractEvent} ?
	 *
	 * @return true if the interact event should be cancelled automatically false by
	 * default
	 */
	protected boolean autoCancel() {
		return false;
	}

	/**
	 * Convenience method for quickly setting this tool to a specific slot of players inventory
	 *
	 * @param player
	 */
	public final void give(final Player player, final int slot) {
		player.getInventory().setItem(slot, getItem());
	}

	/**
	 * Convenience method for quickly adding this tool into a players inventory
	 *
	 * @param player
	 */
	public final void give(final Player player) {
		player.getInventory().addItem(getItem());
	}

	/**
	 * Returns true if the compared object is a tool with the same {@link #getItem()}
	 *
	 * @param obj
	 * @return
	 */
	@Override
	public final boolean equals(final Object obj) {
		return obj instanceof Tool && ((Tool) obj).getItem().isSimilar(getItem());
	}
}
