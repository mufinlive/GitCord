package live.mufin.gitcord.commands;

import dev.morphia.query.experimental.filters.Filters;
import live.mufin.gitcord.Gitcord;
import live.mufin.gitcord.entities.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SetRepoCommand extends ListenerAdapter {

  private final Gitcord gitcord;

  public SetRepoCommand(Gitcord gitcord) {
    this.gitcord = gitcord;
  }

  @Override
  public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
    if (!event.getName().equals("setrepo")) return;
    if(!event.getMember().hasPermission(Permission.ADMINISTRATOR)) return;

    String repo = event.getOption("repo").getAsString();

    this.gitcord.getDatabase().createQuery(Server.class)
       .filter(Filters.eq("guild-id", event.getGuild().getId()))
       .stream()
       .findAny()
       .ifPresent(server -> {

         try {
           if (this.gitcord.getGithub().getRepository(repo) == null) {
             return;
           }

           server.setRepository(repo);
           this.gitcord.getDatabase().save(server);
           event.replyEmbeds(
              new EmbedBuilder()
                 .setColor(0x8854d0)
                 .setTitle(String.format("Successfully set repository to `%s`.", repo))
                 .build()
           ).queue();
         } catch (IOException e) {
           e.printStackTrace();
         }

       });
  }
}
