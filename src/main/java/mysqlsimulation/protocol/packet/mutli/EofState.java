package mysqlsimulation.protocol.packet.mutli;



import java.nio.ByteBuffer;

import mysqlsimulation.protocol.constants.ClientCapabilityFlags;
import mysqlsimulation.protocol.packet.EOFPacket;
import mysqlsimulation.protocol.packet.MySQLPacket;


public class EofState implements MultiResultSetState {
    private MultiResultSetContext context;

    public EofState(MultiResultSetContext context) {
        this.context = context;
    }

    @Override
    public MySQLPacket read(ByteBuffer buffer) {
        EOFPacket ep = new EOFPacket();
        ep.capabilities = ClientCapabilityFlags.getClientCapabilities();
        ep.read(buffer);
        if (context.getReadRow()) {
            context.setState(context.rowState);
        } else {
            ByteBuffer bb = buffer.slice();
            if (bb.limit() <= 4) {
                context.setState(null);
            } else {
                byte head = bb.get(4);
                if (head == (byte) 0xff) {
                    context.setState(context.errorState);
                } else if (head == (byte) 0x00) {
                    context.setState(context.okState);
                } else {
                    context.setState(context.columnsNumberState);
                }
            }
        }
        return ep;
    }
}
