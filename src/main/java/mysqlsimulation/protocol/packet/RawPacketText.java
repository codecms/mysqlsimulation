package mysqlsimulation.protocol.packet;



import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import mysqlsimulation.protocol.support.BufferUtil;
import mysqlsimulation.protocol.support.MySQLMessage;

/**
 * lenenc_str     catalog
 * lenenc_str     schema
 * lenenc_str     table
 * lenenc_str     org_table
 * lenenc_str     name
 * lenenc_str     org_name
 * lenenc_int     length of fixed-length fields [0c]
 * 2              character set
 * 4              column length
 * 1              type
 * 2              flags
 * 1              decimals
 * 2              filler [00] [00]
 * if command was COM_FIELD_LIST {
 * lenenc_int     length of default-values
 * string[$len]   default values
 * }
 */
public class RawPacketText extends MySQLPacket {
    //    lenenc_str     catalog
   
	public List<String> valuesList=new LinkedList<>();
	
    @Override
    public void read(ByteBuffer buffer) {
        MySQLMessage message = new MySQLMessage(buffer);
        packetLength = message.readUB3();
        packetSequenceId = message.read();
        if(message.hasRemaining()) {
        	String value=message.readStringWithLength();
        	valuesList.add(value);
        }
    }


    
    @Override
    public void write(ByteBuffer buffer) {
        this.packetLength=calcPacketSize();
        BufferUtil.writeUB3(buffer, this.packetLength);
        buffer.put(this.packetSequenceId);
        for(String va:valuesList) {
            BufferUtil.writeWithLength(buffer, va);	
        }
        
    }

    @Override
    public int calcPacketSize() {
        int size =0;
        for(String va:valuesList) {
        	size+=BufferUtil.getLength( va);	
        }

        return size;
    }

    @Override
    public String getPacketInfo() {
        return "MySQL Column Packet";
    }

    @Override
    public String toString() {
        return "RawPacketText";
    }
}
