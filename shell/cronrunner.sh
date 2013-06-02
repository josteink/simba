#!/usr/bin/env bash

echo "Starting SIMBA cron job..."

# this file is included so that we can have the process run by cron.

# set current dir to project root $ROOT/shell/../
cd $( dirname "${BASH_SOURCE[0]}" ) 
cd ..

echo "Working directory set..."

# process
$HOME/bin/lein run

echo "SIMBA completed."
