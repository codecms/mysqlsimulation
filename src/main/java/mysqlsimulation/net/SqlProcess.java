package mysqlsimulation.net;

import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import mysqlsimulation.bufferpool.Chunk;
import mysqlsimulation.bufferpool.DirectByteBufferPool;
import mysqlsimulation.protocol.constants.CapabilityFlags;
import mysqlsimulation.protocol.constants.StatusFlags;
import mysqlsimulation.protocol.packet.ColumnPacket;
import mysqlsimulation.protocol.packet.ERRPacket;
import mysqlsimulation.protocol.packet.OKPacket;
import mysqlsimulation.protocol.packet.RawPacketText;
import mysqlsimulation.protocol.packet.ResultSetPacket;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.ShowStatement;
import net.sf.jsqlparser.statement.Statement;



public class SqlProcess {
	
	private DirectByteBufferPool bufferPool;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	Chunk OkRes(String sqlimp){
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
	
	Chunk useProcess(String sqlimp) {
		Chunk temp =OkRes(sqlimp) ;
		return temp;
	}
	
	Chunk setProcess(String sqlimp) {
		Chunk temp =OkRes(sqlimp) ;
		return temp;
	}
	
	String matchSessonfind(String orgSql) {
		orgSql=orgSql.trim().toLowerCase();
		String patternStr="show\\s*session\\s*status\\s*like\\s*'(.*)'";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(orgSql);

		if(matcher.find()){
			return (matcher.group(1));
		}else {
			patternStr="show\\s*session\\s*variables\\s*like\\s*'(.*)'";
			pattern = Pattern.compile(patternStr);
			matcher = pattern.matcher(orgSql);
			if(matcher.find()) {
				return (matcher.group(1));
			}
		}
		
		return null;

	}
	
	Chunk showParm(String value1,String Value2) {
		
		Chunk temp = null;
		ResultSetPacket res = buildSessionRes2(value1, Value2);

		temp = bufferPool.getChunk(res.calcPacketSize() + 4);
		res.write(temp.getBuffer());
		temp.getBuffer().flip();
		
		return temp;
	}
	
	Chunk showDatabases(){
		Chunk temp = null;
		ResultSetPacket res = new ResultSetPacket();
		res.columnsNumber.columnsNumber = 1;

		{
			ColumnPacket c1 = new ColumnPacket();
			c1.table = "SCHEMATA";
			c1.name = "Database";
			c1.orgName = "SCHEMA_NAME";
			res.columns.add(c1);
		}

		// result
		{
			RawPacketText rpt1 = new RawPacketText();
			rpt1.valuesList.add("infomation_schema");
			res.rows.add(rpt1);

			RawPacketText rpt2 = new RawPacketText();
			rpt2.valuesList.add("test_all2");
			res.rows.add(rpt2);
		}

		temp = bufferPool.getChunk(res.calcPacketSize() + 4);
		res.write(temp.getBuffer());
		temp.getBuffer().flip();
		
		return temp;
	}
	
	Chunk showCollation(){
		Chunk temp = null;
		String Table="COLLATIONS";
		String Origtable=null;
		List<String> Name_list=new LinkedList<>();
		List<String> OrigName_list=new LinkedList<>();
		
		Name_list.add("Collation");
		OrigName_list.add("COLLATION_NAME");
		
		Name_list.add("Charset");
		OrigName_list.add("CHARACTER_SET_NAME");
		
		Name_list.add("Id");
		OrigName_list.add("ID");
		
		Name_list.add("Default");
		OrigName_list.add("IS_DEFAULT");
		
		Name_list.add("Compiled");
		OrigName_list.add("IS_COMPILED");
		
		
		Name_list.add("SortLen");
		OrigName_list.add("SORTLEN");
		

		List<RawPacketText> raw_list=new LinkedList<>();
		RawPacketText rpt1 = new RawPacketText();
		rpt1.valuesList.add("big5_chinese_ci");
		rpt1.valuesList.add("big5");
		rpt1.valuesList.add("1");
		rpt1.valuesList.add("Yes");
		rpt1.valuesList.add("Yes");
		rpt1.valuesList.add("1");
		raw_list.add(rpt1);
		
		
		ResultSetPacket res = buildSelect(Table,Origtable,Name_list,OrigName_list,raw_list);

		temp = bufferPool.getChunk(res.calcPacketSize() + 4);
		res.write(temp.getBuffer());
		temp.getBuffer().flip();
		
		return temp;

	}
	
	
	Chunk showCharset(){
		Chunk temp = null;
		
		
		String Table="CHARACRER_SETS";
		String Origtable=null;
		List<String> Name_list=new LinkedList<>();
		List<String> OrigName_list=new LinkedList<>();
		
		Name_list.add("Charset");
		OrigName_list.add("CHARACTER_SET_NAME");
		
		Name_list.add("Description");
		OrigName_list.add("DESCRIPTION");
		
		Name_list.add("Default collation");
		OrigName_list.add("DEFAULT_COLLATE_NAME");
		
		
		Name_list.add("Maxlen");
		OrigName_list.add("MAXLEN");
		


		List<RawPacketText> raw_list=new LinkedList<>();
		RawPacketText rpt1 = new RawPacketText();
		rpt1.valuesList.add("big5");
		rpt1.valuesList.add("BIG5 Traditional Chinese");
		rpt1.valuesList.add("big5_chinese_ci");
		rpt1.valuesList.add("2");
		raw_list.add(rpt1);
		
		
		ResultSetPacket res = buildSelect(Table,Origtable,Name_list,OrigName_list,raw_list);

		temp = bufferPool.getChunk(res.calcPacketSize() + 4);
		res.write(temp.getBuffer());
		temp.getBuffer().flip();

		
		return temp;

	}
	
	
	Chunk showEngines(){
		Chunk temp = null;
		
		String Table="ENGINES";
		String Origtable=null;
		List<String> Name_list=new LinkedList<>();
		List<String> OrigName_list=new LinkedList<>();
		
		Name_list.add("Engine");
		OrigName_list.add("ENGINE");
		
		Name_list.add("Support");
		OrigName_list.add("SUPPORT");
		
		Name_list.add("Comment");
		OrigName_list.add("COMMENT");
		
		Name_list.add("Transactions");
		OrigName_list.add("TRANSACRIONS");
		
		Name_list.add("XA");
		OrigName_list.add("XA");
		
		
		Name_list.add("Savepoints");
		OrigName_list.add("SAVEPOINTS");
		

		List<RawPacketText> raw_list=new LinkedList<>();
		RawPacketText rpt1 = new RawPacketText();
		rpt1.valuesList.add("MRG_MYISAM");
		rpt1.valuesList.add("YES");
		rpt1.valuesList.add("Collection of identical MyISAM tables");
		rpt1.valuesList.add("NO");
		rpt1.valuesList.add("NO");
		rpt1.valuesList.add("NO");
		raw_list.add(rpt1);
		
		
		ResultSetPacket res = buildSelect(Table,Origtable,Name_list,OrigName_list,raw_list);

		temp = bufferPool.getChunk(res.calcPacketSize() + 4);
		res.write(temp.getBuffer());
		temp.getBuffer().flip();

		
		return temp;

	}
	
	Chunk showVariablesGlobal(){
		Chunk temp = null;
		
		ResultSetPacket res = new ResultSetPacket();
		res.columnsNumber.columnsNumber = 2;

		ColumnPacket c1 = new ColumnPacket();
		c1.table = "VARIABLES";
		c1.name = "Variable_name";
		c1.orgName = "VARIABLE_NAME";
		res.columns.add(c1);

		ColumnPacket c2 = new ColumnPacket();
		c2.table = "VARIABLES";
		c2.name = "Value";
		c2.orgName = "VARIABLE_VALUE";
		res.columns.add(c2);

		RawPacketText rpt1 = new RawPacketText();
		rpt1.valuesList.add("auto_increment_increment");
		rpt1.valuesList.add("1");
		res.rows.add(rpt1);

		RawPacketText rpt2 = new RawPacketText();
		rpt2.valuesList.add("auto_increment_increment");
		rpt2.valuesList.add("1");
		res.rows.add(rpt2);



		temp = bufferPool.getChunk(res.calcPacketSize() + 4);
		res.write(temp.getBuffer());
		temp.getBuffer().flip();

		
		return temp;

	}
	
	Chunk showProcess(String sqlimp) {
		
		//show global variables
		if(sqlimp.equals("show global variables")) {
			return showVariablesGlobal();
		}
		
		
		String param=matchSessonfind(sqlimp);
		if(param != null) {
			switch(param) {
			case "lower_case_table_names":
				return showParm("lower_case_table_names", "0");
			case "ssl_cipher":
				return showParm("Ssl_cipher", null);
			case "sql_mode":
				return showParm("sql_mode", null);
			case "version_comment":
				return showParm("version_comment", "Source distribution");
			case "version":
				return showParm("version", "5.1.73");
			case "version_compile_os":
				return showParm("version_compile_os", "redhat-linux-gnu");
			case "offline_mode":
				return showParm("offline_mode", "0");
			case "wait_timeout":
				return showParm("wait_timeout", "28800");
			case "interactive_timeout":
				return showParm("interactive_timeout", "28800");
			}
		}else {
			
			CCJSqlParserManager pm = new CCJSqlParserManager();
			Statement statement;
			try {
				statement = pm.parse(new StringReader(sqlimp));
				if (statement instanceof ShowStatement) {
		               String showName=((ShowStatement) statement).getName();
		                switch(showName) {
		                case "Database":
		                	return showDatabases();
		                case "collation":
		                    return showCollation();
		                case "charset":
		                	return showCharset(); 
		                case "engines":
		                 return showEngines(); 
		                }

					}else {

					}
			} catch (JSQLParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} 
		

		return null;
	}
	
	
	Chunk select1(){
		Chunk temp = null;
		
		ResultSetPacket res = new ResultSetPacket();
		res.columnsNumber.columnsNumber = 1;

		ColumnPacket c1 = new ColumnPacket();

		c1.name = "1";
		res.columns.add(c1);

		RawPacketText rpt1 = new RawPacketText();
		rpt1.valuesList.add("1");
		res.rows.add(rpt1);

		temp = bufferPool.getChunk(res.calcPacketSize() + 8);
		res.write(temp.getBuffer());
		temp.getBuffer().flip();

		
		return temp;

	}
	
	Chunk selectCurrentUser()
	{
		
		Chunk temp = null;
		
		ResultSetPacket res = new ResultSetPacket();
		res.columnsNumber.columnsNumber = 1;

		ColumnPacket c1 = new ColumnPacket();
		c1.name = "current_user()";
		res.columns.add(c1);

		RawPacketText rpt1 = new RawPacketText();
		rpt1.valuesList.add("test_all2@%");
		res.rows.add(rpt1);

		temp = bufferPool.getChunk(res.calcPacketSize() + 8);
		res.write(temp.getBuffer());
		temp.getBuffer().flip();
		
		return temp;
	}
	
	Chunk selectConnection_id()
	{
		
		Chunk temp = null;
		
		ResultSetPacket res = new ResultSetPacket();
		res.columnsNumber.columnsNumber = 1;

		res.columns = new LinkedList<>();
		ColumnPacket c1 = new ColumnPacket();
		c1.name = "CONNECTION_ID()";
		res.columns.add(c1);

		RawPacketText rpt1 = new RawPacketText();
		rpt1.valuesList.add("1160");
		res.rows.add(rpt1);

		temp = bufferPool.getChunk(res.calcPacketSize() + 8);
		res.write(temp.getBuffer());
		temp.getBuffer().flip();

		
		return temp;
	}
	
	Chunk selectVersion()
	{
		
		Chunk temp = null;
		
		ResultSetPacket res = new ResultSetPacket();
		res.columnsNumber.columnsNumber = 1;

		ColumnPacket c1 = new ColumnPacket();
		c1.name = "version()";
		res.columns.add(c1);

		RawPacketText rpt1 = new RawPacketText();
		rpt1.valuesList.add("5.6.0");
		res.rows.add(rpt1);

		temp = bufferPool.getChunk(res.calcPacketSize() + 8);
		res.write(temp.getBuffer());
		temp.getBuffer().flip();

		
		return temp;
	}
	
	Chunk selectProcess(String sqlimp) {
		
		//SELECT current_user()
		if(sqlimp.equals("select current_user()")) {
			return selectCurrentUser();
		}else if(sqlimp.equals("select 1")) {
			return select1();
		}else if(sqlimp.equals("select connection_id()")) {
			return selectConnection_id();
		}else if(sqlimp.equals("select version()")) {
			return selectVersion();
		}else	 if (sqlimp.contains(" logfile_group_name") && sqlimp.contains("information_schema")) {

			ResultSetPacket res = new ResultSetPacket();
			res.columnsNumber.columnsNumber = 1;

			ColumnPacket c1 = new ColumnPacket();
			c1.table = "FILES";
			c1.name = "logfile_group_name";
			c1.orgName = "LOGFILE_GROUP_NAME";
			res.columns.add(c1);

			res.rows = null;
			Chunk temp = null;
			temp = bufferPool.getChunk(res.calcPacketSize() + 8);
			res.write(temp.getBuffer());
			temp.getBuffer().flip();
			return temp;
		} else if (sqlimp.contains(" tablespace_name") && sqlimp.contains("information_schema.files")) {

			ResultSetPacket res = new ResultSetPacket();

			res.columnsNumber.columnsNumber = 1;

			ColumnPacket c1 = new ColumnPacket();

			c1.table = "FILES";
			c1.name = "tablespace_name";
			c1.orgName = "TABLESPACE_NAME";
			res.columns.add(c1);

			res.rows = null;
			Chunk temp = null;
			temp = bufferPool.getChunk(res.calcPacketSize() + 8);
			res.write(temp.getBuffer());
			temp.getBuffer().flip();
			
			return temp;

		} else if (sqlimp.contains(" name") && sqlimp.contains(" mysql.func")) {

			
			return errorDenied();

		}
		
	
		

		return null;
	}
	
	Chunk errorDenied()
	{

		ERRPacket err = new ERRPacket();
		err.packetSequenceId = 1;
		err.errorCode = 1142;
		err.capabilities = CapabilityFlags.CLIENT_PROTOCOL_41.getCode();
		err.sqlState = "4200";
		err.errorMessage = "denied";
		Chunk temp = null;
		temp = bufferPool.getChunk(err.calcPacketSize() + 8);
		err.write(temp.getBuffer());
		temp.getBuffer().flip();
		return temp;
	}
	
	Chunk sqlProcess(String sqlimp,DirectByteBufferPool bufferPoolP) {
		bufferPool=bufferPoolP;
		sqlimp=StringUtils.normalizeSpace(sqlimp).trim().toLowerCase();
		Chunk temp = null;
		if (sqlimp.startsWith("use ")) {
			temp=useProcess(sqlimp);
		} else if (sqlimp.startsWith("set ")) {
			if (sqlimp.contains("autocommit=1")
					|| (sqlimp.contains(" session ") && sqlimp.contains(" transaction ")
							&& sqlimp.contains(" isolation "))
					|| sqlimp.contains(" character ") || sqlimp.contains(" names ") || sqlimp.contains("timeout")) {
				temp=setProcess(sqlimp);
			}
		} else if (sqlimp.startsWith("show ")) {
			temp=showProcess(sqlimp);
 	
		} else if (sqlimp.startsWith("select ")) {
			   temp=selectProcess(sqlimp);


			
	
		}
		return temp;
	}
	
ResultSetPacket buildSessionRes2(String value1, String value2) {
		
		String Table="VARIABLES";
		String Origtable=null;
		List<String> Name_list=new LinkedList<>();
		List<String> OrigName_list=new LinkedList<>();
		
		Name_list.add("Variable_name");
		OrigName_list.add("VARIABLE_NAME");
		
		Name_list.add("Value");
		OrigName_list.add("VARIABLE_VALUE");
		

		List<RawPacketText> raw_list=new LinkedList<>();
		RawPacketText rpt1 = new RawPacketText();
		rpt1.valuesList.add(value1);
		rpt1.valuesList.add(value2);
		raw_list.add(rpt1);
		

		return buildSelect(Table,Origtable,Name_list,OrigName_list,raw_list);
	}

	ResultSetPacket buildSelect(String Table, String Origtable, List<String> Name_list, List<String> OrigName,
			List<RawPacketText> raw_list) {
		ResultSetPacket res = new ResultSetPacket();
		res.columnsNumber.columnsNumber = Name_list.size();

		for (int i = 0; i < Name_list.size(); i++) {
			ColumnPacket c1 = new ColumnPacket();
			c1.table = Table;
			c1.orgTable = Origtable;
			c1.name = Name_list.get(i);
			c1.orgName = OrigName.get(i);
			res.columns.add(c1);
		}

		res.rows.addAll(raw_list);

		return res;
	}
}
