/**
 * Licensed under the MIT license: http://www.opensource.org/licenses/mit-license.php
 */
package ro.softgress.server;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.BatchSizeHint;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import ro.softgress.client.Data;

/**
 * @author valer
 * @author mcq
 */
public class DAO {

	private final ColumnFamilyTemplate<String, String> template;
	private final Keyspace ks;

	public DAO() {
		CassandraHostConfigurator chc = new CassandraHostConfigurator();
		chc.setHosts("localhost:9160");
		// chc.setAutoDiscoverHosts(true);
		Cluster cluster = HFactory.getOrCreateCluster("test-cluster", chc);
		// KeyspaceDefinition ks = cluster.describeKeyspace("demo");
		ks = HFactory.createKeyspace("demo", cluster);
		template = new ThriftColumnFamilyTemplate<String, String>(ks, "metrics", StringSerializer.get(),
				StringSerializer.get());
	}

	public void insertData(Data data) {
		// <String, String> correspond to key and Column name.
		ColumnFamilyUpdater<String, String> updater = template.createUpdater(String.valueOf(data
				.getTimestamp() / 1000));
		// updater.setString("domain", "www.datastax.com");
		// for (Data data : dataRow.getColumns()) {
		String columnName = String.valueOf(data.getApplicationId()) + String.valueOf(data.getSystemId())
				+ String.valueOf(data.getTimestamp());
		updater.setString(columnName, data.getValue());
		// }
		// updater.setString("time",
		// String.valueOf(System.currentTimeMillis()));

		try {
			template.update(updater);
		} catch (HectorException e) {
			// do something ...
			e.printStackTrace();
		}
	}

	public void insertData(Data[] datas) {
		Mutator<String> mutator = HFactory.createMutator(ks, StringSerializer.get(), new BatchSizeHint(
				datas.length, 1));
		for (Data d : datas) {
			String columnName = String.valueOf(d.getApplicationId()) + String.valueOf(d.getSystemId())
					+ String.valueOf(d.getTimestamp());
			mutator.addInsertion(String.valueOf(d.getTimestamp() / 1000), "Standard1",
					HFactory.createStringColumn(columnName, d.getValue()));
		}
	}
}
