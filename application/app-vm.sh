
# Run this script as sudo

# Write the commands run in the terminal
set -xe

# Setup IP of interface eth0
sudo ifconfig eth0 192.168.0.1/24 up

# Set the default gateway
sudo ip route add 192.168.1.0/24 via 192.168.0.0
sudo ip route add 192.168.2.0/24 via 192.168.0.0

# Restart NetworkManager
sudo systemctl restart NetworkManager