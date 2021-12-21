

croot=$(pwd)
appName="cloudarg"
cr="arche-docker.pkg.coding.net/arche.cloud/arche-cloud"
ver="devel"

docker stop $appName 

docker run --rm -d \
	--pull=always \
	-p 8089:8089 \
	--name $appName \
	$cr/$appName:$ver \
  java -jar /app/cloudarg.jar



#	--network host \