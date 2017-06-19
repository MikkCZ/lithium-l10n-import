#!/usr/bin/env bash

# escape colons in .properties file keys
function _escape_colons_in_keys {
    file=$1;
    grep -n ':' $file | while read -r line ; do
        k=$( echo "$line" | cut -d'=' -f1 | xargs );
        line_num=$( echo "$k" | cut -d':' -f1 );
        key=$( echo "$k" | cut -d':' -f2-9999 );
        if [[ $key == *':'* ]]; then
            newkey="$(sed s/\\:/\\\\\\\\:/g <<< $key)";
            sed -i $line_num"s/$key/$newkey/g" $file;
        fi
    done
}

export -f _escape_colons_in_keys;

# run in the both source and input folder to escape colons in property keys
find . -type f -name "*.properties" -exec bash -c '_escape_colons_in_keys "$0"' {} \;
