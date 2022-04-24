package live.mufin.gitcord;

import live.mufin.gitcord.commands.SetRepoCommand;
import live.mufin.gitcord.database.MongoDatabase;
import live.mufin.gitcord.events.GuildCreateEvent;
import live.mufin.gitcord.events.IssueMentionEvent;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Properties;

public class Gitcord {

  public static void main(String[] args) {
     new Gitcord().init(RunMode.DEV);
  }

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Getter
  private JDA jda;
  @Getter
  private MongoDatabase database;
  @Getter
  private Properties config;
  @Getter
  private GitHub github;

  @SneakyThrows
  public void init(RunMode mode) {
    this.config = new Properties();
    switch (mode) {
      case DEFAULT -> this.config.load(getClass().getClassLoader().getResourceAsStream("info.properties"));
      case DEV -> this.config.load(getClass().getClassLoader().getResourceAsStream("info-dev.properties"));
      case PROD -> this.config.load(getClass().getClassLoader().getResourceAsStream("info-prod.properties"));
    }

    this.jda = JDABuilder.createDefault(this.config.getProperty("TOKEN"))
       .setActivity(Activity.watching("Your GitHub repositories | v1.3"))
       .setStatus(OnlineStatus.DO_NOT_DISTURB)
       .build();

    this.database = new MongoDatabase(this.config.getProperty("DATABASE_URI"), this.config.getProperty("DATABASE"));
    this.database.mapPackage(getClass().getPackageName());

    this.github = new GitHubBuilder().withOAuthToken(this.config.getProperty("GITHUB_TOKEN")).build();

    this.jda.addEventListener(new IssueMentionEvent(this), new GuildCreateEvent(this));
    this.jda.addEventListener(new SetRepoCommand(this));

    this.registerCommands();

    this.logger.info("Currently in {} guilds with {} total members.", this.jda.getGuilds().size(), this.getMemberCount());
  }

  private int getMemberCount() {
    return this.jda.getGuilds().stream().mapToInt(Guild::getMemberCount).sum();
  }

  private void registerCommands() {
    this.jda.upsertCommand(
       Commands.slash("setrepo", "Sets the guild's repository.")
          .addOption(OptionType.STRING, "repo", "The repository that you want to link to this server.", true)
    ).queue();
  }

  private enum RunMode {
    DEFAULT, DEV, PROD
  }

}
