#!/bin/bash

path=.
rm -r "$path"/png/
mkdir "$path"/png/

ls "$path"/dot | while read filename
do
	funcID=$(echo ${filename%.*})
		echo $funcID
		dot -T png -Gdpi=48 "$path"/dot/$funcID.dot -o "$path"/png/$funcID.png
done
