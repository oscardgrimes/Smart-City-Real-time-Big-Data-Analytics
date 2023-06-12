cd ..\
cd .\WeatherPredictionTopology\target
storm jar storm-kafka-topology-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.stormadvance.kafka.PredictionTopology PredictionTopology -c nimbus.host=RT01