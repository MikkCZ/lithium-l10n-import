#!/usr/bin/env bash

# run for output to unescape colons, equal signs, exclamation marks and tabulators
find . -type f -name "*.properties" -exec sed -i 's/\\:/\:/g' {} +
find . -type f -name "*.properties" -exec sed -i 's/\\=/\=/g' {} +
find . -type f -name "*.properties" -exec sed -i 's/\\!/\!/g' {} +
find . -type f -name "*.properties" -exec sed -i 's/\\t/\t/g' {} +

# escape colons in .properties keys
./escape-input.sh
