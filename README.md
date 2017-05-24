# Channel-Inventory <a href="#"><img src="https://img.shields.io/badge/Version-0.0.1-brightgreen.svg" alt="Latest version"></a>

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
There may one day be a way to search a room, and perhaps in
the distant future a way to allow for personal inventory management (so things can't be put in a
room if you don't have them, and taking something out of a room will move it to the individual's
personal inventory).

## Command List
_TBD_

## Neat Info About Hosting
_TBD_

## Details About Permanent Storage
_TBD_
