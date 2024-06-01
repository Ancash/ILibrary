package de.ancash.nbtnexus.packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;

import de.ancash.nbtnexus.NBTNexus;
import de.ancash.nbtnexus.serde.ItemSerializer;

public class InventoryUpdateAdapter extends PacketAdapter implements Listener {

	private ProtocolManager protocolManager;
	private final NBTNexus pl;
	@SuppressWarnings("nls")
	private final String metaKey = "NBTNexusItemComputable";

	public InventoryUpdateAdapter(NBTNexus pl) {
		super(pl.pl, ListenerPriority.HIGHEST, Arrays.asList(PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS));
		protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(this);
		this.pl = pl;
	}

	@SuppressWarnings("nls")
	private void computeItem(StructureModifier<ItemStack> itemStackStructureModifier, Player player) {
		ItemStack original = itemStackStructureModifier.read(0);
		if (original != null && original.getType() != Material.AIR) {
			try {
				itemStackStructureModifier.write(0, ItemComputerRegistry.computeItem(original));
			} catch (Throwable th) {
				pl.pl.getLogger().severe("Could not compute item, using already set item");
				th.printStackTrace();
				try {
					pl.pl.getLogger().severe(ItemSerializer.INSTANCE.serializeItemStack(original).toString());
				} catch (Throwable th2) {
					pl.pl.getLogger().severe(original.toString());
				}
				itemStackStructureModifier.write(0, original);
			}
		}
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
			itemStackStructureModifier.write(0, original);
	}

	@SuppressWarnings({ "nls" })
	private void computeItemList(StructureModifier<List<ItemStack>> itemStackStructureModifier, Player player) {
		List<List<ItemStack>> original = new ArrayList<>();
		for (int i = 0; i < itemStackStructureModifier.size(); i++) {
			List<ItemStack> list = itemStackStructureModifier.read(i);
			original.add(list);
			for (int slot = 0; slot < list.size(); slot++) {
				ItemStack itemStack = list.get(slot);
				if (itemStack != null && itemStack.getType() != Material.AIR) {
					try {
						list.set(slot, ItemComputerRegistry.computeItem(itemStack));
					} catch (Throwable th) {
						pl.pl.getLogger().severe("Could not compute list item, using already set item");
						th.printStackTrace();
						try {
							pl.pl.getLogger().severe(ItemSerializer.INSTANCE.serializeItemStack(itemStack).toString());
						} catch (Throwable th2) {
							pl.pl.getLogger().severe(original.toString());
						}
					}
				}
			}
			itemStackStructureModifier.write(i, list);
		}
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
			for (int i = 0; i < original.size(); i++)
				itemStackStructureModifier.write(i, original.get(i));
	}

	@org.bukkit.event.EventHandler
	public void onGMChange(PlayerGameModeChangeEvent event) {
		Player player = event.getPlayer();
		if ((player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)
				&& (event.getNewGameMode() == GameMode.CREATIVE || event.getNewGameMode() == GameMode.SPECTATOR)) {
			Bukkit.getScheduler().runTaskLater(pl.pl, () -> {
				if (!event.isCancelled())
					player.updateInventory();
			}, 1);
		}
	}

	@org.bukkit.event.EventHandler
	public void onPickUp(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Bukkit.getScheduler().runTaskLater(pl.pl, () -> {
			if (!event.isCancelled())
				((Player) event.getEntity()).updateInventory();
		}, 1);
	}

	private void handleSync(PacketEvent event) {
		PacketContainer packet = event.getPacket();
		if (event.getPacketType().equals(PacketType.Play.Server.WINDOW_ITEMS))
			computeItemList(packet.getItemListModifier(), event.getPlayer());
		else
			computeItem(packet.getItemModifier(), event.getPlayer());
	}

	@Override
	public void onPacketSending(PacketEvent event) {
		PacketContainer packet = event.getPacket();
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode() == GameMode.SPECTATOR)
			return;
		if (packet.getIntegers().read(0) != 0) // ignore everything that is not player inv window id (0)
			return;

		if (!pl.enableExperimentalPacketEditing())
			return;
		handleSync(event);
	}

	public void disable() {
		protocolManager.removePacketListeners(pl.pl);
	}
}
