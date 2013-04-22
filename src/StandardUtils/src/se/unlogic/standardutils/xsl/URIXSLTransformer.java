/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.xsl;

import java.net.URI;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class URIXSLTransformer extends BaseXSLTransformer {

	private URI uri;
	private URIResolver uriResolver;

	public URIXSLTransformer(URI uri) throws TransformerConfigurationException {
		super();
		this.uri = uri;
		this.reloadStyleSheet();
	}

	public URIXSLTransformer(URI uri, URIResolver uriResolver) throws TransformerConfigurationException {

		this.uri = uri;
		this.uriResolver = uriResolver;
		this.reloadStyleSheet();
	}

	public void reloadStyleSheet() throws TransformerConfigurationException {

		TransformerFactory transFact = TransformerFactory.newInstance();

		if(uriResolver != null){
			transFact.setURIResolver(uriResolver);
		}

		this.templates = transFact.newTemplates(new StreamSource(uri.toString()));
	}

	@Override
	public String toString() {

		return "CachedXSLTURI: " + uri;
	}
}
