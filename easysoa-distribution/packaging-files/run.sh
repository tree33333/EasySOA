#!/bin/bash

clear

mkdir -p log
export PATH="$PATH:$PWD/node"

# Start functions

serviceregistry()
{
  touch log/serviceRegistry.log
  ./serviceRegistry/bin/nuxeoctl console > log/serviceRegistry.log 2>&1
}

esperproxy()
{
  touch log/esperProxy.log
  cd frascati
  ./start-esperProxy.sh > ../log/esperProxy.log 2>&1
}

web()
{
  touch log/web.log
  cd web
  chmod +x ./start-web.sh
  ./start-web.sh > ../log/web.log 2>&1
}

pafservices()
{
  touch log/pafServices.log
  cd pafServices
  ./start-pafServices.sh > ../log/pafServices.log 2>&1
}

travelbackup()
{
  touch log/travelBackup.log
  cd travelBackup
  ./start-travelBackup.sh > ../log/travelBackup.log 2>&1
}

traveldemo()
{
  touch log/travelDemo.log
  cd frascati
  ./start-travelDemo.sh > ../log/travelDemo.log 2>&1
}

uiscaffolder()
{
  touch log/uiScaffolder.log
  cd frascati
  ./start-uiScaffolder.sh > ../log/uiScaffolder.log 2>&1
}

airportservice()
{
  touch log/airportService.log
  cd talend/airportService/SimpleProvider
  ./SimpleProvider_run.sh > ../../../log/airportService.log 2>&1
}

startupmonitor()
{
  cd startupMonitor
  ./start-startupMonitor.sh
}

# Start processes
echo "Starting the EasySOA Demo. Press any key to stop."
echo ""

# Prepare to interrupt all running processes
shutdown()
{
  echo "Stopping all servers."
  ps | awk 'NR>2 { print $1 }' | xargs kill -9 > /dev/null 2>&1
}

trap shutdown SIGINT SIGTERM

# FIXME The script uses delays to solve dependencies issues,
# it might not be enough on lower-end computers

startupmonitor &
serviceregistry &
esperproxy &
pafservices &
travelbackup &
sleep 3 # Let the servers start
traveldemo &
airportservice &
sleep 7 # Let the demo start
web &
uiscaffolder &

# Wait for a key to be pressed
read -n 1 -s
shutdown

