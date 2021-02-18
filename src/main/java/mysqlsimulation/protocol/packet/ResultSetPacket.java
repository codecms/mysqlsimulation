package mysqlsimulation.protocol.packet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;



/**
 * https://dev.mysql.com/doc/internals/en/com-query-response.html#text-resultset-row
 */
public class ResultSetPacket extends MySQLPacket {

    public ColumnsNumberPacket columnsNumber=new ColumnsNumberPacket();;

    public List<ColumnPacket> columns = new ArrayList<>();

    public EOFPacket columnsEof=new EOFPacket();;

    public List<MySQLPacket> rows = new ArrayList<>();

    public EOFPacket rowsEof=new EOFPacket();

    public int capabilities;


    @Override
    public void read(ByteBuffer buffer) {
        columnsNumber = new ColumnsNumberPacket();
        columnsNumber.read(buffer);

        for (int i = 0; i < this.columnsNumber.columnsNumber; i++) {
            buffer = buffer.slice();
            ColumnPacket cp = new ColumnPacket();
            cp.read(buffer);
            columns.add(cp);
        }

        buffer = buffer.slice();
        if ((buffer.get(4) & 0xff) == 0xfe) {
            columnsEof = new EOFPacket();
            columnsEof.read(buffer);
        }
        for (; ; ) {
            buffer = buffer.slice();
            int packetType=buffer.get(4)& 0xff;
            if (packetType == 0xfe) {
                rowsEof = new EOFPacket();
                rowsEof.read(buffer);
                break;
            } else if (packetType==0x00){
                BinaryResultSetRowPacket brsrp = new BinaryResultSetRowPacket();
                brsrp.columns = columns;
                brsrp.read(buffer);
                rows.add(brsrp);
            }else {
                ResultSetRowPacket rsrp = new ResultSetRowPacket();
                rsrp.columnCount = columns.size();
                rsrp.read(buffer);
                rows.add(rsrp);
            }
        }


//        buffer = buffer.slice();
//        columnsEof = new EOFPacket();
//        columnsEof.read(buffer);
//
//        buffer = buffer.slice();
//        ResultSetRowPacket rsrp = new ResultSetRowPacket();
//        rsrp.columnCount = columns.size();
//        rsrp.read(buffer);
//        rows.add(rsrp);
//
//        buffer = buffer.slice();
//        rowsEof = new EOFPacket();
//        rowsEof.read(buffer);
    }

    @Override
    public void write(ByteBuffer buffer) {

    	byte i=1;
    	columnsNumber.packetSequenceId=i;
        columnsNumber.write(buffer);
        
        for( ColumnPacket one:columns) {
        	  i++;
        	  one.packetSequenceId=i;
        	 one.write(buffer);
        }
        i++;
        columnsEof.packetSequenceId=i;
        columnsEof.write(buffer);
        
        if(rows !=null) {
        for(MySQLPacket one:rows) {
        	 i++;
        	 one.packetSequenceId=i;
        	 one.write(buffer);
        }
        }
        
   	    i++;
    	rowsEof.packetSequenceId=i;
        rowsEof.write(buffer);
        
    }

    @Override
    public int calcPacketSize() {
    	int size=0;
    	size=size+columnsNumber.calcPacketSize()+4;
        for( ColumnPacket one:columns) {
        	size=size+one.calcPacketSize()+4;
       }
        
    	size=size+columnsEof.calcPacketSize()+4;
    	
    	if(rows != null) {
        for(MySQLPacket one:rows) {
        	size=size+one.calcPacketSize()+4;
       }
    	}
        
    	size=size+rowsEof.calcPacketSize()+4;
    	
        return size;
    }

    @Override
    public String getPacketInfo() {
        return null;
    }

    @Override
    public String toString() {
        return "ResultSetPacket{" +
                "\n, columnsNumber=" + columnsNumber +
                "\n, columns=" + columns +
                "\n, columnsEof=" + columnsEof +
                "\n, rows=" + rows +
                "\n, rowsEof=" + rowsEof +
                "\n, capabilities=" + capabilities +
                "\n}";
    }
}
