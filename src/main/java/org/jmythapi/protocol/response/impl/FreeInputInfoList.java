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
package org.jmythapi.protocol.response.impl;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_37;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_87;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jmythapi.protocol.IMythPacket;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.response.IFreeInputInfoList;
import org.jmythapi.protocol.response.IFreeInputInfo;
import org.jmythapi.protocol.utils.EnumUtils;

@MythProtoVersionAnnotation(from=PROTO_VERSION_87)
public class FreeInputInfoList extends AMythResponse<FreeInputInfoList.Props> implements Iterable<IFreeInputInfo>, IFreeInputInfoList {
    public static final String EMPTY_LIST = "OK";

    public static enum Props {
        // NO PROPERTIES AVAILABLE
    }

    public FreeInputInfoList(IMythPacket packet) {
        super(Props.class, packet);
    }

    /**
     * The size of the response array must be a multiple of {@code IFreeInputInfo.Props.values().size()}.
     * There is no size value at the beginning of the response list (e.g. as available for program-lists).
     */
    @Override
    protected void checkSize(List<String> responseArgs) throws IllegalArgumentException {
        if (responseArgs != null && responseArgs.size() == 1 && responseArgs.get(0).equals(EMPTY_LIST)) {
            return;
        }

        final int expectedSize = EnumUtils.getEnumLength(IFreeInputInfo.Props.class, this.protoVersion);
        final int currentSize = (responseArgs==null)?0:responseArgs.size();

        if (currentSize != 0 && currentSize % expectedSize != 0) {
            throw new IllegalArgumentException(String.format(
                    "%d args expected but %d args found.",
                    ((currentSize / expectedSize)+1)*expectedSize,
                    currentSize
            ));
        }
    }

    public List<IFreeInputInfo> asList() {
        final ArrayList<IFreeInputInfo> inputInfoList = new ArrayList<IFreeInputInfo>();

        final int totalSize = this.getPropertyCount();
        if (totalSize > 1 && !this.getPropertyValue(0).equals(EMPTY_LIST)) {
            final int entryArgsLength = EnumUtils.getEnumLength(IFreeInputInfo.Props.class, this.protoVersion);
            final int size = totalSize / entryArgsLength;

            int fromIdx = 0;
            int toIdx = entryArgsLength;
            for (int i=0; i<size; i++) {
                final List<String> entryArgs = this.respArgs.subList(fromIdx, toIdx);
                final FreeInputInfo inputInfoFree = new FreeInputInfo(this.protoVersion, entryArgs);
                inputInfoList.add(inputInfoFree);
                fromIdx = toIdx;
                toIdx = toIdx + entryArgsLength;
            }
        }
        return inputInfoList;
    }

    public IFreeInputInfo get(int idx) {
        final List<IFreeInputInfo> inputInfoFrees = this.asList();
        if(inputInfoFrees == null || inputInfoFrees.isEmpty()) return null;
        return inputInfoFrees.get(idx);
    }

    public int size() {
        final int totalSize = this.getPropertyCount();
        final int entryArgsLength = EnumUtils.getEnumLength(IFreeInputInfo.Props.class, this.protoVersion);
        return totalSize / entryArgsLength;
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public Iterator<IFreeInputInfo> iterator() {
        final List<IFreeInputInfo> inputInfoList = this.asList();
        return inputInfoList.iterator();
    }

    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder();

        final List<IFreeInputInfo> inputInfoList = this.asList();
        if (inputInfoList != null) {
            for (IFreeInputInfo inputInfoFree : inputInfoList) {
                buff.append(inputInfoFree).append("\r\n");
            }
        }

        return buff.toString();
    }
}

