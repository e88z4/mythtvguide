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

import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1029;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_LATEST;

import org.jmythapi.database.annotation.MythDatabaseVersionAnnotation;
import org.jmythapi.impl.AVersionRange;

public class DatabaseVersionRange extends AVersionRange<DatabaseVersion> {
	public static final DatabaseVersionRange DEFAULT_RANGE = new DatabaseVersionRange(
		DB_VERSION_1029,
		DB_VERSION_LATEST
	);
	
	public DatabaseVersionRange(MythDatabaseVersionAnnotation versionRange) {
		this(versionRange.from(),versionRange.to());
	}	
	
	public DatabaseVersionRange(DatabaseVersion from, DatabaseVersion to) {
		super(
			from==null?DB_VERSION_1029:from,
			to==null?DB_VERSION_LATEST:to
		);
	}

	@Override
	public boolean isInRange(int dbVersion) {
		return isInRange(this.fromVersion, this.toVersion, dbVersion);
	}
	
	private static boolean isInRange(DatabaseVersion from, DatabaseVersion to, int current) {	
		return !((from.getVersion() > current)||(!to.equals(DB_VERSION_LATEST) && to.getVersion() <= current));
	}
}
