package mysqlsimulation.protocol.packet.mutli;



import java.nio.ByteBuffer;

import mysqlsimulation.protocol.packet.MySQLPacket;


public interface MultiResultSetState {
    MySQLPacket read(ByteBuffer buffer);
}
