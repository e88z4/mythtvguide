package org.jmythapi.protocol.response.impl;

import java.util.Set;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IFlag;
import org.jmythapi.protocol.response.IFlagGroup;
import org.jmythapi.protocol.response.IVersionableValue;

public abstract class AFlagGroup<E extends IFlag> extends AGroup<E> implements IVersionable, IFlagGroup<E> {
	private static final long serialVersionUID = 1L;

	public AFlagGroup(Class<E> groupClass, ProtocolVersion protoVersion, long enumValue) {
		super(groupClass, protoVersion, enumValue);
	}	
	
	public abstract Set<E> getActiveFlags();

	public abstract boolean isSupportedFlag(E flag);
	
	public abstract Set<E> getSupportedFlags();
	
	public boolean isSet(Long flagLong) {
		if(flagLong == null) return false;
		return (this.longValue() & flagLong.longValue()) != 0;
	}
	
	public boolean isSet(E flag) {
		if (flag == null) return false;
		else if (!isSupportedFlag(flag)) {
			logger.warning(String.format(
				"Flag %s not found in list of supported value: %s",
				flag, this.getSupportedFlags()
			));
			return false;
		}
		
		// getting the current flag value
		final Long flagLong = getFlagValue(this.protoVersion,flag);
		
		// check if the given flag is set
		if (flagLong == null) {
			logger.warning(String.format(
				"Unable to determine the long value for flag %s",
				flag
			));
			return false;
		}
		return isSet(flagLong);
	}
	
	public void set(Long flagLong) {
		if(flagLong == null) return;
		this.setLongValue(this.longValue() | flagLong.longValue());
	}
	
	public boolean set(E flag) {
		if (flag == null) return false;
		else if (!isSupportedFlag(flag)) {
			logger.warning(String.format(
				"Flag %s not found in list of supported value: %s",
				flag, this.getSupportedFlags()
			));
			return false;
		} 
		
		// getting the current flag value
		final Long flagLong = getFlagValue(this.protoVersion,flag);
		
		// check if the given flag is set
		if (flagLong == null) {
			logger.warning(String.format(
				"Unable to determine the long value for flag %s",
				flag
			));
			return false;
		} else if(isSet(flagLong)) {
			// if the flag is already set
			return false;			
		}
		
		// set the flag
		this.set(flagLong);
		return true;
	}
	
	public void clear(Long flagLong) {
		if(flagLong == null) return;
		this.setLongValue(this.longValue() & ~flagLong);
	}
	
	public boolean clear(E flag) {
		if (flag == null) return false;
		else if (!isSupportedFlag(flag)) {
			logger.warning(String.format(
				"Flag %s not found in list of supported value: %s",
				flag, this.getSupportedFlags()
			));
			return false;
		}
		
		// getting the current flag value
		final Long flagLong = getFlagValue(this.protoVersion,flag);
		
		// check if the given flag is set
		if (flagLong == null) {
			logger.warning(String.format(
				"Unable to determine the long value for flag %s",
				flag
			));
			return false;
		} else if(!isSet(flagLong)) {
			// if the flag is not set
			return false;			
		}
		
		// clear the flag
		this.clear(flagLong);
		return true;
	}
	
	/* ============================================================================
	 * VALUEOF methods
	 * ============================================================================ */
	
	/**
	 * Gets the long value of the given flag.
	 * <p>
	 * If the given flag is of type {@link IVersionableValue} then {@link IVersionableValue#getValue(ProtocolVersion)} 
	 * is used to determine the flag value, otherwise {@link IFlag#getFlagValue()} is used.
	 * <p>
	 * Depending of the given protocol version, the returned value may be different.
	 * 
	 * @return
	 * 		the long value of the given flag or {@code null} if the flag object was null.
	 */
	public static <E extends IFlag> Long getFlagValue(ProtocolVersion protoVersion, E flag) {
		if(flag == null) return null;

		Long flagLong = null;
		if (flag instanceof IVersionableValue) {
			flagLong = ((IVersionableValue)flag).getValue(protoVersion);
		} else {
			flagLong = flag.getFlagValue();
		}
		return flagLong;
	}
	
	
	/* ============================================================================
	 * TO_STRING methods
	 * ============================================================================ */	
	
	/**
	 * <p>Calculates a string representation of this flag-group.</p>
	 * E.g.
	 * <pre>4=> {FL_AUTOEXP}</pre>
	 */
	@Override
	public String toString() {
		final StringBuilder buff = new StringBuilder();
		
		buff.append(this.longValue()).append("=> {");
		for (E flag : this.getSupportedFlags()) {
			final Boolean flagIsSet = this.isSet(flag);
			if (flagIsSet != null && flagIsSet.booleanValue()) {
				buff.append(flag.name())
				    .append(",");
			}
		}
		if (buff.charAt(buff.length()-1)==',') buff.setLength(buff.length()-1);
		buff.append("}");
		
		return buff.toString();
	}	
}
