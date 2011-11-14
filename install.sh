#!/bin/sh

################################################################################
# Installing Software for Amazon EC2 Instance
################################################################################

sudo apt-get -y install postgresql-9.1-postgis
sudo apt-get -y install postgis

mkdir -p gis/files
rsync -e ssh -r -Avz drubenson@direct.jakebowers.org:"~/CanadaContext/boundary\ files" gis/files

unzip "gis/files/boundary files/gccs000a06a_e.zip" -d gis/files
unzip "gis/files/boundary files/gcd_000a06a_e.zip" -d gis/files
unzip "gis/files/boundary files/gcma000a06a_e.zip" -d gis/files
unzip "gis/files/boundary files/gcsd000a06a_e.zip" -d gis/files
unzip "gis/files/boundary files/gct_000a06a_e.zip" -d gis/files
unzip "gis/files/boundary files/gdpl000a06a_e.zip" -d gis/files
unzip "gis/files/boundary files/gfed000a06a_e.zip" -d gis/files
unzip "gis/files/boundary files/gfsa000a06a_e.zip" -d gis/files
unzip "gis/files/boundary files/gpr_000a06a_e.zip" -d gis/files
unzip "gis/files/boundary files/gua_000a06a_e.zip" -d gis/files

### Creating the Postgresql user and database
sudo -u postgres createdb postgis-template
sudo -u postgres createlang plpgsql postgis-template

POSTGIS_SQL_PATH=/usr/share/postgresql/9.1/contrib/postgis-1.5/
sudo -u postgres psql -d postgis-template -f $POSTGIS_SQL_PATH/postgis.sql
sudo -u postgres psql -d postgis-template -f $POSTGIS_SQL_PATH/spatial_ref_sys.sql

sudo -u postgres createdb -T postgis-template canada

