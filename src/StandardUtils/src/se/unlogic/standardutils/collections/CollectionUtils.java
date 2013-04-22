/*******************************************************************************
 * Copyright (c) 2010 Robert "Unlogic" Olofsson (unlogic@unlogic.se).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 ******************************************************************************/
package se.unlogic.standardutils.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class CollectionUtils {

	public static <T> List<T> getGenericList(Class<T> clazz, int size) {

		return new ArrayList<T>(size);
	}

	public static <T> List<T> getGenericList(Class<T> clazz) {

		return new ArrayList<T>();
	}

	public static <T> List<T> getGenericSingletonList(T bean) {

		ArrayList<T> list = new ArrayList<T>(1);

		list.add(bean);

		return list;
	}

	public static boolean isEmpty(Collection<?> collection) {

		if (collection == null || collection.isEmpty()) {

			return true;
		}

		return false;
	}

	public static <T> List<T> conjunction(Collection<T> c1, Collection<T> c2) {

		if (c1 == null || c2 == null) {

			return null;

		} else {

			List<T> result = new ArrayList<T>(c1.size());

			for (T o : c1) {

				if (c2.contains(o)) {

					result.add(o);
				}
			}
			return result;
		}
	}

	/**
	 * Returns the part of a disjunction of two collections that comes from the first collection (in argument order)
	 * @param <T>
	 * @param c1 Collection of <T> objects
	 * @param c2 Collection of <T> objects
	 * @return all elements that exists exclusively in c1
	 */
	public static <T> Collection<T> exclusiveDisjunction(Collection<T> c1, Collection<T> c2) {

		if(c1 == null && c2 == null) {

			return null;

		}

		if(c1 == null && c2 != null){

			return c2;

		}

		if(c1 != null && c2 == null) {

			return c1;

		}

		Collection<T> result = new ArrayList<T>(c1.size());

		for (T o : c1) {

			if (!c2.contains(o)) {

				result.add(o);
			}
		}

		return result;

	}

	public static <T> List<T> getList(T... objects) {

		return Arrays.asList(objects);
	}

	public static void removeNullValues(List<?> list) {

		Iterator<?> iterator = list.iterator();

		while (iterator.hasNext()) {

			Object value = iterator.next();

			if (value == null) {

				iterator.remove();
			}
		}
	}

	/**
	 * @param list
	 * @return the size of the collection or 0 if the collection is null
	 */
	public static int getSize(Collection<?> collection) {

		if(collection == null){

			return 0;
		}

		return collection.size();
	}

	/**
	 * Takes a list of lists as input and returns a single list containing the items of all input lists. If all input lists are empty or null then null is returned.
	 * @param <T>
	 * @param lists
	 * @return
	 */
	public static <T> ArrayList<T> combine(List<T>... lists){

		int totalSize = 0;

		for(List<T> list : lists){

			if(getSize(list) > 0){

				totalSize+=list.size();
			}
		}

		if(totalSize == 0){

			return null;
		}

		ArrayList<T> combinedList = new ArrayList<T>(totalSize);

		for(List<T> list : lists){

			if(list != null){

				combinedList.addAll(list);
			}
		}

		return combinedList;
	}

	/**
	 * Takes a list of lists as input and returns a single set containing the unique items of all input lists. If all input lists are empty or null then null is returned.
	 * @param <T>
	 * @param lists
	 * @return
	 */
	public static <T> HashSet<T> combineAsSet(List<T>... lists){

		int totalSize = 0;

		for(List<T> list : lists){

			if(getSize(list) > 0){

				totalSize+=list.size();
			}
		}

		if(totalSize == 0){

			return null;
		}

		HashSet<T> set = new HashSet<T>(totalSize);

		for(List<T> list : lists){

			if(list != null){

				set.addAll(list);
			}
		}

		return set;
	}

	public static <T> void addNewEntries(List<T> list1, List<T> list2){

		for(T object : list2){

			if(!list1.contains(object)){

				list1.add(object);
			}
		}
	}

	public static <T> List<T> instantiateIfNeeded(List<T> list) {

		if(list == null){

			list = new ArrayList<T>();
		}

		return list;
	}

	public static <T> List<T> addAndInstantiateIfNeeded(List<T> list, T item) {

		list = instantiateIfNeeded(list);

		list.add(item);

		return list;
	}

	public static <T> List<T> addAndInstantiateIfNeeded(List<T> list, List<T> items) {

		list = instantiateIfNeeded(list);

		list.addAll(items);

		return list;
	}
}
