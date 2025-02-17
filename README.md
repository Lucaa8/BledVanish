# Vanish
This plugin allows your staff to become invisible to players.

## Commands
`/v` toggles the current vanish state.  
`/v help` displays information about the player's vanish state and provides help for plugin commands.  
`/v save` sets the staff's current vanish mode to `SAVE`, ensuring they rejoin the server with the same vanish state they had when leaving. (E.g., if they leave while vanished, they will rejoin vanished.)  
`/v always` makes the staff always join the server in a vanished state.  
`/v never` makes the staff always join the server in a visible state like a regular player.  

## Permissions
`bledvanish.command.vanish` grants players permission to use the `/v` command, all its subcommands (`/v help`, etc.), and the ability to see other vanished players.  

## Features
### Effects
Players in a vanished state receive a permanent invisibility effect and a glowing effect (to see other vanished players). These effects are removed when they become visible again.  

### Modes
As described in the commands section, staff members can configure their vanish state upon joining the server using the `/v` command.  

### Actions
Currently, vanished players cannot pick up items or arrows from the ground. In a future update, they will be able to open chests silently. 

### Special Cases
- When a player is promoted to Moderator, they won’t immediately gain the ability to see vanished players. They must leave and rejoin the server for the changes to take effect.  
- When a Moderator is demoted, they will still be able to see vanished players until they leave and rejoin the server.  
- If a player is demoted while vanished (whether online or offline), they will automatically become visible the next time they connect.

## API
This plugin comes with a small but complete API which allows all your other plugins to know when a player is vanished or not.

## Maven Dependency
```xml
<repository>
    <id>dev-mc</id>
    <url>https://mvn.luca-dc.ch/repository/dev-mc/</url>
</repository>
<dependency>
    <groupId>ch.luca008</groupId>
    <artifactId>Vanish</artifactId>
    <version>1.0</version>
</dependency>
```

## Get the API
```java
BledVanish vanish = BledVanish.getInstance();
```

### Vanishing a Player
With the method `BledVanish#setVanished(@Nonnull Player player, boolean state, boolean force)`
```java
@EventHandler
public void onJoin(PlayerJoinEvent e) {
    Player p = e.getPlayer();
    BledVanish vanish = BledVanish.getInstance();
    //force should be always false, if set to true, VanishStateChangeEvent won't be called and cannot be potentially cancelled by other plugins.
    vanish.setVanished(p, true, false);
}
```
(If the player is already in the given `state`, the call will be ignored silently and `VanishStateChangeEvent` won't be called)

### Check a Player
With the method `BledVanish#isVanished(@Nonnull Player player)`
```java
@EventHandler
public void onJoin(PlayerJoinEvent e) {
    Player p = e.getPlayer();
    if(BledVanish.getInstance().isVanished(p)) {
        e.setJoinMessage(null);
    }
}
```
(A player's vanish state is set at `PlayerJoinEvent` with priority `LOWEST`, in other words: as soon as possible. So with every priority above `LOWEST` e.g. `LOW` or `NORMAL`, the `isVanished` method will return a correct result)

### Check permission
With the method `BledVanish#canVanish(@Nonnull Player player)` \
This method just checks if the given player does have the `bledvanish.command.vanish` permission.

### Change and get a Player's vanish mode
```java
@EventHandler
public void onRandomEvent(PlayerRandomEvent e) {
    Player p = e.getPlayer();
    //SET mode - ignore silently if the player already has this mode enabled
    BledVanish.VanishMode.ALWAYS.set(p);
    //GET mode - if never set for this player : VanishMode.SAVE by default
    BledVanish.VanishMode playerMode = BledVanish.VanishMode.get(p);
    //playerMode now contains "VanishMode.ALWAYS"
}
```

### VanishStateChangeEvent
Quick example if you wan't to "fake" a moderator leaving the server in the chat when they go invisible!
```java
@EventHandler
public void onVanish(VanishStateChangeEvent e)
{
    Player p = e.getPlayer();
    String message = e.willBeVanished() ? " §cleft the server!" : " §ajoined the server!";
    //String message = e.isCurrentlyVanished() ? " §ajoined the server!" : " §cleft the server!"; //Choose whatever u find the most intuitive

    Bukkit.broadcastMessage(p.getName() + message);
}
```
