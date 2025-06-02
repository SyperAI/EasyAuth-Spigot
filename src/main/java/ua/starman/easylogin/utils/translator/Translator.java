package ua.starman.easylogin.utils.translator;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import ua.starman.easylogin.utils.Utils;
import ua.starman.easylogin.utils.Vars;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Translator {
    private final Yaml yaml;
    public Map<String, Object> configData;

    public Translator() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setIndent(2);

        yaml = new Yaml(new Constructor(new LoaderOptions()), new Representer(dumperOptions));
    }

    @SuppressWarnings("unchecked")
    private static boolean checkTranslationCompatibility(Map<String, Object> mapA, Map<String, Object> mapB, String prefix) {
        for (Map.Entry<String, Object> entry : mapA.entrySet()) {
            String key = entry.getKey();
            Object valueA = entry.getValue();

            String fullKey = prefix.isEmpty() ? key : (prefix + "." + key);

            if (!mapB.containsKey(key)) {
                Vars.plugin.getLogger().warning("Missing translation \"" + fullKey + "\"!");
                return false;
            }

            Object valueB = mapB.get(key);

            if (valueA instanceof Map) {
                if (!(valueB instanceof Map)) {
                    System.out.println("Warning: key \"" + fullKey + "\" is expected to be a nested Map, but found "
                            + (valueB == null ? "null" : valueB.getClass().getSimpleName()));
                    return false;
                }
                Map<String, Object> nestedA = (Map<String, Object>) valueA;
                Map<String, Object> nestedB = (Map<String, Object>) valueB;
                if (!checkTranslationCompatibility(nestedA, nestedB, fullKey)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void loadConfig() {
        String language = Objects.requireNonNull(Vars.plugin.getConfig().getString("plugin.language"));

        File configFile = new File(
                "plugins/EasyAuth/translations",
                language + ".yml"
        );

        if (!configFile.exists()) {
            System.out.println("The configuration file was not found, creating a new one...");
            saveDefaultConfig();
        }

        try (InputStream inputStream = new FileInputStream(configFile)) {
            configData = yaml.load(inputStream);
            System.out.println("Language config loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!language.equals("en")) {
            checkTranslationCompatibility();
        }
    }

    private void checkTranslationCompatibility() {
        Map<String, Object> defaultConfig = getDefaultConfig();

        assert defaultConfig != null;
        checkTranslationCompatibility(defaultConfig, configData, "");
    }

    private Map<String, Object> getDefaultConfig() {
        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("translations/en.yml")) {
            if (resourceStream == null) {
                throw new FileNotFoundException("Default translations not found, plugin may be corrupted");
            }
            return yaml.load(resourceStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveDefaultConfig() {
        Utils.checkDirs(new File("plugins/EasyAuth/translations"));
        try {
            yaml.dump(getDefaultConfig(), new FileWriter("plugins/EasyAuth/translations/en.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }


//        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("translations/en.yml")) {
//            if (resourceStream == null) {
//                throw new FileNotFoundException("Default translation file not found!");
//            }
//            Utils.checkDirs(new File("plugins/EasyAuth/translations"));
//            try (FileOutputStream outputStream = new FileOutputStream("plugins/EasyAuth/translations/en.yml")) {
//                byte[] buffer = new byte[1024];
//                int bytesRead;
//                while ((bytesRead = resourceStream.read(buffer)) != -1) {
//                    outputStream.write(buffer, 0, bytesRead);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @SuppressWarnings("unchecked")
    public Object get(String path) {
        String[] keys = path.split("\\.");
        Map<String, Object> current = configData;
        for (int i = 0; i < keys.length - 1; i++) {
            current = (Map<String, Object>) current.get(keys[i]);

            if (current == null) {
                return null;
            }
        }
        return current.get(keys[keys.length - 1]);
    }

    public String getString(String path) {
        Object value = get(path);
        if (value == null) {
            System.out.println("No translation found for " + path);
            return null;
        }
        return value.toString();
    }

    public List<String> getStringList(String path) {
        Object value = get(path);
        if (value instanceof List<?> list) {
            // Checking that the list elements are lines
            List<String> stringList = new ArrayList<>();
            for (Object item : list) {
                if (item instanceof String) {
                    stringList.add((String) item);
                } else {
                    stringList.add(item.toString());
                }
            }
            return stringList;
        }
        return new ArrayList<>(); // Return an empty list if there is no value or not a list
    }

}
