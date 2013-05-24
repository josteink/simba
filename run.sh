#!/usr/bin/env bash

# this file is included so that we can have the process run by chron.

# set current dir to script-dir
cd $( dirname "${BASH_SOURCE[0]}" )

# process
lein run
