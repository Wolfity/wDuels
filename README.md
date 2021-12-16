# wDuels
a duels plugin

### **Introduction**
In Duels, you can fight people in a 1 versus one situation, 
where both of you have the same kit. wDuels provides several duel game modes, 
such as but not limited to: No Debuff, UHC, Combo, Debuff, Classic, Diamond, Archer, and many more.

### **How to set it up**
Setting up duels is extremely easy! There are only a few steps.

**Setting the duels spawn**\
Make sure you set a duels spawn using the command [/duels setspawn].
This will create a spawn for the duels lobby

**Creating an Arena**\
How do you create an arena? Simple, you can execute the command
[/duels createarena arenaName]. After that, you enter the arena creation mode. 
You will receive a wand; 
you will have to select the two corners of the map with that wand. 
Now the arena selection has been made!

**Setting player spawns**\
To set a player spawn you simply execute the command [/duels addspawn arenaName].

**Kits**\
All the kits per game mode are already created in the kits.yml config file. 
If you would like to make any changes, you can! Make sure you fill out everything appropriately,
though!



### **Kill Effects**
A unique set of kill effects is provided, you can select them if you are in the duels game mode.
To enter the duels game mode you simply just execute the command [/duels join]

### **Win Effects**
wDuels does not only provide Kill Effects but also win effects. 
You can select Win Effects in the Duels game mode, 
you enter that by executing the command [/duels join]

### **Permissions**
Win and Kill effects are given by the permissions, the permissions are named as followed.

**Win Effects**\
`wduels.wineffect.explosion`\
`wduels.wineffect.firework`\
`wduels.wineffect.angryvillager`


**Kill Effects**\
`wduels.killeffect.explosion`\
`wduels.killeffect.hearts`\
`wduels.killeffect.firework`


### **Commands**
`/duels createarena <arenaName> ` Creates an arena with the specified name\
`/duels deletearena <arenaName> ` Delete the specified arena\
`/duels join` Join the duels gamemode\
`/duels leave` Leave the duels gamemode\
`/duels addspawn <arenaName> ` Add a spawn for the specified arena\
`/duels setspawn` Sets the duel lobby spawn\
`/duels duel <username> ` Allows you to duel a specific user\
`/duels help` View the help message\
`/duels admin` Allows you to view the admin commands -> requires the permission `duels.admin`


### **Storage**
Things such as stats, active kill/win effect are all stored in SQLite.
I may add additional storage options in the future.
