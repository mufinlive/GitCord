package live.mufin.gitcord.events;

import dev.morphia.query.experimental.filters.Filters;
import live.mufin.gitcord.Gitcord;
import live.mufin.gitcord.entities.Server;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.github.GHFileNotFoundException;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IssueMentionEvent extends ListenerAdapter {

  private final Pattern pattern = Pattern.compile("(#\\d+)");

  private final Gitcord gitcord;
  public IssueMentionEvent(Gitcord gitcord) {
    this.gitcord = gitcord;
  }

  @Override
  public void onMessageReceived(@NotNull MessageReceivedEvent event) {

    this.gitcord.getDatabase().createQuery(Server.class)
       .filter(Filters.eq("guild-id", event.getGuild().getId()))
       .stream()
       .filter(server -> server.getRepository() != null && !server.getRepository().isEmpty())
       .findAny()
       .ifPresent(server -> {
         String message = event.getMessage().getContentStripped();
         Matcher matcher = pattern.matcher(message);
         List<String> matches = new ArrayList<>();

         while(matcher.find())
           matches.add(matcher.group());

         matches.forEach(match -> {
           try {
             int id = Integer.parseInt(match.replace("#", ""));

             try {
               GHIssue issue = this.gitcord.getGithub().getRepository(server.getRepository()).getIssue(id);

               MessageEmbed embed = new EmbedBuilder()
                  .setColor(issue.getState() == GHIssueState.CLOSED ? 0xeb3b5a : 0x26de81)
                  .setTitle(issue.isPullRequest() ? String.format("Pull request %s:", match) : String.format("Issue %s:", match))
                  .addField(new MessageEmbed.Field("Title:", issue.getTitle() ,true))
                  .addField(new MessageEmbed.Field("Status: ", issue.getState().name(), true))
                  .addField(new MessageEmbed.Field("Author: ", getOrUnknown(issue.getUser().getName()), true))
                  .addField(new MessageEmbed.Field("Content:", getOrUnknown(StringUtils.abbreviate(issue.getBody(),"...", 200)), false))
                  .addField(new MessageEmbed.Field("URL: ", issue.getHtmlUrl().toString(), false))
                  .build();

               event.getMessage().replyEmbeds(embed).queue();
             } catch (GHFileNotFoundException ignored) {
               MessageEmbed embed = new EmbedBuilder()
                  .setColor(0xfa8231)
                  .setTitle("Couldn't find issue!")
                  .build();
                event.getMessage().replyEmbeds(embed).queue();
             }

           } catch (IOException e) {
             e.printStackTrace();
           }
         });
       });
  }

  private String getOrUnknown(String input) {
    return input == null ? "Unknown" : input;
  }

}
