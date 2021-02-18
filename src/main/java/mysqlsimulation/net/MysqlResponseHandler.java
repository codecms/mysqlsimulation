package mysqlsimulation.net;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mysqlsimulation.protocol.packet.EOFPacket;
import mysqlsimulation.protocol.packet.ERRPacket;
import mysqlsimulation.protocol.packet.LocalInfilePacket;
import mysqlsimulation.protocol.packet.OKPacket;
import mysqlsimulation.protocol.packet.ResultSetPacket;
import mysqlsimulation.protocol.packet.StmtPreparePacket;
import mysqlsimulation.protocol.support.MySQLMessage;

import java.nio.ByteBuffer;


public class MysqlResponseHandler {

    private static Logger logger = LoggerFactory.getLogger(MysqlResponseHandler.class);;


    public static void dump(ByteBuffer buffer, FrontendHandler frontendHandler) {
        MySQLMessage m = new MySQLMessage(buffer);
        int packetLength = m.readUB3();
        buffer.position(0);
        int packetType = buffer.get(4);
        //COM_STMT_PREPARE response
        if (packetType == 0x00 && packetLength == 12) {
            StmtPreparePacket sp = new StmtPreparePacket();
            sp.read(buffer);
            frontendHandler.getSession().getStmtIdParamCount().put(sp.stmtPrepareOKPacket.statementId, sp.stmtPrepareOKPacket.parametersNumber);
            System.out.println(sp);
        } else {
            switch (packetType) {
                //OK packet
                case 0x00:
                    OKPacket op = new OKPacket();
                    op.read(buffer);
                    logger.debug(op.toString());
                    break;
                //LOCAL_INFILE packet
                case 0xfb:
                    LocalInfilePacket lip = new LocalInfilePacket();
                    lip.read(buffer);
                    logger.debug(lip.toString());
                    break;
                //EOF packet
                case 0xfe:
                    EOFPacket ep = new EOFPacket();
                    ep.read(buffer);
                    logger.debug(ep.toString());
                    break;
                //ERROR packet
                case 0xff:
                    ERRPacket errp = new ERRPacket();
                    errp.read(buffer);
                    break;
                default:
                    ResultSetPacket rsp = new ResultSetPacket();
                    rsp.read(buffer);
                    logger.debug(rsp.toString());
            }
        }
        buffer.position(0);
    }
}
