# Make sure to "install.packages('yaml')" first

library(yaml)

yaml2df <- function(file) {
  message("Starting process, this may take a while...")
  
  yamldata <- yaml.load_file(file)
  message("YAML data loaded...")

 headers <- Reduce(f = union, x = sapply(yamldata, names))
 message("Headers computed...")
 
 message("Transforming to data.frame...")
 tmp <- do.call(rbind.fill, lapply(yamldata, function(k) {
  nulls <- sapply(k, is.null)
  notnulls <- k[!nulls]
  data.frame(notnulls)
 }))
    
}
