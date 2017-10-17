import pycom
from network import Bluetooth
import time

bluetooth = Bluetooth()

def conn_cb (bt_o):
    events = bt_o.events()   # this method returns the flags and clears the internal registry
    if events & Bluetooth.CLIENT_CONNECTED:
        print("Client connected")
    elif events & Bluetooth.CLIENT_DISCONNECTED:
        print("Client disconnected")

bluetooth.callback(trigger=Bluetooth.CLIENT_CONNECTED | Bluetooth.CLIENT_DISCONNECTED, handler=conn_cb)

counter = 0

while True:
    bluetooth.set_advertisement(name='testRolf', service_uuid='1234567890123456', service_data='abcdefghijklmnopqrstuvwxyz01234567890')
    bluetooth.advertise(True)
    time.sleep(5)
    bluetooth.advertise(False)
    counter = counter + 1
    time.sleep(1)
