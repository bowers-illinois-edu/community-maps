<?php

$pgsql_conn = pg_connect("dbname=canada");

if(!$pgsql_conn) {
	print pg_last_error($pgsql_conn);
	exit;
}

$res = pg_fetch_all(pg_query($pgsql_conn, "SELECT count(*) FROM ccs"));
print_r($res);


pg_close($pgsql_conn);
?>
