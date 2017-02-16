# MDP-G6-2017

Instructions for adding new files:
- Use folders to represent the work you're doing (e.g. in this case, android team does work on Robot Remote Folder)
- Branching isn't really necessary if you're trying to code on separate pages

Status:
- Added bluetooth discovery
- Added ListView bluetooth view
- Added refresh bluetooth list button
- listview elements now clickable
- Change to Material light theme
- Manual refresh working

Instructions For RPI Group:
- When sending strings processed from Arduino's raw data, please format these strings for the following task:
	- For robot position <- "{"robotPosition": [x, y, clockwise degrees]}"
	- For grid <- "{"grid":75 hexadecimals}"
	- For battery <- "{"battery":0-100}"
- When receiving strings sent from Android, please activate the following functions:
	- For grabbing grid updates (does not include robot's position) <- detect "sendArena"
	- To begin exploration mode <- detect "explore"
	- To start fastest path <- detect "fastest"
	- 
