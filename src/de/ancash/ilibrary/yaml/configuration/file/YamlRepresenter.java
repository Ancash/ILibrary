package de.ancash.ilibrary.yaml.configuration.file;

import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;

import de.ancash.ilibrary.yaml.configuration.ConfigurationSection;
import de.ancash.ilibrary.yaml.configuration.serialization.ConfigurationSerializable;
import de.ancash.ilibrary.yaml.configuration.serialization.ConfigurationSerialization;

/**
 * @author Bukkit
 * @see <a href="https://github.com/Bukkit/Bukkit/tree/master/src/main/java/org/bukkit/configuration/file/YamlRepresenter.java">Bukkit Source</a>
 */
public final class YamlRepresenter extends Representer {

    public YamlRepresenter() {
        this.multiRepresenters.put(ConfigurationSection.class, new RepresentConfigurationSection());
        this.multiRepresenters.put(ConfigurationSerializable.class, new RepresentConfigurationSerializable());
    }

    private final class RepresentConfigurationSection extends RepresentMap {

        @Override
        public Node representData(final Object data) {
            return super.representData(((ConfigurationSection) data).getValues(false));
        }

    }

    private final class RepresentConfigurationSerializable extends RepresentMap {

        @Override
        public Node representData(final Object data) {
            final ConfigurationSerializable serializable = (ConfigurationSerializable) data;
            final Map<String, Object> values = new LinkedHashMap<String, Object>();
            values.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(serializable.getClass()));
            values.putAll(serializable.serialize());

            return super.representData(values);
        }

    }

}
