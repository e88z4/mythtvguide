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
package org.jmythapi.database;

import org.jmythapi.IVersion;

public enum DatabaseVersion implements IVersion {
	DB_VERSION_1029(1029),
	DB_VERSION_1037(1037),
	DB_VERSION_1042(1042),
	DB_VERSION_1047(1047),
	DB_VERSION_1056(1056),
	DB_VERSION_1057(1057),
	DB_VERSION_1061(1061),
	DB_VERSION_1062(1062),
	DB_VERSION_1072(1072),
	DB_VERSION_1074(1074),
	DB_VERSION_1082(1082),
	DB_VERSION_1085(1085),
	DB_VERSION_1088(1088),
	DB_VERSION_1108(1108),
	DB_VERSION_1143(1143),
	DB_VERSION_1158(1158),
	DB_VERSION_1170(1170),	
	DB_VERSION_1171(1171),
	DB_VERSION_1182(1182),
	DB_VERSION_1193(1193),
	DB_VERSION_1244(1244),
	DB_VERSION_1257(1257),
	DB_VERSION_1277(1277),
	DB_VERSION_1278(1278),
	DB_VERSION_1302(1302),
	DB_VERSION_1309(1309),
	DB_VERSION_1310(1310),
	DB_VERSION_1344(1344),
	DB_VERSION_LATEST(-1);
	
	/* ============================================================================
	 * DB version METHODS
	 * ============================================================================ */
	private int dbVersion = -1;	
	
	private DatabaseVersion(int dbVersion) {
		this.dbVersion = dbVersion;
	}
	
	public int getVersion() {
		return this.dbVersion;
	}	
}
