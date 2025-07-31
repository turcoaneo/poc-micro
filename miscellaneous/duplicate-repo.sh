#!/bin/bash

# Check input arguments
if [ "$#" -ne 2 ]; then
  echo "Usage: $0 <original_git_repo_url> <cloned_git_repo_url>"
  exit 1
fi

ORIGINAL_REPO=$1
CLONED_REPO=$2
TEMP_DIR="repo-mirror-temp"

echo "Cloning original repo: $ORIGINAL_REPO"
git clone --bare "$ORIGINAL_REPO" "$TEMP_DIR"

cd "$TEMP_DIR" || { echo "❌ Failed to enter temp directory"; exit 1; }

echo "Pushing to new repo: $CLONED_REPO"
git push --mirror "$CLONED_REPO"

cd ..
rm -rf "$TEMP_DIR"

echo "Repository successfully duplicated!"

#chmod +x duplicate-repo.sh
#./duplicate-repo.sh https://github.com/turcoaneo/poc-server-config https://github.com/turcoaneo/poc-config-all