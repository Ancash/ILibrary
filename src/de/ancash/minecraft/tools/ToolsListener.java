package de.ancash.minecraft.tools;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

/**
 * The event listener class responsible for firing events in tools
 */
public abstract class ToolsListener extends Tool implements Listener {

	// -------------------------------------------------------------------------------------------
	// Main tool listener
	// -------------------------------------------------------------------------------------------

	private static final long serialVersionUID = 7942610690526450350L;

	/**
	 * Handles clicking tools and shooting rocket
	 *
	 * @param event
	 * @throws Exception 
	 */
	@SuppressWarnings("deprecation")
	@EventHandler()
	public void onToolClick(final PlayerInteractEvent event) throws Exception {
		final Player player = event.getPlayer();
		final Tool tool = ToolRegistry.getTool(player.getItemInHand());

		if (tool != null)
			try {
				if (event.isCancelled() && tool.ignoreCancelled())
					return;
				Action action = event.getAction();
				Block block = event.getClickedBlock();
				if (action == Action.RIGHT_CLICK_BLOCK)
					onBlockClick(player, ClickType.RIGHT, block);

				else if (action == Action.LEFT_CLICK_BLOCK)
					onBlockClick(player, ClickType.LEFT, block);

				else if (action == Action.RIGHT_CLICK_AIR)
					onAirClick(player, ClickType.RIGHT);

				else if (action == Action.LEFT_CLICK_AIR)
				if (tool.autoCancel())
					event.setCancelled(true);

			} catch (final Throwable t) {
				event.setCancelled(true);
				throw new Exception("Failed to handle " + event.getAction() + " using Tool: " + tool.getClass());
			}
	}

	/**
	 * Called automatically when a player holding this tool clicks
	 * a block. The {@link ClickType} can only be RIGHT or LEFT here.
	 *lol
	 * @param player
	 * @param click
	 * @param block
	 */
	
	protected abstract void onBlockClick(Player player, ClickType click, Block block);

	/**
	 * Called automatically when a player clicks the air
	 *
	 * @param player
	 * @param click
	 */
	protected abstract void onAirClick(final Player player, final ClickType click);

	/**
	 * Called when the player swap items in their hotbar and the new slot matches
	 * this tool.
	 *
	 * @param player the player
	 */
	protected abstract void onHotbarFocused(final Player player);

	/**
	 * Called when the player the tool is out of focus at hotbar
	 *
	 * @param player the player
	 */
	protected abstract void onHotbarDefocused(final Player player);
	
	/**
	 * Handles hotbar focus/defocus for tools
	 */
	@EventHandler()
	public void onHeltItem(final PlayerItemHeldEvent event) {
		final Player player = event.getPlayer();

		final Tool current = ToolRegistry.getTool(player.getInventory().getItem(event.getNewSlot()));
		final Tool previous = ToolRegistry.getTool(player.getInventory().getItem(event.getPreviousSlot()));

		// Player has attained focus
		if (current != null) {

			if (previous != null) {

				// Not really
				if (previous.equals(current))
					return;

				previous.onHotbarDefocused(player);
			}

			current.onHotbarFocused(player);
		}
		// Player lost focus
		else if (previous != null)
			previous.onHotbarDefocused(player);
	}
}
