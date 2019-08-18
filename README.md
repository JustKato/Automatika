# Automatika

## Description
A plugin for minecraft CraftBukkit/Spigot/PaperMC that adds new machinery to the game to help with automation

## Features
- New Items
    - Vaccum Hopper
    - Block Placer
    - Block Breaker
    - Auto Dropper
    
- Configuration
    - Full control of all featues
    - Enable/Disable particles/sound
    - Configure custom item settings
    - Configure naming convention
    - Custom Texture Support
- API
    - Generate item Easily

## Items
#### Vaccum Hopper
The vaccum hopper attracts items and once they are close enough it 
will suck them in it is highly recommended to be placed level with 
the ground
#### Block Breaker
Apply a redstone signal and it will break the block in front of it, 
add any item in the empty slot from the block breaker's inventory to 
upgrade it's mine capabilities.  
Breaking diamond_ore with an empty block breaker is not possible as
it will simulate breaking the diamond ore with an empty hand.

#### Block Placer
Once a redstone signal is applied it will place the block from inside
of it's inventory, if it doesn't have a valid block ( an item would be invalid )
it will make a failure sound, the same applies if the front of the block is
being obscured by another.

#### Auto Dropper
Once an item has been mechanically and not manually added inside of the
dropper inventory ( Hopper, OtherDropper, etc... ). It will drop it 
after exactly 10 ticks.

  
## Admin Tools
  
`/Automatika og` - Opens the admin GUI for spawning in items   
`/Automatika reload` - Reloads the config   
  
  
## API

#### Spawning in the items

You can find the items under `com.justkato.Automatika.Items.*`, every
item has the `GenerateItem()` method. Use that to spawn in the items
into your GUI/World/Recipe

##### Example:
```java
import com.justkato.Automatika.Items.*;

class MyGUI {
    public static Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(null, 9*5, "My Cool Gui");
        // Generate a Vaccum Hopper
        ItemStack vaccum_dropper = VaccumDropper.GenerateItem();
        inv.add(vaccum_dropper);

        return inv;
    }
}

```