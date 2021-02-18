package mysqlsimulation.net;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mysqlsimulation.bufferpool.Chunk;
import mysqlsimulation.bufferpool.DirectByteBufferPool;
import mysqlsimulation.protocol.constants.StatusFlags;
import mysqlsimulation.protocol.packet.CommandPacket;
import mysqlsimulation.protocol.packet.OKPacket;
import mysqlsimulation.protocol.packet.StmtExecutePacket;
import mysqlsimulation.protocol.support.MySQLMessage;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class NormalSchemaSqlCommandHandler implements SqlCommandHandler {

	private static Logger logger = LoggerFactory.getLogger(NormalSchemaSqlCommandHandler.class);;

	private DirectByteBufferPool bufferPool;

	public NormalSchemaSqlCommandHandler(DirectByteBufferPool bufferPool) {
		this.bufferPool = bufferPool;
	}

	@Override
	public Chunk response(Chunk chunk, SocketChannel socketChannel, FrontendHandler handler) {
		byte packetType = chunk.getBuffer().get(4);
		// COM_STMT_PREPARE
		if (packetType == 0x16) {

		}
		String sqlimp = "";
		// COM_STMT_EXECUTE
		if (packetType == 0x17) {
			MySQLMessage mm = new MySQLMessage(chunk.getBuffer());
			mm.move(5);
			int statementId = mm.readUB4();
			chunk.getBuffer().position(0);
			StmtExecutePacket sep = new StmtExecutePacket();
			Map<Integer, Integer> spm = handler.getSession().getStmtIdParamCount();
			sep.paramCount = spm.get(statementId);
			sep.read(chunk.getBuffer());
			logger.debug(sep.toString());
		} else if (packetType == 0x3) {
			CommandPacket cp = new CommandPacket();
			cp.read(chunk.getBuffer());
			logger.debug(cp.toString());
			sqlimp = cp.getArgs().trim().toLowerCase();
		} else if (packetType == 0x0e) {
			Chunk temp = null;
			OKPacket op = new OKPacket();
			op.packetSequenceId = 1;
			op.capabilities = FakeMysqlServer.getFakeServerCapabilities();
			op.statusFlags = StatusFlags.SERVER_STATUS_AUTOCOMMIT.getCode();
			temp = bufferPool.getChunk(op.calcPacketSize() + 4);
			op.write(temp.getBuffer());
			temp.getBuffer().flip();
			return temp;
		}
		chunk.getBuffer().position(0);

		BackendConnectionPool connectionPool = BackendConnectionPool.getInstance();
		BackendConnection connection = connectionPool.connection();
		if (connection != null) {
			BackendHandler backendHandler = connection.getBackendHandler();
			backendHandler.setFrontendHandler(handler);
			try {
				Chunk temp;
				OKPacket op = new OKPacket();
				op.packetSequenceId = 1;
				op.capabilities = FakeMysqlServer.getFakeServerCapabilities();
				op.statusFlags = StatusFlags.SERVER_STATUS_AUTOCOMMIT.getCode();
				temp = bufferPool.getChunk(op.calcPacketSize() + 4);
				op.write(temp.getBuffer());
				temp.getBuffer().flip();

				backendHandler.writeData(temp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Chunk temp = null;
		SqlProcess sqlprocess=new SqlProcess();
		temp=sqlprocess.sqlProcess(sqlimp, bufferPool);

		return temp;
	}

	
}
