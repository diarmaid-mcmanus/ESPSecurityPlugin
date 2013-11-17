package com.espsecurityplugin.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CastUtils {

	/**
	 * Casts elements of a list, return new list
	 * from http://stackoverflow.com/a/2848268/702303
	 * @param clazz class you want back
	 * @param c raw list
	 * @return list populated with correct elements
	 */
	public static <T> List<T> castList(Class<? extends T> clazz, Collection<?> c) {
	    List<T> r = new ArrayList<T>(c.size());
	    for(Object o: c)
	      r.add(clazz.cast(o));
	    return r;
	}
	
}
