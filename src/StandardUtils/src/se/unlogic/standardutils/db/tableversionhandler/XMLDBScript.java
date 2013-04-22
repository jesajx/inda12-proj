package se.unlogic.standardutils.db.tableversionhandler;

import java.sql.SQLException;
import java.util.List;

import se.unlogic.standardutils.dao.TransactionHandler;
import se.unlogic.standardutils.settings.XMLSettingNode;


public class XMLDBScript implements DBScript {

	private XMLSettingNode dbScriptNode;
	
	public XMLDBScript(XMLSettingNode dbScriptNode) {

		this.dbScriptNode = dbScriptNode;
	}

	public void execute(TransactionHandler transactionHandler) throws SQLException {

		List<XMLSettingNode> xmlQueries = dbScriptNode.getSettings("Query");
		
		if(xmlQueries.isEmpty()){
			return;
		}
				
		for(XMLSettingNode query : xmlQueries){
			
			transactionHandler.getUpdateQuery(query.getString(".")).executeUpdate();
			
			if(query.getBoolean("@forceCommit")){
				
				transactionHandler.intermediateCommit();
			}
		}	
	}
}
