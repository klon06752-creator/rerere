package com.example.invtabsfix;

import com.example.invtabsfix.compat.SableCompat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("invtabsfix")
public class InvTabsFixMod {

    public static final String MOD_ID = "invtabsfix";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public InvTabsFixMod(IEventBus modEventBus) {
        modEventBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("[InvTabsFix] Initialising Sable/Aeronautics compatibility...");
        SableCompat.init();
    }
}
