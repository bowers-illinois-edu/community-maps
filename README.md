# Community Maps

This survery/experiment targets respondents in Canada, soliciting
information on subjective communities via online map creation and asks
questions about respondent attitudes on racial issues both within the
subjective communities and official tabulation areas.

## Development

Editing text (i.e. "things inside quotation marks") can easily be
accomplished by navigating the repository to the `screens`
directory, selecting a screen file, and clicking on the "edit this
page" link at the top of the page.

You can also checkout the repository using git. This is required if
you wish to push an update to the app server. Before doing so, you
will need to install [cake](https://github.com/flatland/cake) and
[lein](https://github.com/technomancy/leiningen) as described on the
respective home pages.

The `Makefile` includes two convenient tasks for local development:

- `localdev`: launches the `swank` server (Emacs + Clojure
  integration)
- `deploy`: pushes the code to Google's server. *NOTE*: before using
  this task, it is strongly recommended that you increase the version
  number in the `war/WEB-INF/appengine-web.xml` file. This wil allow
  us to rollback to the previous version if there is an error in this
  deployment.

### GIS development

For the moment, generation of KML files from source GIS documents is
not working, so the GIS aspects of development are not required
working with the survey. In the future, local development may require
a Postgresql/PostGIS installation (though hopefully, I can split it up
so that you only need PostGIS if you want to monkey with the file
generation --- precompiled versions will be available via `git` or
`rsync`).

