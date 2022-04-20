First, download the zip file of the repository after downloading the zip file, unzip that file in the location prefered. Open this folder in android studio. After opening the file, go to open the MainActivity.java file in the CSPRecorder located at CSPRecorder->app->src->main->java->com.example.csprecorder->MainActivity.java. After opening the MainActivity.java connect your mobile to the laptop through USB and allow debugging on your mobile(Option will get display on the mobile) as well as click on transfer files in mobile. After that, Press the run button in the android studio. It will run the file and install the given app in the mobile.
How to run app in mobile?
1) Allow all the permissions asked.
2) Go to settings  -> All apps -> csprecorder -> permissions-> storage -> select allow management of all files. Also, turn on GPS.
3) On clicking on record button, the recording will start and chunks of 10s of recording is done in every 10s. Between every recording there is a buffer time of 10s for the analysis part of previous recording.
4) The recording will only stop on clicking the STOP button.
5) The CSV file is created which is saved in storage/CSVFile/.
6) This CSV file consists of energy of that given recording.
