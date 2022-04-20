package live.mufin.gitcord.events;

import live.mufin.gitcord.Gitcord;
import live.mufin.gitcord.entities.Server;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GuildCreateEvent extends ListenerAdapter {

  private final Gitcord gitcord;
  public GuildCreateEvent(Gitcord gitcord) {
    this.gitcord = gitcord;
  }

  @Override
  public void onGuildJoin(@NotNull GuildJoinEvent event) {
    this.gitcord.getDatabase().save(new Server(event.getGuild().getId(), "facebook/react"));
  }

}
