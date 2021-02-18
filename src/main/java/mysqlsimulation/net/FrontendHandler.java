package mysqlsimulation.net;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mysqlsimulation.bufferpool.Chunk;
import mysqlsimulation.bufferpool.DirectByteBufferPool;
import mysqlsimulation.protocol.packet.HandshakePacket;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;


public class FrontendHandler extends NioHandler {

    private static Logger logger = LoggerFactory.getLogger(FrontendHandler.class);;
	

    private SqlCommandHandler commandHandler;
    private Session session = new Session();

    public FrontendHandler(Selector selector, FrontendConnection connection, DirectByteBufferPool bufferPool) throws IOException {
        super(selector, connection, bufferPool);
        this.commandHandler = new FakeLoginAuthenticationHandler(bufferPool);
    }

    @Override
    public void run() {
        if (!this.selectionKey.isValid()) {
            logger.debug("select-key cancelled");
            return;
        }
        try {
            if (this.selectionKey.isReadable()) {
            	logger.info("Frontend read");
                doReadData();
            } else if (this.selectionKey.isWritable()) {
                doWriteData();
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    @Override
    public void onConnection(SocketChannel socketChannel) throws IOException {
        super.onConnection(socketChannel);
        HandshakePacket handshake = FakeMysqlServer.getInstance().response();
        logger.info(handshake.toString());
        Chunk chunk = bufferPool.getChunk(handshake.calcPacketSize() + 4);
        handshake.write(chunk.getBuffer());
        chunk.getBuffer().flip();
        writeData(chunk);
    }

    @Override
    public void doReadData() throws IOException {
        Chunk chunk = bufferPool.getChunk(1024);
        int readNum = this.connection.getSocketChannel().read(chunk.getBuffer());
        chunk.getBuffer().flip();
        if (readNum == 0) {
            return;
        }
        if (readNum == -1) {
            this.connection.getSocketChannel().socket().close();
            this.connection.getSocketChannel().close();
            selectionKey.cancel();
            return;
        }

        chunk = commandHandler.response(chunk, this.connection.getSocketChannel(), this);
        if (chunk != null) {
            writeData(chunk);
        }
    }

    public Session getSession() {
        return session;
    }

    public void setCommandHandler(SqlCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }
}
