rm *.cb*
rm -rf done tagged toTrash
cp source/*.cb* .
java -jar target/tagger-1.0-SNAPSHOT.jar *.cb*

