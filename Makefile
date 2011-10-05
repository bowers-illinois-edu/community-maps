# A make file to make sure all supporting files are ready for deployment

war/kml/ccs/6208098.kml: gis/create-kml.rb
	cd war
	rm -rf kml
	../gis/create-kml.rb

appengine-prepare:
	lein appengine-prepare

deploy: war/kml/ccs/6208098.kml
	lein appengine-prepare
	appcfg.sh update war

localdev: war/kml/ccs/6208098.kml
	cake kill
	cake swank

