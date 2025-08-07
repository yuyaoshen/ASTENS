#!/bin/bash

path=./
rm -r "$path"/tf/
mkdir "$path"/tf/

ls "$path"/dataset | while read filename
do
	echo "$filename"
	cat "$path"/dataset/"$filename" | awk '{print $5}' >> "$path"/tf/nodeTokens.txt
	cat "$path"/dataset/"$filename" | awk '{print $1" "$2}' >> "$path"/tf/nodeTypes.txt
done

ls "$path"/dataset | while read filename
do
	echo "$filename"
	cat "$path"/dataset/"$filename" | sed "s/ /\n/g" >> "$path"/tf/terms.txt
	cat "$path"/dataset/"$filename" >> "$path"/tf/sentences.txt
done

sort "$path"/tf/nodeTokens.txt | uniq > "$path"/tf/nodeTokens.uniq.txt
sed '/^\s*$/d' "$path"/tf/nodeTokens.uniq.txt > "$path"/tf/nodeTokens.txt
rm "$path"/tf/nodeTokens.uniq.txt
sed -i 's/.\{0\}//' "$path"/tf/nodeTypes.txt
sort "$path"/tf/nodeTypes.txt | uniq -c | sort -nr | awk '{print $1" "$2" "$3}' > "$path"/tf/nodeTypes-w_sc.uniq.txt
sed -i 's/.\{4\}//' "$path"/tf/nodeTypes.txt
sort "$path"/tf/nodeTypes.txt | uniq -c | sort -nr | awk '{print $1" "$2" "$3}' > "$path"/tf/nodeTypes-wo_sc.uniq.txt
rm "$path"/tf/nodeTypes.txt
sort "$path"/tf/terms.txt | uniq > "$path"/tf/terms.uniq.txt
sed '/^\s*$/d' "$path"/tf/terms.uniq.txt > "$path"/tf/terms.txt
rm "$path"/tf/terms.uniq.txt
sort "$path"/tf/sentences.txt | uniq > "$path"/tf/sentences.uniq.txt
sed '/^\s*$/d' "$path"/tf/sentences.uniq.txt > "$path"/tf/sentences.txt
rm "$path"/tf/sentences.uniq.txt
