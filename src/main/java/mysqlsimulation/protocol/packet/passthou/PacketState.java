package mysqlsimulation.protocol.packet.passthou;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public abstract class PacketState {
    abstract void read(SocketChannel channel) throws IOException;

    public void bufferLimit(ByteBuffer buffer, int packetLength) {
        int limit = buffer.limit();
        int position = buffer.position();
        if (limit - position > packetLength + 4) {
            int offset = (limit - position) - (packetLength + 4);
            buffer.limit(limit - offset);
        }
    }

    public boolean checkReadNum(int readNum, SocketChannel channel) throws IOException {
        if (readNum == 0) {
            return true;
        }
        if (readNum == -1) {
            channel.socket().close();
            channel.close();
            return true;
        }
        return false;
    }
}
