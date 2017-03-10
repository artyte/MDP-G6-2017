# MDP-G6-2017

APK BUILT DOWNLOAD FROM GDRIVE

Instructions for adding new files:
- Use folders to represent the work you're doing (e.g. in this case, android team does work on Robot Remote Folder)


Instructions for sending strings from any device:
- Append this number to the start of any string that is to be sent to another device:
	- p (to PC)
	- b (to Android)
	- a (to Arduino)
	- RPI isn't required because RPI is the message broadcaster

Instructions For Algo Group:
- When sending strings processed from Arduino's raw data to Android, please format these strings for the following task:
	- For robot position <- "{"robotPosition": [x, y, clockwise degrees]}"
	- For grid <- "{"grid":75 hexadecimals}"
	- For battery <- "{"battery":0-100}"
	- For status <- "{"status":status name}"
- When receiving strings sent from Android, please activate the following functions:
	- For grabbing grid updates (does not include robot's position) <- detect "pse"
	- To begin exploration mode <- detect "pe"
	- To start fastest path <- detect "pf"
	- For grid position <- detect int(1-18),int(1-13)
	- For battery <- detect "pb"
	- For status <- detect "pst"
	- For robot's position <- detect "pp"
	- For go straight <- detect "pF"
	- For turn left <- detect "pL"
	- For turn right <- detect "pR"