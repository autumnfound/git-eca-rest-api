#!/bin/bash

read in;
OLD_HEAD="$(echo $in | cut -d" " -f1)"
NEW_HEAD="$(echo $in | cut -d" " -f2)"
GIT_LOG="$(git rev-list --first-parent $OLD_HEAD..$NEW_HEAD)";
COMMIT_HASHES=($GIT_LOG);
COMMITS=( )
for i in "${COMMIT_HASHES[@]}"
do
	COMMITS=("$(git show -s --format='{"author": {"name":"%an","mail":"%ae"},"commiter":{"name":"%cn","mail":"%ce"},"body":"%B","subject":"%s","hash":"%H", "parents":"%P"}' $i)" "${COMMITS[@]}");
done
body=$(printf ",%s" "${COMMITS[@]}" | tr "\\n" "\n");
body=${body:1};
POST_COMMAND="curl http://192.168.1.178:8080/git/eca --data '{\"project_id\":\"1\",\"commits\":[$body]}' -H \"Content-Type:application/json\"";
echo $POST_COMMAND;
$("$POST_COMMAND");
exit 1
