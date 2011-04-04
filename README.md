# Community Maps

This is the HTML and JavaScript to create the community mapping demo hosted at [research.markmfredrickson.com](http://research.markmfredrickson.com).

## Local Development

You will need the following to run a local version for testing and development:

- A webserver (e.g. Apache, which ships with OS X)
- A `/etc/apache2/httpd.conf` vhost entry to host maps.local.markmfredrickson.com on your working copy. Here is mine:

      &lt;VirtualHost *:80&gt;
        ServerAdmin mark@localhost
        DocumentRoot /Users/mark/Sites/static/mapsmmf
        ServerName maps.local.markmfredrickson.com
        ErrorLog /private/var/log/apache2/error_log
      &lt;/VirtualHost&gt;
    
- An entry in your `/etc/hosts` file `127.0.0.1 maps.local.markmfredrickson.com`

With these in place, restart apache and point your browser to [maps.local.markmfredrickson.com](http://maps.local.markmfredrickson.com).
