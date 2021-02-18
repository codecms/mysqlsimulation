package mysqlsimulation;



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mysqlsimulation.bufferpool.DirectByteBufferPool;
import mysqlsimulation.net.NioAcceptor;
import mysqlsimulation.net.NioConnector;
import mysqlsimulation.net.NioReactor;
import mysqlsimulation.net.SystemConfig;

public class MysqlDeamon {
    public static void main(String[] args) {
        SystemConfig config = new SystemConfig("127.0.0.1", 3308, "uaa", "root", "123456");
        ExecutorService executorService = Executors.newCachedThreadPool();
        DirectByteBufferPool bufferPool = DirectByteBufferPool.getInstance();
        try {
            NioReactor[] reactors = new NioReactor[Runtime.getRuntime().availableProcessors()];
            for (int i = 0; i < reactors.length; i++) {
                reactors[i] = new NioReactor(executorService, bufferPool, "nio-reactor-" + i);
                reactors[i].start();
            }

           NioAcceptor acceptor = new NioAcceptor(3308, reactors);
            acceptor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
