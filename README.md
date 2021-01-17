# pi-project

Create a micro-sd with Pi OS 32-bit
Boot up with to the screen, keyboard and mouse.
Connect to one or more WiFi networks.

ssh to the machine using u:pi, p:raspberry

## Control GPIO

`pinout`

`gpio -v`

`gpio readall`

`gpio read 1`

`gpio mode 1 output`
`gpio write 1 0`
`gpio write 1 1`

`gpio mode 1 pwm`
`gpio pwm 1 512`

## Install PI4J

sudo apt-get install wiringpi

## Install JDK8

sudo apt update
sudo apt upgrade
sudo apt install openjdk-8-jdk

## Install Maven

`wget https://ftpmirror1.infania.net/mirror/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz`

`tar xvfz apache-maven-3.6.3-bin.tar.gz`

`vi ~/.bashrc`        # and add the lines:

`export M2_HOME=~/apache-maven-3.6.3`

`export "PATH=$PATH:$M2_HOME/bin"`

`mvn -version`





# pi-project Ubuntu

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


## Control GPIO

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


## Install PI4J

curl -sSL https://pi4j.com/install | sudo bash

or

sudo apt-get install wiringpi


## Build PI4J on 64-bit

sudo apt-get update
sudo apt-get upgrade
sudo apt-get -y install build-essential
sudo apt-get -y install cmake git libgtk2.0-dev pkg-config libavcodec-dev libavformat-dev libswscale-dev
sudo apt-get -y install python-dev python-numpy libtbb2 libtbb-dev libjpeg-dev libpng-dev libtiff-dev libjasper-dev libdc1394-22-dev
sudo apt-get -y install gcc g++
sudo apt-get -y install less
sudo apt-get -y install make
sudo apt-get -y install ant
sudo apt-get -y install tree
tar zxf 64-pi.tar.gz
tar zxf jdk-arm64.tar.gz
sudo mv jdk1.8.0_171/ /opt/
sudo mv apache-maven-3.5.3 /opt/

On a non-pi machine:

copy the file from https://github.com/fguiet/automationserver-v2/blob/main/libpi4j/builder/Dockerfile
docker build -t libpi4j .
docker run -it --rm -v /tmp:/tmp libpi4j /bin/bash -c "cp -r /applications/builder/output/libpi4j.so /tmp"



