/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.dao;

import java.sql.SQLException;
import java.util.List;

public interface CRUDDAO<BeanType, IDType> {

	public void add(BeanType bean) throws SQLException;

	public void update(BeanType bean) throws SQLException;

	public void delete(BeanType bean) throws SQLException;

	public BeanType get(IDType beanID) throws SQLException;

	public List<BeanType> getAll() throws SQLException;
}
