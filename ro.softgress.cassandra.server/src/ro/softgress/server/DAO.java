/**
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
package ro.softgress.server;

import ro.softgress.client.Data;

/**
 * Common interface to implement by any db system to test
 * @author val
 */
public interface DAO {

	public void insertData(Data[] datas);
	
}
