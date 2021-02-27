package de.ancash.ilibrary.minecraft.nbt;

import org.bukkit.Chunk;
import org.bukkit.persistence.PersistentDataContainer;

import de.ancash.ilibrary.minecraft.nbt.annotations.FAUtil;
import de.ancash.ilibrary.minecraft.nbt.utils.MinecraftVersion;
import de.ancash.ilibrary.minecraft.nbt.utils.annotations.AvaliableSince;
import de.ancash.ilibrary.minecraft.nbt.utils.annotations.CheckUtil;

public class NBTChunk {

	private final Chunk chunk;
	
	public NBTChunk(Chunk chunk) {
		this.chunk = chunk;
	}
	
	/**
	 * Gets the NBTCompound used by spigots PersistentDataAPI. This method is only
	 * available for 1.16.4+!
	 * 
	 * @return NBTCompound containing the data of the PersistentDataAPI
	 */
	@AvaliableSince(version = MinecraftVersion.MC1_16_R3)
	public NBTCompound getPersistentDataContainer() {
		FAUtil.check(this::getPersistentDataContainer, CheckUtil::isAvaliable);
		return new NBTPersistentDataContainer((PersistentDataContainer) ((NBTChunk) chunk).getPersistentDataContainer());
	}
	
}
