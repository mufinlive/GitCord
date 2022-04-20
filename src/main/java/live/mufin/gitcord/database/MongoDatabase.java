package live.mufin.gitcord.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.Query;

public class MongoDatabase {
  private Datastore datastore;
  private MongoClient client;


  public MongoDatabase(String uri, String database) {
    this.client = MongoClients.create(uri);
    this.datastore = Morphia.createDatastore(client, database);
  }


  public void close() {
    this.client.close();
  }

  public Datastore getDatastore() {
    return datastore;
  }

  public void mapPackage(String pckg) {
    this.datastore.getMapper().mapPackage(pckg);
  }

  public void mapEntity(Class<?> entity) {
    this.datastore.getMapper().map(entity);
    this.datastore.ensureIndexes();
  }

  public <T> Query<T> createQuery(Class<T> aClass) {
    return this.getDatastore().find(aClass);
  }

  public void save(Object o) {
    this.getDatastore().save(o);
  }

}
