package mysqlsimulation.net;

import java.nio.channels.SocketChannel;


public class NioConnection {
    protected SocketChannel socketChannel;

    public NioConnection(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }
}
