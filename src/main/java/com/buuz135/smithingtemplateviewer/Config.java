package com.buuz135.smithingtemplateviewer;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    public static final Config CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;
    
    public final ModConfigSpec.BooleanValue displayMaterials;
    
    private Config(ModConfigSpec.Builder builder) {
        displayMaterials = builder.comment("Wether Trim Materials should be registered into EMI/JEI").define("display_materials", true);
    }
    
    static {
        Pair<Config, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(Config::new);
        
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }
}
