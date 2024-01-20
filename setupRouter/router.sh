
# Run this script as sudo

# Write the commands run in the terminal
set -xe

# Setup IP of interface eth0/eth1/eth2
sudo ifconfig eth0 192.168.0.0/24 up
sudo ifconfig eth1 192.168.1.0/24 up
sudo ifconfig eth2 192.168.2.0/24 up

# Enable IP forwarding
sudo sysctl net.ipv4.ip_forward=1

# Restart NetworkManager
sudo systemctl restart NetworkManager

# Setup forwarding rules
sudo iptables -P FORWARD ACCEPT
sudo iptables -F FORWARD

#Firewall rules
sudo iptables -F

sudo iptables -P FORWARD DROP

sudo iptables -A FORWARD -s 192.168.1.0/24 -d 192.168.0.0/24 -j ACCEPT
sudo iptables -A FORWARD -s 192.168.0.0/24 -d 192.168.1.0/24 -j ACCEPT

sudo iptables -A FORWARD -s 192.168.2.0/24 -d 192.168.0.0/24 -j ACCEPT
sudo iptables -A FORWARD -s 192.168.0.0/24 -d 192.168.2.0/24 -j ACCEPT

sudo iptables -A FORWARD -s 192.168.2.0/24 -d 192.168.1.0/24 -j DROP
sudo iptables -A FORWARD -s 192.168.1.0/24 -d 192.168.2.0/24 -j DROP