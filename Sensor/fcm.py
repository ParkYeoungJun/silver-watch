# -*- coding: utf-8 -*-
import json
import requests
from firebase import firebase


def sendMessage_(message_title, message_body):
    server_key = 'AAAAoirmnTs:APA91bFCKLOhU7P5K-m4GYxt12dCqBR71uJPON4ImO8EtW6_E6CPlkBnD8WUeA8TspNAPHseaUBhddDvHCsOteW-F9V--MEuEsH-XPUgC05ja8e8ntMbzi3R6g3-Msrw4HkU3nz7N-sP'  # Firebase Project Settings > CLOUD MESSAGING
    fb = firebase.FirebaseApplication("https://silver-watch.firebaseio.com/", None) # DB 서버 접속
    token = fb.get('/token', None) # 서버에서  token값 받음
    headers = {
        'Authorization': 'key= ' + server_key,
        'Content-Type': 'application/json',
    }

    data = {
        'to': token,
        'notification': {
            "title" : message_title,
            "body" : message_body,
            "icon" : "myicon"
        },
    }

    response = requests.post('https://fcm.googleapis.com/fcm/send', headers=headers, data=json.dumps(data))
    print(response)
