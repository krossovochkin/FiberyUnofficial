VERSION_CODE=$(grep "android-version-code = *" gradle/libs.versions.toml | sed 's/[^0-9]//g' | awk '{ sum = $1 + 1 }; END { print sum }')
sed -i "s/android-version-code = \"[0-9]*\"/android-version-code = \"$VERSION_CODE\"/" gradle/libs.versions.toml
sed -i "s/android-version-name = \"[0-9]*\.[0-9]*\.[0-9]*\"/android-version-name = \"$1\"/" gradle/libs.versions.toml
