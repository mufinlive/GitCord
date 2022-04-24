# GitCord
Discord bot that replies to your messages with information about Issues and Pull Requests.

Invite: https://mfn.lv/gitcord

## Runnning yourself
Change [this line](https://github.com/mufinlive/GitCord/blob/4d3e343142a96f1f249788579b8650e3421b413b/src/main/java/live/mufin/gitcord/Gitcord.java#L27) to whatever mode you want to use; `DEV`, `PROD` or `DEFAULT`. Then copy [the info file](https://github.com/mufinlive/GitCord/blob/master/src/main/resources/info.properties) into `info(-dev/-prod).properties`. Then build the project with `mvn package` and run the jar with `java -jar target/gitcord-1.x.jar`.
