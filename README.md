# pi-project

Create a micro-sd with Ubuntu-server 20.04 LTS

Boot up the PI

Login with the default credentials Ubuntu/Ubuntu and set a new password.

## Setup Wifi

From https://itsfoss.com/connect-wifi-terminal-ubuntu/

Determine the wireless network device

`ls ls /sys/class/net` 

Edit this file:

`sudo vim /etc/netplan/50-cloud-init.yaml` so it will look like this:

```
network:
    ethernets:
        eth0:
            dhcp4: true
            optional: true
    version: 2
    wifis:
        wlan0:
            dhcp4: true
            optional: true
            access-points:
                "SSID_name":
                    password: "WiFi_password"
```

`sudo netplan generate`
`sudo netplan apply`
`sudo systemctl start wpa_supplicant`
`sudo init 6`

## Update the system

`sudo apt update`
`sudo apt full-upgrade`


## Controll GPIO

`apt install python-gpiozero`

`pinout`


https://raspberry-projects.com/pi/command-line/io-pins-command-line/io-pin-control-from-the-command-line




## Install gpio (Deprecated)

`sudo apt-get install wiringpi`

(http://wiringpi.com/wiringpi-updated-to-2-52-for-the-raspberry-pi-4b/)
`cd /tmp`
`wget https://project-downloads.drogon.net/wiringpi-latest.deb`
`sudo dpkg -i wiringpi-latest.deb`

`sudo gpio readall`




