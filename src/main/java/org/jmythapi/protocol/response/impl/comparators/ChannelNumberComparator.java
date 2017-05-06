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
package org.jmythapi.protocol.response.impl.comparators;

import java.util.Comparator;

import org.jmythapi.IBasicChannelInfo;

public class ChannelNumberComparator implements Comparator<IBasicChannelInfo> {
	public int compare(IBasicChannelInfo c1, IBasicChannelInfo c2) {
		if(c1 == null) return -1;
		else if(c2 == null) return 1;
		
		final String cn1 = c1.getChannelNumber();
		if(cn1 == null) return -1;
		
		final String cn2 = c2.getChannelNumber();
		if(cn2 ==  null) return 1;
		
		if(cn1.matches("\\d+") && cn2.matches("\\d+")) {
			try {
				return Integer.valueOf(cn1).compareTo(Integer.valueOf(cn2));
			} catch (NumberFormatException e) {
				// ignore this.
			}
		} 
		
		return cn1.compareTo(cn2);
	}

}
