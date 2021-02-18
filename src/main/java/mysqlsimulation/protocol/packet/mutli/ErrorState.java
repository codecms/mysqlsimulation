package mysqlsimulation.protocol.packet.mutli;



import java.nio.ByteBuffer;

import mysqlsimulation.protocol.constants.ClientCapabilityFlags;
import mysqlsimulation.protocol.packet.ERRPacket;
import mysqlsimulation.protocol.packet.MySQLPacket;


public class ErrorState implements MultiResultSetState {

    private MultiResultSetContext context;

    public ErrorState(MultiResultSetContext context) {
        this.context = context;
    }

    @Override
    public MySQLPacket read(ByteBuffer buffer) {
        ERRPacket ep = new ERRPacket();
        ep.capabilities = ClientCapabilityFlags.getClientCapabilities();
        ep.read(buffer);
        context.setState(null);
        return ep;
    }
}
