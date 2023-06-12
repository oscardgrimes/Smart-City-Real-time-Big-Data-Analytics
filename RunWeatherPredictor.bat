start /min cmd /c python c:/flask/app.py
cd .\BatchFiles
start /min cmd /c RunTopology.bat
timeout 60
start /min cmd /c RunProducer.bat
