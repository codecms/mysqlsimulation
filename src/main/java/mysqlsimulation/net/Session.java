package mysqlsimulation.net;

import java.util.HashMap;
import java.util.Map;


public class Session {
    private Map<Integer,Integer> stmtIdParamCount=new HashMap<>();

    public Map<Integer, Integer> getStmtIdParamCount() {
        return stmtIdParamCount;
    }

    public void setStmtIdParamCount(Map<Integer, Integer> stmtIdParamCount) {
        this.stmtIdParamCount = stmtIdParamCount;
    }
}
