


croot=$(pwd)
appName="cloudarg"
cr="arche-docker.pkg.coding.net/arche.cloud/arche-cloud"
ver="devel"


echo "Build docker image for Dev of latest..."
mvn clean package
docker build -t $cr/$appName:$ver .
docker push $cr/$appName:$ver

