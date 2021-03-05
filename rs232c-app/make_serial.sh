#!/bin/bash
sudo killall socat
sudo socat PTY,raw,link=/dev/ttyS10 PTY,raw,link=/dev/ttyS11 &
sleep 1
sudo chown $USER:tty /dev/ttyS10
sudo chown $USER:tty /dev/ttyS11
