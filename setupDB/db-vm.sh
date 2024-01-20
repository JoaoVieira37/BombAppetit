# Run this script as sudo
# Write the commands run in the terminal
set -xe


# NETWORKING

# Setup IP of interface eth0
sudo ifconfig eth0 192.168.1.1/24 up

# Set the default gateway
sudo ip route add 192.168.0.0/24 via 192.168.1.0
sudo ip route add 192.168.2.0/24 via 192.168.1.0

# Restart NetworkManager
sudo systemctl restart NetworkManager

# DATABASE

# Install postgresql if needed
sudo apt-get install postgresql

# Allow postgresql to listen to connections
sudo echo "listen_addresses = '192.168.1.1'" >> /etc/postgresql/16/main/postgresql.conf

# Allow connections to the DB from a certain ip
sudo echo "host  all all 192.168.0.1/24 password" >> /etc/postgresql/16/main/pg_hba.conf
sudo echo "hostssl   all all 192.168.0.1/24 password" >> /etc/postgresql/16/main/pg_hba.conf

# Start the database service
sudo service postgresql stop
sudo service postgresql start

# Create user 'app' in database
sudo -u postgres dropdb bombappetit -e
sudo -u postgres createdb bombappetit -e
sudo -u postgres psql -d bombappetit -f db-init.sql