# Channel-Inventory <a href="#"><img src="https://img.shields.io/badge/Version-0.1.1-brightgreen.svg" alt="Latest version"></a>

## Original Concept
This is a Discord bot based on Bastian Oppermann's excellent [JavacordBot](https://github.com/BtoBastian/JavacordBot).
I'm a player and frequent contributor to a large-scale chat-based roleplaying community that is run
on [Discord](https://discordapp.com/). Each RPG group has its own server, and channels on that server
are used to represent rooms.  I noticed the degree to which we were struggling with inventory
management - that is, someone would roleplay putting down their armor and shield in a room, and
we'd lose track of the fact that there was now a shield and a suit of armor in there.  Continuity
of play suffered.

To meet this need, I started developing a Discord bot to manage inventory for us.  The concept is
pretty straightforward: a channel has one and only one room (or no room, if the channel isn't 
intended to act like one, in which case there will be no inventory data for it).  The room can
have a name (like "West Armory") independent of the channel name and a text description.  Each room
then can have items put into it, and the items themselves may be containers with more items. There
are commands to look at what's there, commands to put new things, commands to take things, update
descriptions, remove entire rooms.

### Ideas for the Future
There may one day be a way to search a room, and perhaps in the distant future a way to allow for 
personal inventory management (so things can't be put in a room if you don't have them, and taking 
something out of a room will move it to the individual's personal inventory).  I also started down 
the path of being able to put things _in_ other things and _on_ and _behind_ and _under_ other 
things - but that got really hairy.  It's still in my loose "backlog" of things to do, but at the 
moment I'm not sure it'll be wanted or needed.

If you have any clever ideas for new features, email them to qmaster.bot@gmail.com - or better yet,
send a pull request!  The GitHub repository can be found at [channel-inventory](https://github.com/allenr98/channel-inventory).

## Command List

### Room Commands
| Command | Description | Alias(es) |
|---|---|---|
| `!!room` | What room am I in? | `!!rm` |
| `!!room describe` | Retrieve the room name and description | `!!(room, rm) (describe, desc, d)` |
| `!!room describe -d <description>` | Replace the room's description. | `!!(room, rm) (describe, desc, d) -d <description>` |
| `!!room remove` | Remove the room associated with the current channel. *Warning: this will also remove all the room's inventory!* | `!!(room, rm)) (remove, rem, r)` |
| `!!room add <name> -d <description>` | Add the room with the specified name and description. | `!!((room, rm) add <name> -d <description>` |

### Inventory Control Commands
| Command | Description | Alias(es) |
|---|---|---|
| `!!put <item>` | Add the named item to the room's inventory. If the item already exists, increment the quantity by 1. | |
| _arguments_ | These arguments can be added after the <item> name in a !!put command. | |
| |   `-q <#>` : specify the quantity to !!put.  Must be a non-negative integer.  If it's 0 and the item already exists, it will be removed. | |
| |   `-p <price>` : specify a free-format text price (e.g. "10 gp" or "120 kroners") | |
| |   `-d <description>` : include an item description. | |
| `!!take <item>` | Take an item from the inventory. Only works for items that have no associated price. Taking the last one removes it from the inventory. | |
| _arguments_ | These arguments can be added after the <item> name in a !!put command. | |
| |   `-q <#>` : specify the quantity to !!take.  Must be a non-negative integer.  If it's 0 and the item already exists, it will be removed. | |
| `!!buy <item>` | Buy an item from the inventory. The buyer and room admin/owner will both be private messaged with the transaction details. Buying the last one removes it from the inventory. | |
| _arguments_ | These arguments can be added after the <item> name in a !!put command. | |
| |   `-q <#>` : specify the quantity to !!buy.  Must be a non-negative integer.  If it's 0 and the item already exists, it will be removed. | |

### Administrative/Utility Commands
| Command | Description | Alias(es) |
|---|---|---|
| `!!save` | Write the server contents to storage; whenever something changes, this will happen automagically. | |
| `!!read` | Read server contents from storage. Everything added since the last inv!server write will be lost! | |
| `!!help` | Get command help | `!!commands` |
| `!!info` | Shows information about the bot | |
| `!!admin` | Shows who the current room admin/owner is | `!!a` |
| `!!admin set` | Set yourself as the room admin/owner.  Only works if there's no admin currently set. | `!!a set` |
| `!!admin set <@mention>` | Set the @mentioned user as the room admin/owner.  Can only be done by the current owner. | `!!a set <@mention>` |

## Details About Permanent Storage
To keep operational costs minimal and spare the effort of integrating with a database for the first release, the bot
currently stores all data in the server's file system.  This is not entirely stable and may be prone to loss of data
and bandwidth/disk storage limits. ***Users of this bot in its 0.1 version do so at their own risk***. As development
continues, the bot will become more stable and grow a persistent datastore.
