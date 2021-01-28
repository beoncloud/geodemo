FROM openjdk:11
 
# copy the packaged jar file into our docker image
COPY target/geolocation-0.1.0.jar /geolocation-0.1.0.jar
COPY GeoLite2-Country-Blocks-IPv4.csv /GeoLite2-Country-Blocks-IPv4.csv
COPY GeoLite2-Country-Locations-en.csv /GeoLite2-Country-Locations-en.csv

# set the startup command to execute the jar
CMD ["java","-Dmmdb_ip=/GeoLite2-Country-Blocks-IPv4.csv","-Dmmdb_country=/GeoLite2-Country-Locations-en.csv","-jar","/geolocation-0.1.0.jar"]
