#!/bin/sh

################################################################################
# Installing Software for Amazon EC2 Instance
################################################################################

apt-get -y install postgresql-8.4-postgis
apt-get -y install postgis

mkdir -p gis/files
rsync -e sssh -Avz drubenson@direct.jakebowers.org:"~/CanadaContext/boundary\ files" gis/files




