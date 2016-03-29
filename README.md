# Legacy
**Legacy v2.0**

Legacy keeps track of each player's time spent in your server.  Similar to the "/played" command found in MMO games like World of Warcraft, you can now check the time of yourself and other players.  The time is displayed in days, hours, and minutes, however the configuration file is accurate up to the second.  Legacy is very light-weight and requires zero configuration, except simple permissions.

**Features**
- Keeps track of each player's game time
- Pauses game time if player is idle
- Display your time or another player's time
- Display leaderboard of top players
- Comments, suggestions, or bug reports post below, or (more preferably) to our GitHub page.

**Commands**
- /legacy - Displays your own time on the server
- /legacy (player) - Displays another player's time on the server
- /legacy [top] - Displays the top number of players on the server
- /legacy [reload] - Reloads Legacy's config files

**Permissions**
- legacy.check - Displays your own time on the server
- legacy.others - Displays another player's time on the server
- legacy.top - Displays the top number of players on the server
- legacy.reload - Reloads Legacy's config files

**Known Caveats**
- Data from previous Legacy version is unusable
- The config from any Legacy version  before this will not work. Delete config.yml before installing.
