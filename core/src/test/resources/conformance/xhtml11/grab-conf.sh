# CURRENTLY these tests aren't retrievable, so this doesn't work
# get directly from git.

echo "Not working yet"
exit 1

rm *.xhtml
rm *.sparql

MANIFEST='https://github.com/msporny/rdfa-test-suite/raw/master/xhtml11-manifest.rdf'

curl -S -s -O "$MANIFEST"

MANIFEST_FILE=$(basename "$MANIFEST")

for i in $(grep 'informationResourceInput' $MANIFEST_FILE | cut -d '"' -f 2)
do
	curl -S -s -O "$i"
done

for i in $(grep 'informationResourceResults' $MANIFEST_FILE | cut -d '"' -f 2)
do
	curl -S -s -O "$i"
done
