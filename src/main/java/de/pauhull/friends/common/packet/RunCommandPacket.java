package de.pauhull.friends.common.packet;

import com.ikeirnez.pluginmessageframework.PacketWriter;
import com.ikeirnez.pluginmessageframework.StandardPacket;
import lombok.Getter;

import java.io.DataInputStream;
import java.io.IOException;

public class RunCommandPacket extends StandardPacket {

    @Getter
    private String command;

    public RunCommandPacket() {

    }

    public RunCommandPacket(String command) {
        this.command = command;
    }

    @Override
    public void handle(DataInputStream dataInputStream) throws IOException {
        this.command = dataInputStream.readUTF();
    }

    @Override
    public PacketWriter write() throws IOException {
        PacketWriter packetWriter = new PacketWriter(this);
        packetWriter.writeUTF(command);
        return packetWriter;
    }
}
