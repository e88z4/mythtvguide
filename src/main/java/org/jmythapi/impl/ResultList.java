/*
 * Copyright (C) ${year} Martin Thelian
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, please email thelian@users.sourceforge.net
 */
package org.jmythapi.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Just a helper class to generate a nicer toString on some result lists.
 * @param <E>
 * 		the type of the item contained in the list
 */
public class ResultList <E> extends ArrayList<E> implements List<E> {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an empty result list.
	 */
	public ResultList() {
		super();
	}
	
	/**
	 * Creates a result list and appends the given items.
	 * @param items
	 * 		the items to add
	 */
	public ResultList(Collection<E> items) {
		super(items);
	}
	
	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		if(this.size()>0) {
			for(E item : this) {
				if(item == null) continue;
				buff.append(item.toString()).append("\r\n");
			}
		}
		return buff.toString();
	}
}
