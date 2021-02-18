package mysqlsimulation.protocol.packet.mutli;



import java.nio.ByteBuffer;

import mysqlsimulation.protocol.packet.ColumnsNumberPacket;
import mysqlsimulation.protocol.packet.MySQLPacket;


public class ColumnsNumberState implements MultiResultSetState {

    private MultiResultSetContext context;

    public ColumnsNumberState(MultiResultSetContext context) {
        this.context = context;
    }

    @Override
    public MySQLPacket read(ByteBuffer buffer) {
        ColumnsNumberPacket cnp = new ColumnsNumberPacket();
        cnp.read(buffer);
        context.setColumnsNumber(cnp.columnsNumber);
        context.setState(context.fieldState);
        return cnp;
    }
}
