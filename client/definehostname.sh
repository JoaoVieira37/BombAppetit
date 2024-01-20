#!/bin/bash

# The IP address and hostname
IP="192.168.0.1"
HOSTNAME="BombAppetit"

# Add the entry to the /etc/hosts file
echo "$IP $HOSTNAME" | sudo tee -a /etc/hosts