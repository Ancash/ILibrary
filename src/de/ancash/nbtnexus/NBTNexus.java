package de.ancash.nbtnexus;

import static de.ancash.nbtnexus.MetaTag.AMOUNT_TAG;
import static de.ancash.nbtnexus.MetaTag.XMATERIAL_TAG;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.configuration.implementation.snakeyaml.SnakeYamlImplementation;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import com.cryptomorin.xseries.XMaterial;

import de.ancash.ILibrary;
import de.ancash.nbtnexus.NBTNexusItem.Type;
import de.ancash.nbtnexus.command.EditCommand;
import de.ancash.nbtnexus.command.NBTNexusCommand;
import de.ancash.nbtnexus.command.SerializeCommand;
import de.ancash.nbtnexus.command.TestSerDeComparisonCommand;
import de.ancash.nbtnexus.packet.InventoryUpdateAdapter;
import de.ancash.nbtnexus.serde.IItemSerDe;
import de.ancash.nbtnexus.serde.handler.ArmorMetaSerDe;
import de.ancash.nbtnexus.serde.handler.AxolotlBucketMetaSerDe;
import de.ancash.nbtnexus.serde.handler.BannerMetaSerDe;
import de.ancash.nbtnexus.serde.handler.BookMetaSerDe;
import de.ancash.nbtnexus.serde.handler.BundleMetaSerDe;
import de.ancash.nbtnexus.serde.handler.CompassMetaSerDe;
import de.ancash.nbtnexus.serde.handler.DamageableMetaSerDe;
import de.ancash.nbtnexus.serde.handler.EnchantmentStorageMetaSerDe;
import de.ancash.nbtnexus.serde.handler.FireworkEffectMetaSerDe;
import de.ancash.nbtnexus.serde.handler.FireworkMetaSerDe;
import de.ancash.nbtnexus.serde.handler.KnowledgeBookMetaSerDe;
import de.ancash.nbtnexus.serde.handler.LeatherArmorMetaSerDe;
import de.ancash.nbtnexus.serde.handler.MapMetaSerDe;
import de.ancash.nbtnexus.serde.handler.MusicInstrumentMetaSerDe;
import de.ancash.nbtnexus.serde.handler.PotionMetaSerDe;
import de.ancash.nbtnexus.serde.handler.RepairableMetaSerDe;
import de.ancash.nbtnexus.serde.handler.SkullMetaSerDe;
import de.ancash.nbtnexus.serde.handler.SpawnEggMetaSerDe;
import de.ancash.nbtnexus.serde.handler.SuspiciousStewMetaSerDe;
import de.ancash.nbtnexus.serde.handler.TropicalFishBucketMetaSerDe;
import de.ancash.nbtnexus.serde.handler.UnspecificMetaSerDe;
import de.ancash.nbtnexus.serde.structure.SerDeStructure;
import de.ancash.nbtnexus.serde.structure.SerDeStructureEntry;
import de.ancash.nbtnexus.serde.structure.SerDeStructureKeySuggestion;
import de.ancash.nbtnexus.serde.structure.SerDeStructureValueSuggestion;

@SuppressWarnings("deprecation")
public class NBTNexus {

	public static final String SPLITTER = "$";
	public static final String SPLITTER_REGEX = "\\$";

	private static NBTNexus instance;
	private NBTNexusCommand cmd;
	private final SerDeStructure structure = new SerDeStructure();
	private InventoryUpdateAdapter iua;
	private final YamlFile config = new YamlFile(new SnakeYamlImplementation());
	private boolean enableExperimentalPacketEditing = false;
	private boolean packetEditingSync = false;
	public final ILibrary pl;

	public NBTNexus(ILibrary pl) {
		config.setConfigurationFile("plugins/NBTNexus/config.yml");
		this.pl = pl;
		instance = this;
//		try {
//			loadConfig();
//		} catch (IOException e) {
//			throw new IllegalStateException(e);
//		}
		structure.putEntry(AMOUNT_TAG, new SerDeStructureEntry(new SerDeStructureKeySuggestion<Byte>(NBTTag.BYTE, a -> a > 0 && a <= 64), null));
		structure.putEntry(XMATERIAL_TAG, new SerDeStructureEntry(SerDeStructureKeySuggestion.forEnum(XMaterial.class), SerDeStructureValueSuggestion
				.forEnum(Arrays.asList(XMaterial.VALUES).stream().filter(x -> x.isSupported() && x.parseItem() != null).toArray(XMaterial[]::new))));
		structure.putMap(NBTNexusItem.NBT_NEXUS_ITEM_PROPERTIES_TAG);
		structure.putMap(MetaTag.COMPONENTS);
		SerDeStructure props = structure.getMap(NBTNexusItem.NBT_NEXUS_ITEM_PROPERTIES_TAG);
		props.putEntry(NBTNexusItem.NBT_NEXUS_ITEM_TYPE_TAG, SerDeStructureEntry.forEnum(Type.class));
		registerSerDeStructure(AxolotlBucketMetaSerDe.INSTANCE);
		registerSerDeStructure(BannerMetaSerDe.INSTANCE);
		registerSerDeStructure(BookMetaSerDe.INSTANCE);
		registerSerDeStructure(BundleMetaSerDe.INSTANCE);
		registerSerDeStructure(CompassMetaSerDe.INSTANCE);
		registerSerDeStructure(DamageableMetaSerDe.INSTANCE);
		registerSerDeStructure(FireworkEffectMetaSerDe.INSTANCE);
		registerSerDeStructure(FireworkMetaSerDe.INSTANCE);
		registerSerDeStructure(KnowledgeBookMetaSerDe.INSTANCE);
		registerSerDeStructure(LeatherArmorMetaSerDe.INSTANCE);
		registerSerDeStructure(MapMetaSerDe.INSTANCE);
		registerSerDeStructure(MusicInstrumentMetaSerDe.INSTANCE);
		registerSerDeStructure(PotionMetaSerDe.INSTANCE);
		registerSerDeStructure(RepairableMetaSerDe.INSTANCE);
		registerSerDeStructure(SkullMetaSerDe.INSTANCE);
		registerSerDeStructure(SpawnEggMetaSerDe.INSTANCE);
		registerSerDeStructure(SuspiciousStewMetaSerDe.INSTANCE);
		registerSerDeStructure(TropicalFishBucketMetaSerDe.INSTANCE);
		registerSerDeStructure(UnspecificMetaSerDe.INSTANCE);
		registerSerDeStructure(EnchantmentStorageMetaSerDe.INSTANCE);
		registerSerDeStructure(ArmorMetaSerDe.INSTANCE);
		cmd = new NBTNexusCommand(this);
		cmd.addSubCommand(new EditCommand(this));
		cmd.addSubCommand(new TestSerDeComparisonCommand(this));
		cmd.addSubCommand(new SerializeCommand(this));
		pl.getCommand("nbtn").setExecutor(cmd);
//		Bukkit.getPluginManager().registerEvents(new InventoryStackListener(), singleton);
//		addPacketListener();
	}

	public SerDeStructure getStructure() {
		return structure.clone();
	}

	public void registerSerDeStructure(IItemSerDe iisd) {
		if (iisd.getStructure() == null)
			return;
		structure.putMap(iisd.getKey(), iisd.getStructure());
	}

	public boolean enableExperimentalPacketEditing() {
		return enableExperimentalPacketEditing;
	}

	public boolean editPacketsSync() {
		return packetEditingSync;
	}

//	@SuppressWarnings("nls")
//	private void addPacketListener() {
//		if (!enableExperimentalPacketEditing) {
//			pl.getLogger().info("Experimental editing of items in packets is disabled");
//			return;
//		}
//		if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
//			pl.getLogger().severe("ProtocolLib not found! Experimental editing disabled");
//			return;
//		}
//		pl.getLogger().warning("Experimental editing of items in packets is enabled");
//		iua = new InventoryUpdateAdapter(this);
//		Bukkit.getPluginManager().registerEvents(iua, pl);
//	}
//
//	private void loadConfig() throws InvalidConfigurationException, IOException {
//		config.createNewFile(false);
//		config.loadWithComments();
//		checkFile(config, "config.yml");
//		config.loadWithComments();
//		enableExperimentalPacketEditing = config.getBoolean("enable-experimental-packet-editing");
//		packetEditingSync = config.getBoolean("experimental-packet-editing-sync");
//	}
//
//	private void checkFile(YamlFile file, String src)
//			throws org.simpleyaml.exceptions.InvalidConfigurationException, IllegalArgumentException, IOException {
//		pl.getLogger().info("Checking " + file.getConfigurationFile().getPath() + " for completeness (comparing to " + src + ")");
//		de.ancash.misc.io.FileUtils.setMissingConfigurationSections(file, pl.getResource(src), new HashSet<>(Arrays.asList("XMaterial")));
//	}

	public static NBTNexus getInstance() {
		return instance;
	}
}
