/**
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
package ro.softgress.client;

import java.util.Random;

/**
 * @author valer
 *
 */
public class Generator {

	private Random random = new Random(System.currentTimeMillis());
	private int applicationId = generateId();
	private int systemId = generateId();

	private int generateId() {
		Integer id = random.nextInt(10);
		random.setSeed(System.currentTimeMillis() + id);
		return id;
	}

	public Data generateData() {
		long timestamp = System.currentTimeMillis();
		long valueLong = random.nextLong();
		String value = "value" + valueLong;
		random.setSeed(timestamp + valueLong);
		return new Data(timestamp, applicationId, systemId, value);
	}
}
