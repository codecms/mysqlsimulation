package mysqlsimulation.protocol.packet;


import java.nio.ByteBuffer;

import mysqlsimulation.protocol.support.MySQLMessage;


public class CommandPacket extends MySQLPacket {

    private int header;

    private String args;

    @Override public void read(ByteBuffer buffer) {
        MySQLMessage m = new MySQLMessage(buffer);
        this.packetLength = m.readUB3();
        this.packetSequenceId = m.read();
        this.header = m.read();
        this.args = new String(m.readBytes(this.packetLength - 1));
    }

    @Override public void write(ByteBuffer buffer) {
        super.write(buffer);
    }

    
    
    public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	@Override public int calcPacketSize() {
        int size = 5;
        if (args != null) {
            size += args.length();
        }
        return size;
    }

    @Override public String getPacketInfo() {
        return "MySQL Command Packet";
    }

    @Override public String toString() {
        return "CommandPacket{" + "packetLength=" + packetLength + ", packetSequenceId="
                + packetSequenceId + ", header=" + header + ", args='" + args + '\'' + '}';
    }
}
