/**
 * 
 */
package ro.softgress.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import ro.softgress.client.Data;
import ro.softgress.client.DataRow;

/**
 * @author valer
 *
 */
public class DAO {

	private Logger logger = LoggerFactory.getLogger(DAO.class);
	
	private ColumnFamilyTemplate<String, String> template = null;
        
	public DAO() {
		CassandraHostConfigurator chc = new CassandraHostConfigurator();
		chc.setHosts("localhost:9160");
		//chc.setAutoDiscoverHosts(true);
		Cluster cluster = HFactory.getOrCreateCluster("test-cluster", "localhost:9160");
		//KeyspaceDefinition ks = cluster.describeKeyspace("demo");
		Keyspace ks = HFactory.createKeyspace("demo", cluster);
		template = new ThriftColumnFamilyTemplate<String, String>(ks,
                "metrics", 
                StringSerializer.get(),
                StringSerializer.get());
	}
	
	public void insertData(Data data) {
		// <String, String> correspond to key and Column name.
		ColumnFamilyUpdater<String, String> updater = template.createUpdater(String.valueOf(data.getTimestamp()/1000));
		//updater.setString("domain", "www.datastax.com");
		//for (Data data : dataRow.getColumns()) {
			String columnName = String.valueOf(data.getApplicationId()) + 
				String.valueOf(data.getSystemId()) + String.valueOf(data.getTimestamp());
			updater.setString(columnName, data.getValue());
		//}
		//updater.setString("time", String.valueOf(System.currentTimeMillis()));

		try {
		    template.update(updater);
		} catch (HectorException e) {
		    // do something ...
			e.printStackTrace();
		}
	}
}
