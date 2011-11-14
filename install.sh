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
# The .gitignore file will ignore `gis/files` so that you can load stuff there without committing it to the repository (for size concerns).

### Projection Info
# All the Canda files use:
# GEOGCS["GCS_North_American_1983",DATUM["D_North_American_1983",SPHEROID["GRS_1980",6378137,298.257222101]],PRIMEM["Greenwich",0],UNIT["Degree",0.017453292519943295]]
# This appears to be EPSG: 4269
#
# Google uses EPSG:3857

# cd files
# 
# shp2pgsql -s 4269 -I -W LATIN1 gccs000a06a_e.shp ccs | psql canada
# shp2pgsql -s 4269 -I -W LATIN1 gcd_000a06a_e.shp cd | psql canada
# shp2pgsql -s 4269 -I -W LATIN1 gcma000a06a_e.shp cma | psql canada
# shp2pgsql -s 4269 -I -W LATIN1 gcsd000a06a_e.shp csd | psql canada
# shp2pgsql -s 4269 -I -W LATIN1 gct_000a06a_e.shp ct | psql canada
# shp2pgsql -s 4269 -I -W LATIN1 gdpl000a06a_e.shp dpl | psql canada
# shp2pgsql -s 4269 -I -W LATIN1 gfed000a06a_e.shp fed | psql canada
# shp2pgsql -s 4269 -I -W LATIN1 gfsa000a06a_e.shp fsa | psql canada
# shp2pgsql -s 4269 -I -W LATIN1 gpr_000a06a_e.shp pr | psql canada
# shp2pgsql -s 4269 -I -W LATIN1 gua_000a06a_e.shp ua | psql canada

  
