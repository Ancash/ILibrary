package de.ancash.ilibrary.minecraft.skills;

public abstract class Levelable {

	protected int level;
	
	/**			
	 * @return int the current level for this skill
	 */
	protected final int getLevel() {
		return level;
	}
	
	protected abstract boolean setLevel(int level);
}
