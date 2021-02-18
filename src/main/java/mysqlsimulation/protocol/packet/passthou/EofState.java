package mysqlsimulation.protocol.packet.passthou;



import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class EofState extends PacketState {
    private ServerResponseContext context;

    public EofState(ServerResponseContext context) {
        this.context = context;
    }

//    @Override
//    public MySQLPacket read(ByteBuffer buffer) {
//        EOFPacket ep = new EOFPacket();
//        ep.capabilities = ClientCapabilityFlags.getClientCapabilities();
//        ep.read(buffer);
//        if (context.getReadRow()) {
//            context.setState(context.rowState);
//        } else {
//            ByteBuffer bb = buffer.slice();
//            if (bb.limit() <= 4) {
//                context.setState(null);
//            } else {
//                byte head = bb.get(4);
//                if (head == (byte) 0xff) {
//                    context.setState(context.errorState);
//                } else if (head == (byte) 0x00) {
//                    context.setState(context.okState);
//                } else {
//                    context.setState(context.columnsCountState);
//                }
//            }
//        }
//        return ep;
//    }

    @Override
    void read(SocketChannel channel) throws IOException {

    }
}
