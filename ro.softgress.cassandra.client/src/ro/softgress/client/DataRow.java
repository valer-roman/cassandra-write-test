/**
 * 
 */
package ro.softgress.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author valer
 *
 */
public class DataRow implements Serializable {

	private long second;
	private List<Data> columns = new ArrayList<Data>();

	/**
	 * @return the second
	 */
	public long getSecond() {
		return second;
	}
	/**
	 * @param second the second to set
	 */
	public void setSecond(long second) {
		this.second = second;
	}
	/**
	 * @return the columns
	 */
	public List<Data> getColumns() {
		return columns;
	}
	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<Data> columns) {
		this.columns = columns;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DataRow[" + second + ",data:" + columns.size() + "]");
		return sb.toString();
	}
	
}
