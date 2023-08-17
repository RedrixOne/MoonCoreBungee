package com.redrixone;

import com.redrixone.commands.LobbyCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

    public void onEnable() {
        System.out.println("[MoonPixel Core] Enabled!");
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new LobbyCommand(this));
    }

    public void onDisable() {
        System.out.println("Disabled");
    }

}
