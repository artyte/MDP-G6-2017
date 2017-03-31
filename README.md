# MDP-G6-2017

Built APK ready to DOWNLOAD FROM https://drive.google.com/drive/folders/0ByKpSnr45RgkdFpEaE9NanMyM1E?usp=sharing

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
	- For stopping robot <- detect "ps"

