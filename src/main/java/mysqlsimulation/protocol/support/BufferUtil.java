package mysqlsimulation.protocol.support;

import java.nio.ByteBuffer;


public class BufferUtil {
    public static final void writeUB2(ByteBuffer buffer, int i) {
        buffer.put((byte) (i & 0xff));
        buffer.put((byte) (i >>> 8));
    }

    public static final void writeUB3(ByteBuffer buffer, int i) {
        buffer.put((byte) (i & 0xff));
        buffer.put((byte) (i >>> 8));
        buffer.put((byte) (i >>> 16));
    }

    public static final void writeInt(ByteBuffer buffer, int i) {
        buffer.put((byte) (i & 0xff));
        buffer.put((byte) (i >>> 8));
        buffer.put((byte) (i >>> 16));
        buffer.put((byte) (i >>> 24));
    }

    public static final void writeFloat(ByteBuffer buffer, float f) {
        writeInt(buffer, Float.floatToIntBits(f));
    }

    public static final void writeUB4(ByteBuffer buffer, long l) {
        buffer.put((byte) (l & 0xff));
        buffer.put((byte) (l >>> 8));
        buffer.put((byte) (l >>> 16));
        buffer.put((byte) (l >>> 24));
    }

    public static final void writeLong(ByteBuffer buffer, long l) {
        buffer.put((byte) (l & 0xff));
        buffer.put((byte) (l >>> 8));
        buffer.put((byte) (l >>> 16));
        buffer.put((byte) (l >>> 24));
        buffer.put((byte) (l >>> 32));
        buffer.put((byte) (l >>> 40));
        buffer.put((byte) (l >>> 48));
        buffer.put((byte) (l >>> 56));
    }

    public static final void writeDouble(ByteBuffer buffer, double d) {
        writeLong(buffer, Double.doubleToLongBits(d));
    }

    public static final int writeLength(ByteBuffer buffer, long l) {
        if (l < 0xfb) {
            buffer.put((byte) (l & 0xff));
            return 1;
        } else if (l < 0x10000) {
            buffer.put((byte) 0xfc);
            writeUB2(buffer, (int) l);
            return 2;
        } else if (l < 0x1000000) {
            buffer.put((byte) 0xfd);
            writeUB3(buffer, (int) l);
            return 3;
        } else {
            buffer.put((byte) 0xfe);
            writeLong(buffer, l);
            return 4;
        }
    }

    public static final void writeWithNull(ByteBuffer buffer, byte[] src) {
        buffer.put(src);
        buffer.put((byte) 0);
    }

    public static final int writeWithLength(ByteBuffer buffer, String src) {
        if (src == null) {
            buffer.put((byte) 0);
            return 1;
        } else {
           return writeWithLength(buffer, src.getBytes());
        }
    }
    
    public static final int writeWithLength(ByteBuffer buffer, byte[] src) {
        int length = src.length;
        int len= writeLength(buffer, length);
        buffer.put(src);
        return len+length;
    }

    public static final int writeWithLength(ByteBuffer buffer, byte[] src, byte nullValue) {
        if (src == null) {
            buffer.put(nullValue);
            return 1;
        } else {
           return  writeWithLength(buffer, src);
        }
    }

    public static final int getLength(long length) {
        if (length < 251) {
            return 1;
        } else if (length < 0x10000) {
            return 3;
        } else if (length < 0x1000000) {
            return 4;
        } else {
            return 9;
        }
    }

    public static final int getLength(String src) {
    	if(src==null) {
    		return 1;
    	}
    	
        int length = src.getBytes().length;
        if (length < 251) {
            return 1 + length;
        } else if (length < 0x10000L) {
            return 3 + length;
        } else if (length < 0x1000000L) {
            return 4 + length;
        } else {
            return 9 + length;
        }
    }
    
    public static final int getLength(byte[] src) {
        int length = src.length;
        if (length < 251) {
            return 1 + length;
        } else if (length < 0x10000L) {
            return 3 + length;
        } else if (length < 0x1000000L) {
            return 4 + length;
        } else {
            return 9 + length;
        }
    }

}
