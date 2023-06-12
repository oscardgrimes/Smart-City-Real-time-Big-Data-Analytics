::Runs all required services for real
::time weather predictions

::Remove logs
RmDir C:\storm\var\storm\supervisor\stormdist /s /q
RmDir C:\kafka\kafka-logs /s /q
RmDir C:\zookeeper\zookeeper-logs /s /q
start /min cmd /c zkServer
::Wait 30 sec to allow zk to run
timeout 30
::Change dir to to RunKafka.bat dir 
::Run Kafka,Cassandra,Storm Nimbus
cd ./BatchFiles
start /min cmd /c RunKafka.bat
start /min cmd /c cassandra
start /min cmd /c storm nimbus
::Wait 30 sec to allow nimbus to run
timeout 30
::Run Storm Supervisor
start /min cmd /c storm supervisor
::Wait 30 sec to allow supervisor to run
timeout 30
::Run storm ui
start /min cmd /c storm ui


