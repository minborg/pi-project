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

## Install gpio

`sudo apt-get install wiringpi`
`sudo gpio readall`




