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

# This next section expects all the files from the unzip above.
# - all the varioius district files in a subdirectory called `files`:
#   gccs000a06a_e.shp
#   gcd_000a06a_e.shp
#   gcma000a06a_e.shp
#   gcsd000a06a_e.shp
#   gct_000a06a_e.shp
#   gdpl000a06a_e.shp
#   gfed000a06a_e.shp
#   gfsa000a06a_e.shp
#   gpr_000a06a_e.shp
#   gua_000a06a_e.shp

### Projection Info
# All the Canda files use:
# GEOGCS["GCS_North_American_1983",DATUM["D_North_American_1983",SPHEROID["GRS_1980",6378137,298.257222101]],PRIMEM["Greenwich",0],UNIT["Degree",0.017453292519943295]]
# This appears to be EPSG: 4269
#
# Google uses EPSG:3857
alias shp2pgsql=/usr/lib/postgresql/9.1/bin/shp2pgsql
shp2pgsql -s 4269 -I -W LATIN1 gis/files/gccs000a06a_e.shp ccs | sudo -u postgres psql canada
shp2pgsql -s 4269 -I -W LATIN1 gis/files/gcd_000a06a_e.shp cd | sudo -u postgres psql canada
shp2pgsql -s 4269 -I -W LATIN1 gis/files/gcma000a06a_e.shp cma | sudo -u postgres psql canada
shp2pgsql -s 4269 -I -W LATIN1 gis/files/gcsd000a06a_e.shp csd | sudo -u postgres psql canada
shp2pgsql -s 4269 -I -W LATIN1 gis/files/gct_000a06a_e.shp ct | sudo -u postgres psql canada
shp2pgsql -s 4269 -I -W LATIN1 gis/files/gdpl000a06a_e.shp dpl | sudo -u postgres psql canada
shp2pgsql -s 4269 -I -W LATIN1 gis/files/gfed000a06a_e.shp fed | sudo -u postgres psql canada
shp2pgsql -s 4269 -I -W LATIN1 gis/files/gfsa000a06a_e.shp fsa | sudo -u postgres psql canada
shp2pgsql -s 4269 -I -W LATIN1 gis/files/gpr_000a06a_e.shp pr | sudo -u postgres psql canada
shp2pgsql -s 4269 -I -W LATIN1 gis/files/gua_000a06a_e.shp ua | sudo -u postgres psql canada

### Install Apache and PHP, configuring for use with Postgresql

sudo apt-get -y install apache2
sudo apt-get -y install php5 libapache2-mod-php5 php5-pgsql 
