#!/usr/bin/ruby -w

require 'rubygems'
require 'pg'

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
SIMPLIFY = [5, 4, 3,  2, 1.5, 1] + (1..20).map {|q| 1.0/(2 ** q)}
MIN_POINTS = 25
MAX_POINTS = 100
TABLES = ['pr', 'ccs', 'cma', 'csd', 'ct', 'dpl', 'fed', 'fsa', 'cd', 'ua']
PRECISION = 8 # how many digits past decimal point to include in KML file

# DB connection
USER = 'postgres'
DB = 'canada'
db = PG.connect(:dbname => DB, :user => USER)

if (!FileTest::directory?("kml"))
  Dir::mkdir("kml")
end

TABLES.map {|t|
  if (!FileTest::directory?("kml/" + t))
    Dir::mkdir("kml/" + t)
  end

  # NOTE: the tables have an id called TABLENAMEuid, with the exception of fsa and ua
  # There the ids are census_fsa and uapid
  id = t + "uid"
  if (t == 'fsa')
    id = 'census_fsa'
  elsif (t == 'ua')
    id = 'uapuid'
  end

  # for each table I create a temporary table to hold the best
  # simplification value for each geometry. We are trying to make
  # geometries with between MAX_POINTS and MIN_POINTS

  db.exec("CREATE TEMPORARY TABLE #{t}simp AS (SELECT #{id}, 0.0 as s FROM #{t})")

  SIMPLIFY.map do |s| 
    db.exec("INSERT INTO #{t}simp SELECT #{id}, #{s} FROM
               (SELECT #{id}, st_npoints(simplify(the_geom, #{s})) as n FROM #{t}) as tmp
                 WHERE n < #{MAX_POINTS} and n > #{MIN_POINTS};")
  end
  
  qstr = "SELECT #{t}.#{id}, asKML(simplify(the_geom, s), #{PRECISION}) FROM 
          (SELECT #{id}, max(s) as s FROM #{t}simp GROUP BY #{id}) AS tmp
          LEFT JOIN #{t} ON tmp.#{id} = #{t}.#{id};"

  db.exec(qstr).each {|q|
    kid = q[id]
    kml = q["askml"]
    File.open("kml/#{t}/#{kid}.kml", 'w') {|f|
      tmp = <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://www.opengis.net/kml/2.2">
<Document>
<Placemark>
#{kml}
<Style>
 <PolyStyle>
  <color>#aa000000</color>
  <outline>0</outline>
 </PolyStyle>
</Style>
</Placemark>
</Document>
</kml>
EOF
    f.write(tmp)
    puts "wrote #{kid}\n"
  }
 }
  
}

