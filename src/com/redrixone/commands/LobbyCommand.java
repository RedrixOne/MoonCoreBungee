package com.redrixone.commands;

import com.redrixone.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LobbyCommand extends Command {

    private Connection connection;
    private PreparedStatement statement;
    private static final String COMMAND_NAME = "lobby";
    private static final String CONFIRMATION_MESSAGE = ChatColor.translateAlternateColorCodes('&', "&bAre you sure? Type /lobby again if you want to quit.");

    private final Map<UUID, Boolean> confirmationMap = new HashMap<>();

    public LobbyCommand(Main main) {
        super(COMMAND_NAME);
        try {
            // Connessione al database
            String url = "jdbc:mysql://localhost:3306/lobby_secure";
            String username = "corpselog";
            String password = "1234";
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.prepareStatement("SELECT lobbysecured FROM lobby_secure WHERE uuid = ?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            ProxiedPlayer player = (ProxiedPlayer) commandSender;

            try {
                // Controllo se la colonna "lobbysecured" è impostata su "true" nel daendase
                statement.setString(1, player.getUniqueId().toString());
                ResultSet result = statement.executeQuery();

                if (result.next() && result.getBoolean("lobbysecured")) {
                    if (confirmationMap.getOrDefault(player.getUniqueId(), false)) {
                        ServerInfo lobby = ProxyServer.getInstance().getServerInfo("lobby");
                        player.connect(lobby);
                        confirmationMap.put(player.getUniqueId(), false);
                    } else {
                        player.sendMessage(CONFIRMATION_MESSAGE);
                        confirmationMap.put(player.getUniqueId(), true);
                    }
                } else {
                    // La colonna "lobbysecured" non è impostata su "true", connessione diretta alla lobby
                    ServerInfo lobby = ProxyServer.getInstance().getServerInfo("lobby");
                    player.connect(lobby);
                }

                result.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
