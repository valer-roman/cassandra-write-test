/**
 * 
 */
package ro.softgress.client;

import java.io.Serializable;

/**
 * @author valer
 *
 */
public class Data implements Serializable {

	private long timestamp;
	private int applicationId;
	private int systemId;
	private String value;
	
	public Data(long timestamp, int applicationId, int systemId, String value) {
		this.timestamp = timestamp;
		this.applicationId = applicationId;
		this.systemId = systemId;
		this.value = value;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the applicationId
	 */
	public int getApplicationId() {
		return applicationId;
	}

	/**
	 * @param applicationId the applicationId to set
	 */
	public void setApplicationId(int applicationId) {
		this.applicationId = applicationId;
	}

	/**
	 * @return the systemId
	 */
	public int getSystemId() {
		return systemId;
	}

	/**
	 * @param systemId the systemId to set
	 */
	public void setSystemId(int systemId) {
		this.systemId = systemId;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Data[ts:" + timestamp + ",value:" + value + "]");
		return sb.toString();
	}
	
}
