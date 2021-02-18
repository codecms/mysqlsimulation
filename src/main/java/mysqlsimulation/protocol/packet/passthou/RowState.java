package mysqlsimulation.protocol.packet.passthou;



import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class RowState extends PacketState {

    private ServerResponseContext context;

    public RowState(ServerResponseContext context) {
        this.context = context;
    }

    @Override
    void read(SocketChannel channel) throws IOException {

    }

//    @Override
//    public MySQLPacket read(ByteBuffer buffer) {
//        ResultSetRowPacket rp = new ResultSetRowPacket();
//        rp.columnCount = context.getColumnsNumber();
//        rp.read(buffer);
//        ByteBuffer bb = buffer.slice();
//        MySQLMessage m = new MySQLMessage(bb);
//        int packetLength = m.readUB3();
//        m.move(1);
//        byte head = m.read();
//        if (head == (byte) 0xfe && packetLength < 9) {
//            context.setReadRow(false);
//            context.setState(context.eofState);
//        }
//        return rp;
//    }
}
