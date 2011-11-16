install: .completed/001_INSTALL_SOFTWARE

.completed:
	mkdir -p .completed

files: 
	mkdir -p files

.completed/001_FETCH_FILES: .completed 
	rsync -e ssh -r -Avz drubenson@direct.jakebowers.org:"~/CanadaContext/boundary\ files" files
	touch .completed/001_FETCH_FILES

AGINSTALL = sudo apt-get -y install
.completed/002_INSTALL_SOFTWARE: .completed
	$(AGINSTALL) postgresql-9.1-postgis
	$(AGINSTALL) postgis
	$(AGINSTALL) apache2
	$(AGINSTALL) php5 libapache2-mod-php5 php5-pgsql 
	sudo apache2ctl restart 
	touch .completed/002_INSTALL_SOFTWARE

.completed/003_UNZIP_FILES: .completed/001_FETCH_FILES
	unzip "files/boundary files/gccs000a06a_e.zip" -d files
	unzip "files/boundary files/gcd_000a06a_e.zip" -d files
	unzip "files/boundary files/gcma000a06a_e.zip" -d files
	unzip "files/boundary files/gcsd000a06a_e.zip" -d files
	unzip "files/boundary files/gct_000a06a_e.zip" -d files
	unzip "files/boundary files/gdpl000a06a_e.zip" -d files
	unzip "files/boundary files/gfed000a06a_e.zip" -d files
	unzip "files/boundary files/gfsa000a06a_e.zip" -d files
	unzip "files/boundary files/gpr_000a06a_e.zip" -d files
	unzip "files/boundary files/gua_000a06a_e.zip" -d files
	touch .completed/003_UNZIP_FILES

# a convenient shorthand in the next install step
POSTGIS_SQL_PATH = /usr/share/postgresql/9.1/contrib/postgis-1.5/
.completed/004_SETUP_DB: .completed/003_UNZIP_FILES .completed/002_INSTALL_SOFTWARE
	### Creating the Postgresql user and database
	sudo -u postgres createdb postgis-template
	sudo -u postgres createlang plpgsql postgis-template
	sudo -u postgres psql -d postgis-template -f $(POSTGIS_SQL_PATH)/postgis.sql
	sudo -u postgres psql -d postgis-template -f $(POSTGIS_SQL_PATH)/spatial_ref_sys.sql
	sudo -u postgres createdb -T postgis-template canada
	touch .completed/004_SETUP_DB

# Notes about projection info
# The Canadian data appears to be EPSG: 4269
# Google uses EPSG:3857
SHP2PGSQL = /usr/lib/postgresql/9.1/bin/shp2pgsql
DBIN = sudo -u postgresl psql
.completed/005_POPULATE_DB: .completed/004_SETUP_DB
	$(SHP2PGSQL) -s 4269 -I -W LATIN1 files/gccs000a06a_e.shp ccs | $(DBIN)
	$(SHP2PGSQL) -s 4269 -I -W LATIN1 files/gcd_000a06a_e.shp cd | $(DBIN)
	$(SHP2PGSQL) -s 4269 -I -W LATIN1 files/gcma000a06a_e.shp cma | $(DBIN)
	$(SHP2PGSQL) -s 4269 -I -W LATIN1 files/gcsd000a06a_e.shp csd | $(DBIN)
	$(SHP2PGSQL) -s 4269 -I -W LATIN1 files/gct_000a06a_e.shp ct | $(DBIN)
	$(SHP2PGSQL) -s 4269 -I -W LATIN1 files/gdpl000a06a_e.shp dpl | $(DBIN)
	$(SHP2PGSQL) -s 4269 -I -W LATIN1 files/gfed000a06a_e.shp fed | $(DBIN)
	$(SHP2PGSQL) -s 4269 -I -W LATIN1 files/gfsa000a06a_e.shp fsa | $(DBIN)
	$(SHP2PGSQL) -s 4269 -I -W LATIN1 files/gpr_000a06a_e.shp pr | $(DBIN)
	$(SHP2PGSQL) -s 4269 -I -W LATIN1 files/gua_000a06a_e.shp ua | $(DBIN)
	touch .completed/005_POPULATE_DB

# the webserver (and thus php) runs as user www-data, which needs access to the canada db
.completed/006_SET_USER_PERMS: 005_POPULATE_DB
	sudo -u postgres createuser -D -l -R -S www-data
	$(DBIN) -c 'GRANT SELECT ON ALL TABLES IN SCHEMA public TO "www-data";'
	touch .completed/006_SET_USER_PERMS


