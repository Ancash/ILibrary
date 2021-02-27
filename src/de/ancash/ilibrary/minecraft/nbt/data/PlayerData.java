package de.ancash.ilibrary.minecraft.nbt.data;

import java.io.File;
import java.io.IOException;

import de.ancash.ilibrary.minecraft.nbt.NBTCompound;
import de.ancash.ilibrary.minecraft.nbt.NBTFile;
import de.ancash.ilibrary.minecraft.nbt.NbtApiException;

public class PlayerData {

	private final NBTFile file;

	protected PlayerData(File playerFile) throws IOException {
		file = new NBTFile(playerFile);
	}

	public NBTFile getFile() {
		return file;
	}

	public NBTCompound getCompound() {
		return file;
	}

	public void saveChanges() {
		try {
			file.save();
		} catch (IOException e) {
			throw new NbtApiException("Error when saving level data!", e);
		}
	}
	
	public float getHealth(){
		return file.getFloat("Health");
	}
	
	public void setHealth(float health){
		file.setFloat("Health", health);
	}
	
}
