package mysqlsimulation.net;



import java.nio.channels.SocketChannel;

import mysqlsimulation.bufferpool.Chunk;


public interface SqlCommandHandler {
    Chunk response(Chunk chunk, SocketChannel socketChannel,FrontendHandler handler);
}
