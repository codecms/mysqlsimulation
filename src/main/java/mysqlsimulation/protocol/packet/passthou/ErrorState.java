package mysqlsimulation.protocol.packet.passthou;



import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class ErrorState extends PacketState {

    private ServerResponseContext context;

    public ErrorState(ServerResponseContext context) {
        this.context = context;
    }

//    @Override
//    public void read(ByteBuffer buffer) {
//        ERRPacket ep = new ERRPacket();
//        ep.capabilities = ClientCapabilityFlags.getClientCapabilities();
//        ep.read(buffer);
//        context.setState(null);
//        return ep;
//    }

    @Override
    void read(SocketChannel channel) throws IOException {

    }
}
