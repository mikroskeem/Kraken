# Kraken
Light-weight object-oriented Tab-API for the Bukkit/Spigot API

**Note:** Broken currently. Do not try to use.

### Installation

#### Option 1: Maven repository 

*Coming soon*

#### Option 2: Local maven

Invoke `mvn install` and add this to your dependencies:
```xml
<dependency>
    <groupId>com.alexandeh.kraken</groupId>
    <artifactId>Kraken</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```


### Usage

#### Instantiate Kraken in your onEnable:

```java
public class YourPlugin extends JavaPlugin {
  public void onEnable() {
      // All your other stuff
      new Kraken(this);
  }
} 
```
  
#### Example code
```java
public class YourPlugin extends JavaPlugin {
    @EventHandler
    public void onPlayerTabCreateEvent(PlayerTabCreateEvent event) {
        PlayerTab playerTab = event.getPlayerTab();
        playerTab.getByPosition(0, 0).text("Static Text").send();
    }
}
```

##### Result
![result](https://i.gyazo.com/3ca29baf4bce8d9402885a954b7dbcd6.png)
