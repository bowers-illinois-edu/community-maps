#!/usr/bin/ruby -w

require 'rubygems'
require 'postgres'

# example query
# select asKML(ST_simplifypreservetopology(the_geom,0.001)) from gccs000a06a_e limit 1; 
#
# Tables:
# ccs 
# cd 
# cma 
# csd 
# ct
# dpl 
# fed 
# fsa 
# pr
# ua

# Some constants for use in the data -> .kml process
SIMPLIFY = 0.001 # how much should the geomtries be simplified before ouput
TABLES = ['pr'] #, 'ccs', 'cma', 'csd', 'ct', 'dpl', 'fed', 'fsa', 'cd', 'ua']
# NOTE: the tables all have an id called TABLENAMEuid, with the exception of fsa and ua
# There the ids are census_fsa and uapid

def select(table)
  id = table + "uid"
  if (table == 'fsa')
    id = 'census_fsa'
  elsif (table == 'ua')
    id = 'uapuid'
  end

  "SELECT #{id}, asKML(ST_SimplifyPreserveTopology(ST_transform(the_geom, 3857), #{SIMPLIFY})) FROM #{table}"
end

# DB connection
USER = 'mark'
DB = 'canada'
db = PGconn.connect(:dbname => DB, :user => USER)

if (!FileTest::directory?("kml"))
  Dir::mkdir("kml")
end

TABLES.map {|t|
  if (!FileTest::directory?("kml/" + t))
    Dir::mkdir("kml/" + t)
  end

 db.query(select(t)).map {|q|
  id = q[0]
  kml = q[1]
  File.open("kml/#{t}/#{id}.kml", 'w') {|f|
    tmp = <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://www.opengis.net/kml/2.2">
<Document>
<Placemark>
#{kml}
<Style>
 <PolyStyle>
  <color>#AF0000aa</color>
  <outline>0</outline>
 </PolyStyle>
</Style>
</Placemark>
</Document>
</kml>
EOF
    f.write(tmp)
  }
 }
  
}

