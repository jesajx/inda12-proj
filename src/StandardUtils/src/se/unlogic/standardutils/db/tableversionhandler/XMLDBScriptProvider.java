package se.unlogic.standardutils.db.tableversionhandler;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import se.unlogic.standardutils.settings.XMLSettingNode;
import se.unlogic.standardutils.xml.XMLUtils;


public class XMLDBScriptProvider implements DBScriptProvider {

	private XMLSettingNode settingNode;
	
	public XMLDBScriptProvider(Document doc) {

		settingNode = new XMLSettingNode(doc);
	}

	public XMLDBScriptProvider(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {

		settingNode = new XMLSettingNode(XMLUtils.parseXmlFile(inputStream, false));
	}

	public DBScript getScript(int version) {

		XMLSettingNode dbScriptNode = settingNode.getSetting("/DBScripts/Script[@version='" + version + "']");
		
		if(dbScriptNode == null){
			
			return null;
		}
		
		return new XMLDBScript(dbScriptNode);
	}
}
