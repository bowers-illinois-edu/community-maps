# A make file to make sure all supporting files are ready for deployment

# KML is not being generated properly, so commenting this out for now
# war/kml/ccs/6208098.kml: gis/create-kml.rb
# 	cd war && rm -rf kml
# 	cd war && ../gis/create-kml.rb

# lein2 is version 1.6.2 on my machine (for reasons unknown version 1.7 does not work)
appengine-prepare:
	cd app && lein2 appengine-prepare

deploy: appengine-prepare
	cd app && appcfg.sh update war

localdev: 
	cd app && cake kill
	cd app && cake swank

install-gis:
	make -C gis install
