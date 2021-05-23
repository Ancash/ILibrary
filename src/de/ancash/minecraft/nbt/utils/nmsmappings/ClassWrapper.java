package de.ancash.minecraft.nbt.utils.nmsmappings;

import static de.ancash.minecraft.nbt.utils.MinecraftVersion.getLogger;

import java.util.logging.Level;
import org.bukkit.Bukkit;

import de.ancash.minecraft.nbt.utils.MinecraftVersion;

/**
 * Wraps NMS and CRAFT classes
 * 
 * @author tr7zw
 *
 */
public enum ClassWrapper {
CRAFT_ITEMSTACK(PackageWrapper.CRAFTBUKKIT, "inventory.CraftItemStack"),
CRAFT_METAITEM(PackageWrapper.CRAFTBUKKIT, "inventory.CraftMetaItem"),
CRAFT_ENTITY(PackageWrapper.CRAFTBUKKIT, "entity.CraftEntity"),
CRAFT_WORLD(PackageWrapper.CRAFTBUKKIT, "CraftWorld"),
CRAFT_PERSISTENTDATACONTAINER(PackageWrapper.CRAFTBUKKIT, "persistence.CraftPersistentDataContainer", MinecraftVersion.MC1_14_R1, null),
NMS_NBTBASE(PackageWrapper.NMS, "NBTBase"),
NMS_NBTTAGSTRING(PackageWrapper.NMS, "NBTTagString"),
NMS_NBTTAGINT(PackageWrapper.NMS, "NBTTagInt"),
NMS_NBTTAGFLOAT(PackageWrapper.NMS, "NBTTagFloat"),
NMS_NBTTAGDOUBLE(PackageWrapper.NMS, "NBTTagDouble"),
NMS_NBTTAGLONG(PackageWrapper.NMS, "NBTTagLong"),
NMS_ITEMSTACK(PackageWrapper.NMS, "ItemStack"),
NMS_NBTTAGCOMPOUND(PackageWrapper.NMS, "NBTTagCompound"),
NMS_NBTTAGLIST(PackageWrapper.NMS, "NBTTagList"),
NMS_NBTCOMPRESSEDSTREAMTOOLS(PackageWrapper.NMS, "NBTCompressedStreamTools"),
NMS_MOJANGSONPARSER(PackageWrapper.NMS, "MojangsonParser"),
NMS_TILEENTITY(PackageWrapper.NMS, "TileEntity"),
NMS_BLOCKPOSITION(PackageWrapper.NMS, "BlockPosition", MinecraftVersion.MC1_8_R3, null),
NMS_WORLDSERVER(PackageWrapper.NMS, "WorldServer"),
NMS_MINECRAFTSERVER(PackageWrapper.NMS, "MinecraftServer"),
NMS_WORLD(PackageWrapper.NMS, "World"),
NMS_ENTITY(PackageWrapper.NMS, "Entity"),
NMS_ENTITYTYPES(PackageWrapper.NMS, "EntityTypes"),
NMS_REGISTRYSIMPLE(PackageWrapper.NMS, "RegistrySimple", MinecraftVersion.MC1_11_R1, MinecraftVersion.MC1_12_R1),
NMS_REGISTRYMATERIALS(PackageWrapper.NMS, "RegistryMaterials"),
NMS_IREGISTRY(PackageWrapper.NMS, "IRegistry"),
NMS_MINECRAFTKEY(PackageWrapper.NMS, "MinecraftKey", MinecraftVersion.MC1_8_R3, null),
NMS_GAMEPROFILESERIALIZER(PackageWrapper.NMS, "GameProfileSerializer"),
NMS_IBLOCKDATA(PackageWrapper.NMS, "IBlockData", MinecraftVersion.MC1_8_R3, null),
GAMEPROFILE("com.mojang.authlib.GameProfile", MinecraftVersion.MC1_8_R3)
;
	
    private Class<?> clazz;
    private boolean enabled = false;
    
    ClassWrapper(PackageWrapper packageId, String suffix){
    	this(packageId, suffix, null, null);
    }
    
    ClassWrapper(PackageWrapper packageId, String suffix, MinecraftVersion from, MinecraftVersion to){
    	if(from != null && MinecraftVersion.getVersion().getVersionId() < from.getVersionId()) {
    		return;
    	}
    	if(to != null && MinecraftVersion.getVersion().getVersionId() > to.getVersionId()) {
    		return;
    	}
    	enabled = true;
        try{
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            clazz = Class.forName(packageId.getUri() + "." + version + "." + suffix);
        }catch(Exception ex){
        	getLogger().log(Level.WARNING, "[NBTAPI] Error while trying to resolve the class '" + suffix + "'!", ex);
        }
    }
    
    ClassWrapper(String path, MinecraftVersion from){
    	if(from != null && MinecraftVersion.getVersion().getVersionId() < from.getVersionId()) {
    		return;
    	}
    	enabled = true;
        try{
            clazz = Class.forName(path);
        }catch(Exception ex){
        	getLogger().log(Level.WARNING, "[NBTAPI] Error while trying to resolve the class '" + path + "'!", ex);
        }
    }
    
    /**
     * @return The wrapped class
     */
    public Class<?> getClazz(){
        return clazz;
    }
    
    /**
     * @return Is this class available in this Version
     */
    public boolean isEnabled() {
    	return enabled;
    }
    
}
