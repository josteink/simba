#!/usr/bin/env bash

# invocation: (sh patcher-tool source-folder file)

SOURCE_DIR=$1
SOURCE=$2
TARGET=`echo $SOURCE | perl -pe "s/(.*)\.zip/\1-tabletUIpatch.zip/" `

echo "Copying target $SOURCE from $SOURCE_DIR..."
cp $SOURCE_DIR/$SOURCE . || exit 1
#cp $SOURCE_DIR/$SOURCE.md5sum . || exit 1

echo "Verifying MD5 sum..."

# UGLY hack. strip out path from md5
sed -E "s/\s(\/[a-z0-9\.]+)+\/.+\.zip/ $SOURCE/g" $SOURCE_DIR/$SOURCE.md5sum >$SOURCE.md5sum || exit 1

md5sum -c $SOURCE.md5sum || exit 1


echo "Creating patch $TARGET from $SOURCE."

./auto_patcher $SOURCE tabletUI cm || exit 1

echo "Success! Cleaning up..."

rm -f restore*
#  update-cm-tf101-20130424-tabletUI.zip
TMP=update-cm-*
mv $TMP $TARGET
md5sum $TARGET >$TARGET.md5sum

cp $TARGET $SOURCE_DIR
cp $TARGET.md5sum $SOURCE_DIR

rm $SOURCE
