package mysqlsimulation.net;

import java.nio.channels.SocketChannel;


public class FrontendConnection extends NioConnection {
    public FrontendConnection(SocketChannel socketChannel) {
        super(socketChannel);
    }
}
