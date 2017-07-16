#-*- coding: utf-8 -*-
import RPi.GPIO as GPIO
import time
import fcm
import pygame

GPIO.setmode(GPIO.BCM)
PIR_PIN = 21
FLAME_PIN = 4
BUTTON_PIN = 12

GPIO.setup(PIR_PIN, GPIO.IN)
GPIO.setup(FLAME_PIN, GPIO.IN)
GPIO.setup(BUTTON_PIN, GPIO.IN)

pygame.init()
pygame.mixer.music.load("drugTime.wav")

stopTime = 0
limitTime = 5

try:
	print "PIR Module TEST (CTRL + C to exit)"
	time.sleep(2)
	print "Ready"

	while True:
		t = time.localtime()
		if GPIO.input(PIR_PIN):
			stopTime = 0
			print "%d:%d:%d Motion Detected!" % (t.tm_hour, t.tm_min, t.tm_sec)
		else :
			stopTime += 1

		if stopTime >= limitTime:
			stopTime = 0
			fcm.sendMessage_("움직임 감지 센서",str(limitTime) + "초동안 움직임이 감지되지 않았어요!")
		
		if GPIO.input(FLAME_PIN) == False:
			print("flame detected")
			fcm.sendMessage_("불꽃 감지 센서", "불꽃이 감지되었어요!");
			
		if(t.tm_hour == 8 and t.tm_min == 0 and t.tm_sec == 0 ) or (t.tm_hour == 13 and t.tm_min == 0 and t.tm_sec == 0) or (t.tm_hour == 19 and t.tm_min == 0 and t.tm_sec == 0):
			pygame.mixer.music.play()
			
		if GPIO.input(BUTTON_PIN) == False:
			fcm.sendMessage_("엄마가", "보호자분을 찾아요!");

		time.sleep(1)

except KeyboardInterrupt:
	print "Quit"
	GPIO.cleanup()

