#! /bin/bash

# Clear screen
clear

# display start time
start_time=$(date)

echo
echo -e "\t\t\e[1;42mBuild started at: $start_time\e[0m"

# keep this file in Project root directory

echo -e '\e[1;31m==============================================================\e[0m'
echo -e '\t\t\e[1;42mRunning assembleDebug\e[0m'
echo -e '\e[1;31m==============================================================\e[0m'
./gradlew --no-rebuild assembleDebug

retval=$?


if [ $retval -eq 0 ]; then
	echo -e '\t\t\e[1;42mBuild Created : Debug\e[0m'
	echo $1;

	if [ ! -z $1 ]; then
		fresh_install=$1
	else
		echo -n "Install fresh build (y/n, any other key for no) > "
		read -n1 -t 5 fresh_install
	fi

	echo
	echo
	echo -e '\e[1;31m==============================================================\e[0m'
	echo -e '\t\t\e[1;42mWaiting for device to come online\e[0m'
	adb wait-for-device
	echo -e '\e[1;31m==============================================================\e[0m'
	echo -e '\t\t\e[1;42mList of devices\e[0m'
	echo -e '\e[1;31m==============================================================\e[0m'
	adb devices
	echo -e '\e[1;31m==============================================================\e[0m'
	if [ ! -z $fresh_install ] && [ $fresh_install = "y" ]; then
		echo -e '\e[1;42mUninstalling previous build\e[0m'
		adb uninstall in.buzzzz
	fi

	cd app/build/outputs/apk/
	echo -e '\e[1;42minstalling QA New Debug Debug\e[0m'
	adb install -r app-debug.apk
	
	echo -e '\e[1;42mStarting Debug\e[0m'
	
	if [ $? -eq 0 ]; then
		adb shell am start -n "in.buzzzz/in.buzzzz.activity.SplashActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
	else
		echo -e '\e[1;42merror while starting \e[0m'
	fi
elif [ $retval -eq 130 ]; then
	echo -e '\e[1;42mBuild process stopped\e[0m'
else
	echo -e '\e[1;42merror while building project\e[0m'
fi

# display end time
end_time=$(date)

echo
echo -e "\t\t\e[1;42mBuild ended at: $end_time\e[0m"
