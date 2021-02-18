package mysqlsimulation.protocol.packet;



import java.nio.ByteBuffer;

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
public class ColumnPacket extends MySQLPacket {
    //    lenenc_str     catalog
    public String catalog="def";
    //    lenenc_str     schema
    public String schema=null;
    //    lenenc_str     table
    public String table=null;
    //    lenenc_str     org_table
    public String orgTable=null;
    //    lenenc_str     name
    public String name=null;
    //    lenenc_str     org_name
    public String orgName=null;

    //    lenenc_int     length of fixed-length fields [0c]
    //            2              character set
    public int characterSet=0x21;
    //4              column length
    public int columnLength=192;
    //1              type
    public int type=0xfd;
    //2              flags
    public int flags=0x01;
    //1              decimals
    public byte decimals=0;
    //2              filler [00] [00]
    //if command was COM_FIELD_LIST {
    //      lenenc_int     length of default-values
    //      string[$len]   default values
    //}
//    public long defaultValuesLength;
//
//    public String defualtValues;

    @Override
    public void read(ByteBuffer buffer) {
        MySQLMessage message = new MySQLMessage(buffer);
        packetLength = message.readUB3();
        packetSequenceId = message.read();
        catalog = message.readStringWithLength();
        schema = message.readStringWithLength();
        table = message.readStringWithLength();
        orgTable = message.readStringWithLength();
        name = message.readStringWithLength();
        orgName = message.readStringWithLength();
        message.move(1);
        characterSet = message.readUB2();
        columnLength = message.readUB4();
        type = (message.read() & 0xff);
        flags = message.readUB2();
        decimals = message.read();
        message.move(2);
    }


    
    @Override
    public void write(ByteBuffer buffer) {
        this.packetLength=calcPacketSize();
        BufferUtil.writeUB3(buffer, this.packetLength);
        buffer.put(this.packetSequenceId);
        BufferUtil.writeWithLength(buffer, catalog);
        
        BufferUtil.writeWithLength(buffer,schema);
        BufferUtil.writeWithLength(buffer,table);
        BufferUtil.writeWithLength(buffer,orgTable);
        BufferUtil.writeWithLength(buffer,name);
        BufferUtil.writeWithLength(buffer,orgName);
        
        
        buffer.put((byte)0x0c);//pad
        BufferUtil.writeUB2(buffer, characterSet);
        BufferUtil.writeInt(buffer, columnLength);
        buffer.put((byte)type);
        
        BufferUtil.writeUB2(buffer, flags);
        buffer.put(decimals);
        
        BufferUtil.writeUB2(buffer, 0);  //pad      
    }

    @Override
    public int calcPacketSize() {
        int size = 
                 BufferUtil.getLength(this.catalog)
                + BufferUtil.getLength(this.schema)+BufferUtil.getLength(this.table)
                +BufferUtil.getLength(this.orgTable)+BufferUtil.getLength(this.name)
                +BufferUtil.getLength(this.orgName)+3+4+1+2+1+2;

        return size;
    }

    @Override
    public String getPacketInfo() {
        return "MySQL Column Packet";
    }

    @Override
    public String toString() {
        return "ColumnPacket{" +
                "  packetLength=" + packetLength +
                ", packetSequenceId=" + packetSequenceId +
                ", catalog='" + catalog + '\'' +
                ", schema='" + schema + '\'' +
                ", table='" + table + '\'' +
                ", orgTable='" + orgTable + '\'' +
                ", name='" + name + '\'' +
                ", orgName='" + orgName + '\'' +
                ", characterSet=" + characterSet +
                ", columnLength=" + columnLength +
                ", type=" + type +
                ", flags=" + flags +
                ", decimals=" + decimals +
                "}\r\n";
    }
}
