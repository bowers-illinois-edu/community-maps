# A make file to make sure all supporting files are ready for deployment

# KML is not being generated properly, so commenting this out for now
# war/kml/ccs/6208098.kml: gis/create-kml.rb
# 	cd war && rm -rf kml
# 	cd war && ../gis/create-kml.rb

appengine-prepare:
	lein appengine-prepare

deploy: 
	lein appengine-prepare
	appcfg.sh update war

localdev: 
	cake kill
	cake swank

