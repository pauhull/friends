package de.pauhull.friends.bungee.util;

import com.ikeirnez.pluginmessageframework.PacketHandler;
import com.ikeirnez.pluginmessageframework.PacketListener;
import de.pauhull.friends.common.packet.RunCommandPacket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class IncomingPacketHandler implements PacketListener {

    @PacketHandler
    public void onRunCommand(RunCommandPacket packet) {
        ProxiedPlayer player = packet.getSender().getBungeePlayer();
        String command = packet.getCommand();
        ProxyServer.getInstance().getPluginManager().dispatchCommand(player, command);
    }

}
