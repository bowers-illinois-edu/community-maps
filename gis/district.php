<?php

$pgsql_conn = pg_connect("dbname=canada");

if(!$pgsql_conn) {
	print pg_last_error($pgsql_conn);
	exit;
}

// $res = pg_fetch_all(pg_query($pgsql_conn, "SELECT count(*) FROM ccs"));
// print_r($res);

/* 
  Outline of Script
  - Get lat, lon, and table from query params of same name
  - Verify that the table is a valid option
  - Make sure lat and lon are ints (atoi, probably)
  - Generate a select query, where the the lat and lon are converted from 3857
    to 4269 (Google's coordinate system to the one used in the Canada files)
  - parse out district ID and return that as JSON (or perhaps just a text file
    with a single number in it? JSON would leave room for additional
    information).
*/

$tables_to_ids = array('pr'  => 'pruid',
		       'ccs' => 'ccsuid',
		       'cma' => 'cmauid',
		       'csd' => 'csduid',
		       'ct'  => 'ctuid',
		       'dpl' => 'dpluid', 
		       'fed' => 'feduid',
		       'fsa' => 'census_fsa',
		       'cd'  => 'cduid',
		       'ua'  => 'uapuid');

$lat = $_GET["lat"];
$lon = $_GET["lon"];
$table = $_GET["table"];

if (!$tables_to_ids[$table]) {
  print("Unknown table name");
  exit;
}

if (!is_numeric($lat) || !is_numeric($lon)) {
  print("Lat and lon query parameters must be numeric");
  exit;
}

### Do the look up
# For testing purposes Toronto is 43.652527,-79.381961
$id = $tables_to_ids[$table];
$res = pg_fetch_all(pg_query("SELECT $id FROM $table WHERE
intersects(the_geom, Transform(GeomFromEWKT('SRID=4326;POINT($lon $lat)'),
4269));"));

if (count($res) != 1) {
	print(0);
} else {
	print($res[0][$id]);
}

pg_close($pgsql_conn);
?>
