build:
	mvn clean install -P compose
	docker build -t thehuffs/dataservice:0.1.0-SNAPSHOT -f dataservice-app/Dockerfile dataservice-app

stop:
	docker compose down --remove-orphans

run: build stop
	docker compose down --remove-orphans
	docker compose run --service-ports -d mysql
	docker compose run --service-ports -d redis
	sleep 10
	docker compose run --service-ports -d dataservice

test: 
	cd dataservice-tests && mvn test
