/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.validation;

import se.unlogic.standardutils.xml.GeneratedElementable;
import se.unlogic.standardutils.xml.XMLElement;

//TODO fix case...
@XMLElement(name="validationError")
public class ValidationError extends GeneratedElementable{
	
	@XMLElement
	private String fieldName;
	
	@XMLElement
	private ValidationErrorType validationErrorType;
	
	@XMLElement
	private String messageKey;

	public ValidationError(String fieldName, ValidationErrorType validationErrorType, String messageKey) {
		super();
		this.fieldName = fieldName;
		this.validationErrorType = validationErrorType;
		this.messageKey = messageKey;
	}

	public ValidationError(String fieldName, ValidationErrorType validationErrorType) {
		super();
		this.fieldName = fieldName;
		this.validationErrorType = validationErrorType;
	}

	public ValidationError(String messageKey) {
		super();

		this.messageKey = messageKey;
	}

	public String getFieldName() {
		return fieldName;
	}

	public ValidationErrorType getValidationErrorType() {
		return validationErrorType;
	}

	public String getMessageKey() {
		return messageKey;
	}
}
