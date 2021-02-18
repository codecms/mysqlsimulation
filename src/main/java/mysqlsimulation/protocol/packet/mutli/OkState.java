package mysqlsimulation.protocol.packet.mutli;



import java.nio.ByteBuffer;

import mysqlsimulation.protocol.constants.ClientCapabilityFlags;
import mysqlsimulation.protocol.packet.MySQLPacket;
import mysqlsimulation.protocol.packet.OKPacket;


public class OkState implements MultiResultSetState {

    private MultiResultSetContext context;

    public OkState(MultiResultSetContext context) {
        this.context = context;
    }

    @Override
    public MySQLPacket read(ByteBuffer buffer) {
        OKPacket op = new OKPacket();
        op.capabilities = ClientCapabilityFlags.getClientCapabilities();
        op.read(buffer);
        ByteBuffer bb = buffer.slice();
        if (bb.limit() <= 4) {
            context.setState(null);
        }
        return op;
    }
}
