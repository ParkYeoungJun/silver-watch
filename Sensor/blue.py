# -*- coding: utf-8 -*-
import bluetooth
import time
import fcm

bd_addr = "98:D3:31:FB:6C:AF"
port = 1 
sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
sock.connect((bd_addr, 1))

while 1:
	try:
		buffer = sock.recv(4096)	
		fcm.sendMessage_("심박  감지 센서", "비정상적인 심박이  감시되었어요!")
		print(buffer)
	except KeyboardInterrupt:
		break
sock.close()

