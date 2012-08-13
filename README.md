# Community Maps: The Followup

This is the source for the followup for the Canada community mapping survey.

This survery/experiment targets respondents in Canada, soliciting
information on subjective communities via online map creation and asks
questions about respondent attitudes on racial issues both within the
subjective communities and official tabulation areas.

## Survey

All of the survey logic and text is in the `app/` directory.

Editing text (i.e. "things inside quotation marks") can easily be
accomplished by navigating the repository to the `screens`
directory, selecting a screen file, and clicking on the "edit this
page" link at the top of the page.

You can also checkout the repository using git. This is required if
you wish to push an update to the app server. Before doing so, you
will need to install [cake](https://github.com/flatland/cake) and
[lein](https://github.com/technomancy/leiningen) as described on the
respective home pages. You will also need to install the (Google App
Engine SDK (for
Java))[http://code.google.com/appengine/downloads.html#Google_App_Engine_SDK_for_Java]
and put the `appcfg.sh` script in your `$PATH`. 

The `Makefile` includes two convenient tasks for local development:

- `localdev`: launches the `swank` server (Emacs + Clojure
  integration)
- `deploy`: pushes the code to Google's server. *NOTE*: before using
  this task, it is strongly recommended that you increase the version
  number in the `war/WEB-INF/appengine-web.xml` file. This wil allow
  us to rollback to the previous version if there is an error in this
  deployment.


Note: We are using somewhat older versions of lein and cake (before they were combined). So, to install lein, one could follow the advice here (http://stackoverflow.com/questions/3987683/homebrew-install-specific-version-of-formula)

```
cd /usr/local
brew versions leiningen
git checkout [whatever the 1.6.2 line in the output says]
brew install leiningen
```

Then, since the Makefile refers to lein2 and not lein, one should make a symlink between lein and lein2 (they are the same, as far as I can tell).

To install the appengine dev kit, one must have stuff on the path. So, one way to do this is to put it all in /usr/local/bin/appengine-java-sdk; make symlinks to the .sh files in the appengine-java-sdk/bin direction to /usr/local/bin.


To use swank effectively with emacs, you'll need to install clojure-mode for emacs.

### GIS development

The `gis` directory contains all the build scripts to generate the Postgis based district look up server and the static KML files used for display respondent districts. These tasks are designed to run on a stock Ubuntu 11.10 installation, such as the Amazon EC2 platform, though they can also run in a virtual machine.

The `gis` directory contains its own `Makefile` that runs the installation of
software, setting up the database, and generating the KML files. Running `make
install-gis` in the root directory will in turn run `make install` in the
`gis` directory. Early in the build script, you will need to supply the
password for the GIS files. Contact Jake or Mark to get it.

# Testing

Currently, you can test the deployed version of this follow up survey using: http://followup.mappingcommunities.ca/?pid=290012&tags[]=ignore




