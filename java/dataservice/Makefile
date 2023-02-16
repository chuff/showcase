build:
	mvn clean install -P compose
	docker build -t thehuffs/dataservice:0.1.0-SNAPSHOT -f dataservice-app/Dockerfile dataservice-app

run: build
	docker compose down --remove-orphans
	docker compose run --service-ports -d mysql
	sleep 10
	docker compose run --service-ports -d dataservice

test: 
	cd dataservice-tests && mvn test
