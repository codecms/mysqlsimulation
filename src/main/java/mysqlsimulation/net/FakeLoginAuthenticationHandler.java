package mysqlsimulation.net;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mysqlsimulation.bufferpool.Chunk;
import mysqlsimulation.bufferpool.DirectByteBufferPool;
import mysqlsimulation.protocol.constants.ErrorCode;
import mysqlsimulation.protocol.constants.StatusFlags;
import mysqlsimulation.protocol.packet.AuthPacket;
import mysqlsimulation.protocol.packet.ERRPacket;
import mysqlsimulation.protocol.packet.HandshakePacket;
import mysqlsimulation.protocol.packet.OKPacket;
import mysqlsimulation.protocol.support.SecurityUtil;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;


public class FakeLoginAuthenticationHandler implements SqlCommandHandler {

    private static Logger logger = LoggerFactory.getLogger(FakeLoginAuthenticationHandler.class);;

    private DirectByteBufferPool bufferPool;

    public FakeLoginAuthenticationHandler(DirectByteBufferPool bufferPool) {
        this.bufferPool = bufferPool;
    }

    @Override
    public Chunk response(Chunk chunk, SocketChannel socketChannel, FrontendHandler handler) {
        AuthPacket ap = new AuthPacket();
        ap.read(chunk.getBuffer());
        bufferPool.recycleChunk(chunk);
        if (logger.isDebugEnabled()) {
            logger.debug("client auth packet:{}", ap);
        }
        boolean success = false;
        FakeMysqlServer server = FakeMysqlServer.getInstance();
        if (ap.username.equals(server.getUsername())) {
            HandshakePacket handshake = server.getHandshake();
            int len1 = handshake.authPluginDataPart1.length;
            int len2 = handshake.authPluginDataPart2.length;
            byte[] seed = new byte[len1 + len2];
            System.arraycopy(handshake.authPluginDataPart1, 0, seed, 0, len1);
            System.arraycopy(handshake.authPluginDataPart2, 0, seed, len1, len2);
            String serverEncryptedPassword = null;
            try {
                serverEncryptedPassword = new String(
                        SecurityUtil.scramble411(server.getPassword().getBytes(), seed));
            } catch (NoSuchAlgorithmException e) {
                logger.error(e.toString());
            }
            String clientEncryptedPassword = new String(ap.password);
            if (clientEncryptedPassword.equals(serverEncryptedPassword)) {
                success = true;
            }
        }
        Chunk temp;
        if (success) {
            OKPacket op = new OKPacket();
            op.packetSequenceId = 2;
            op.capabilities = FakeMysqlServer.getFakeServerCapabilities();
            op.statusFlags = StatusFlags.SERVER_STATUS_AUTOCOMMIT.getCode();
            temp = bufferPool.getChunk(op.calcPacketSize() + 4);
            op.write(temp.getBuffer());
            temp.getBuffer().flip();
            if (logger.isDebugEnabled()) {
                logger.debug("login authentication success,server response client ok packet:{}", op);
            }
            handler.setCommandHandler(new NormalSchemaSqlCommandHandler(bufferPool));
        } else {
            ERRPacket ep = new ERRPacket();
            ep.packetSequenceId = 2;
            ep.capabilities = FakeMysqlServer.getFakeServerCapabilities();
            ep.errorCode = ErrorCode.ER_ACCESS_DENIED_ERROR;
            ep.sqlState = "28000";
            String address = "";
            try {
                address = socketChannel.getLocalAddress().toString();
            } catch (IOException e) {
                logger.error(e.toString());
            }
            ep.errorMessage =
                    "Access denied for user '" + ap.username + "'@'" + address + "' (using password: YES)";
            temp = bufferPool.getChunk(ep.calcPacketSize() + 4);
            ep.write(temp.getBuffer());
            temp.getBuffer().flip();
            if (logger.isDebugEnabled()) {
                logger.debug("login authentication error,server response client error packet:{}", ep);
            }
        }
        return temp;
    }
}
