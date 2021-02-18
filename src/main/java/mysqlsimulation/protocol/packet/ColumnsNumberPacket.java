package mysqlsimulation.protocol.packet;



import java.nio.ByteBuffer;

import mysqlsimulation.protocol.support.BufferUtil;
import mysqlsimulation.protocol.support.MySQLMessage;


public class ColumnsNumberPacket extends MySQLPacket {

    public long columnsNumber;

    @Override
    public void read(ByteBuffer buffer) {
        MySQLMessage message = new MySQLMessage(buffer);
        packetLength = message.readUB3();
        packetSequenceId = message.read();
        columnsNumber = message.readLength();
    }

    @Override
    public void write(ByteBuffer buffer) {
        BufferUtil.writeUB3(buffer, 1);
        buffer.put(this.packetSequenceId);
        buffer.put((byte) columnsNumber);
    }

    @Override
    public int calcPacketSize() {
        return 1;
    }

    @Override
    public String getPacketInfo() {
        return "response to a COM_QUERY packet";
    }

    @Override
    public String toString() {
        return "ColumnsNumberPacket{" +
                "  columnsNumber=" + columnsNumber +
                ", packetLength=" + packetLength +
                ", packetSequenceId=" + packetSequenceId +
                "}\n";
    }
}
