package mysqlsimulation.protocol.packet.mutli;



import java.nio.ByteBuffer;

import mysqlsimulation.protocol.packet.MySQLPacket;
import mysqlsimulation.protocol.packet.ResultSetRowPacket;
import mysqlsimulation.protocol.support.MySQLMessage;


public class RowState implements MultiResultSetState {

    private MultiResultSetContext context;

    public RowState(MultiResultSetContext context) {
        this.context = context;
    }

    @Override
    public MySQLPacket read(ByteBuffer buffer) {
        ResultSetRowPacket rp = new ResultSetRowPacket();
        rp.columnCount = context.getColumnsNumber();
        rp.read(buffer);
        ByteBuffer bb = buffer.slice();
        MySQLMessage m = new MySQLMessage(bb);
        int packetLength = m.readUB3();
        m.move(1);
        byte head = m.read();
        if (head == (byte) 0xfe && packetLength < 9) {
            context.setReadRow(false);
            context.setState(context.eofState);
        }
        return rp;
    }
}
