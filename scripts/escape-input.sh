#!/usr/bin/env bash

# run in the both source and input folder to escape colons in property keys
find . -type f -name "*.properties" -exec sed -i 's/\:/\\:/g' {} +
