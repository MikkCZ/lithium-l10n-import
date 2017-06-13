#!/usr/bin/env bash

# run in the input files folder to rename the input files
for file in *.properties; do j=`echo $file | cut -d . -f 2`;j=$j".properties";mv $file $j; done
