# publishing a new version
mvn clean versions:set -U -Prelease-deployment -DneVersion=0.1.0
mvn deploy -Prelease-deployment