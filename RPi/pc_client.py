import socket
import string
import time
import threading



# Client code for PC to connect to the RPi

class Test(threading.Thread):
	def __init__(self):
		threading.Thread.__init__(self)
		self.ip = "192.168.6.6" # Connecting to IP address of MDPGrp2
		self.port = 8089
		# message = "Hello World!"
		# message = list(string.ascii_lowercase)


		# Create a TCP/IP socket
		self.client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.client_socket.connect((self.ip, self.port))

# Send data
	def write(self, count = 0):
		print "Enter text to send: "
		msg = raw_input()
		while True:
			# if (count == 1):
			# 	break
			self.client_socket.send(msg)
			print "sending: ", msg
			# print "Enter text to send: "
			msg = raw_input("Enter a value:")
			count += 1
		print "quit write()"

	# Receive data
	def receive(self):
		while True:
			data = self.client_socket.recv(1024)
			if len(data) == 0:
				print "quitting..."
				break
			print "Data received: %s \n" % data
			# while True:
			# 	if (data == 'q' or len(data) == 0):
			# 		break
		print "quit receive()"
	
	def keep_main(self):
		while True:
			time.sleep(0.5)

if __name__ == "__main__":
	test = Test()


	rt = threading.Thread(target = test.receive)
	wt = threading.Thread(target = test.write)

	rt.daemon = True
	wt.daemon = True

	rt.start()
	wt.start()
	print "start rt and wt"

	test.keep_main()

	# rt.join()
	# wt.join()
	# print "stop rt and wt"

	# Close connections
	self.client_socket.close()
	print "End of client program"
