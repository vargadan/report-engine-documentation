BUILD_CMD='mvn fabric8:deploy -DskipTests'
folders="identity-server dummy-report-factory report-order-manager report-renderer report-repository report-uploader"
for folder in $folders
do 
    ls -la ./$folder/pom.xml
done
for folder in $folders
do 
    $BUILD_CMD -f ./$folder/pom.xml
done