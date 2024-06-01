package de.ancash.nbtnexus.serde;

import java.io.IOException;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.configuration.implementation.snakeyaml.SnakeYamlImplementation;

public final class Serializer {

	public static String toYaml(Map<String, Object> map) throws IOException {
		YamlFile yaml = new YamlFile(new SnakeYamlImplementation());
		map.forEach(yaml::set);
		return yaml.saveToString();
	}

	public static String toJson(Map<String, Object> map) throws IOException {
		YamlFile yaml = new YamlFile(new SnakeYamlImplementation());
		map.forEach(yaml::set);
		JsonObjectBuilder base = Json.createObjectBuilder();
		add(base, yaml);
		return base.build().toString();
	}

	protected static void add(JsonObjectBuilder parent, ConfigurationSection cs) {
		for (String key : cs.getKeys(false)) {
			if (!cs.isConfigurationSection(key)) {
				if (cs.isList(key))
					parent.add(key, Json.createArrayBuilder(cs.getList(key)));
				else if (cs.isBoolean(key))
					parent.add(key, cs.getBoolean(key));
				else if (cs.isDouble(key))
					parent.add(key, cs.getDouble(key));
				else if (cs.isInt(key))
					parent.add(key, cs.getInt(key));
				else if (cs.isLong(key))
					parent.add(key, cs.getLong(key));
				else if (cs.isString(key))
					parent.add(key, cs.getString(key));
			} else {
				JsonObjectBuilder temp = Json.createObjectBuilder();
				add(temp, cs.getConfigurationSection(key));
				parent.add(key, temp);
			}
		}
	}
}
