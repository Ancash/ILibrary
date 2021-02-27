package de.ancash.ilibrary.minecraft.skills;

import java.util.HashMap;

public class Skill extends Levelable{
	
	private static HashMap<Integer, Skill> registeredByID = new HashMap<Integer, Skill>();
	private static HashMap<String, Skill> registeredByName = new HashMap<String, Skill>();
	
	private final String name;
	private final int id;
	private final int maxLevel;
	
	
	Skill(String name, int id, int maxLevel) {
		this.name = name;
		this.id = id;
		this.maxLevel = maxLevel;
		registeredByID.put(id, this);
		registeredByName.put(name, this);
	}
	
	public String getName() {
		return name;
	}
	
	public int getID() {
		return id;
	}
	
	public int getMaxLevel() {
		return maxLevel;
	}
	
	public static boolean registerNewSkill(String name, int id, int maxLevel) {
		if(registeredByID.containsKey(id)) return false;
		new Skill(name, id, maxLevel);
		return true;
	}
	
	public static Skill getByName(String name) {
		return registeredByName.get(name);
	}
	
	public static Skill getByID(int id) {
		return registeredByID.get(id);
	}

	@Override
	protected boolean setLevel(int level) {
		if(level > maxLevel || level < 0) return false;
		this.level = level;
		return true;
	}
}
