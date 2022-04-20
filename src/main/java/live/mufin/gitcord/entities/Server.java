package live.mufin.gitcord.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Entity
public class Server {

  public Server(String guildId, String repository) {
    this.guildId = guildId;
    this.repository = repository;
  }

  @Id
  @Getter
  private ObjectId id;

  @Getter
  @Property("guild-id")
  private String guildId;

  @Getter
  @Setter
  private String repository;

}
