package mysqlsimulation.protocol.packet.mutli;



import java.nio.ByteBuffer;

import mysqlsimulation.protocol.packet.ColumnPacket;
import mysqlsimulation.protocol.packet.MySQLPacket;
import mysqlsimulation.protocol.support.MySQLMessage;


public class FieldState implements MultiResultSetState {
    private MultiResultSetContext context;

    public FieldState(MultiResultSetContext context) {
        this.context = context;
    }

    @Override
    public MySQLPacket read(ByteBuffer buffer) {
        ColumnPacket cp = new ColumnPacket();
        cp.read(buffer);
        ByteBuffer temp = buffer.slice();
        MySQLMessage m = new MySQLMessage(temp);
        m.move(4);
        int position = temp.position();
        byte head = m.read();
        if (head == (byte) 0xfe) {
            context.setReadRow(true);
            context.setState(context.eofState);
        }
        return cp;
    }
}
