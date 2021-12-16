package me.wolf.wduels.managers;

import me.wolf.wduels.DuelsPlugin;
import me.wolf.wduels.file.YamlConfig;
import me.wolf.wduels.utils.Utils;

public class FileManager {

    private YamlConfig arenas, kits;


    public FileManager(final DuelsPlugin plugin) {
        try {
            arenas = new YamlConfig("arenas.yml", plugin);
            kits = new YamlConfig("kits.yml", plugin);
        } catch (final Exception e) {
            System.out.println(Utils.colorize("&4Something went wrong while loading the yml files"));
        }

    }

    public void reloadConfigs() {
        arenas.reloadConfig();

    }

    public YamlConfig getKitsConfigFile() {
        return kits;
    }

    public YamlConfig getArenasConfigFile() {
        return arenas;
    }

}
