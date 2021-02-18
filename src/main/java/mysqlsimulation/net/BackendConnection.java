package mysqlsimulation.net;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mysqlsimulation.bufferpool.Chunk;
import mysqlsimulation.bufferpool.DirectByteBufferPool;
import mysqlsimulation.protocol.constants.ClientCapabilityFlags;
import mysqlsimulation.protocol.packet.AuthPacket;
import mysqlsimulation.protocol.packet.HandshakePacket;
import mysqlsimulation.protocol.support.SecurityUtil;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;


public class BackendConnection extends NioConnection {

    private static Logger logger = LoggerFactory.getLogger(BackendConnection.class);;
    private DirectByteBufferPool pool;
    private SystemConfig config;
    private BackendHandler backendHandler;

    public BackendConnection(SocketChannel socketChannel, SystemConfig config, DirectByteBufferPool bufferPool) {
        super(socketChannel);
        pool = bufferPool;
        this.config = config;
    }

    public Chunk authentication() throws IOException {
        Chunk chunk = pool.getChunk(300);
        socketChannel.read(chunk.getBuffer());
        HandshakePacket handshake = new HandshakePacket();
        handshake.read(chunk.getBuffer());
        pool.recycleChunk(chunk);
        logger.info(handshake.toString());

        AuthPacket ap = new AuthPacket();
        int len1 = handshake.authPluginDataPart1.length;
        int len2 = handshake.authPluginDataPart2.length;
        byte[] seed = new byte[len1 + len2];
        System.arraycopy(handshake.authPluginDataPart1, 0, seed, 0, len1);
        System.arraycopy(handshake.authPluginDataPart2, 0, seed, len1, len2);
        try {
            ap.password = SecurityUtil.scramble411(config.getPassword().getBytes(), seed);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        ap.packetSequenceId = 1;
        ap.capabilityFlags = ClientCapabilityFlags.getClientCapabilities();
        ap.characterSet = 0x53;//utf8_bin
        ap.username = config.getUsername();
        ap.database = config.getDatabase();
        ap.authPluginName = handshake.authPluginName;
        logger.info(ap.toString());
        chunk = pool.getChunk(ap.calcPacketSize() + 4);
        ap.write(chunk.getBuffer());
        chunk.getBuffer().flip();
        return chunk;
    }

    public void setBackendHandler(BackendHandler backendHandler) {
        this.backendHandler = backendHandler;
    }

    public BackendHandler getBackendHandler() {
        return backendHandler;
    }
}
