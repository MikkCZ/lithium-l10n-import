#!/usr/bin/env bash

# run both for the input and output to unescape colons, equal signs and exclamation marks
find . -type f -name "*.properties" -exec sed -i 's/\\:/\:/g' {} +
find . -type f -name "*.properties" -exec sed -i 's/\\=/\=/g' {} +
find . -type f -name "*.properties" -exec sed -i 's/\\!/\!/g' {} +
