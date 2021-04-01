#!/bin/bash

scriptDir="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
projectDir=${scriptDir}/..

if [ -z ${ROBOLECTRIC_MIN_API+x} ]; then
  echo "The variable ROBOLECTRIC_MIN_API must be defined."
  exit 1
fi

if [ -z ${ROBOLECTRIC_MAX_API+x} ]; then
  echo "The variable ROBOLECTRIC_MAX_API must be defined."
  exit 1
fi

[[ $ROBOLECTRIC_MIN_API =~ ^-?[0-9]+$ ]] || {
  echo "The variable ROBOLECTRIC_MIN_API must be an integer."
  exit 1
}
[[ $ROBOLECTRIC_MAX_API =~ ^-?[0-9]+$ ]] || {
  echo "The variable ROBOLECTRIC_MAX_API must be an integer."
  exit 1
}

[[ "$ROBOLECTRIC_MIN_API" -ge 16 && "$ROBOLECTRIC_MAX_API" -le 28 && "$ROBOLECTRIC_MIN_API" -le "$ROBOLECTRIC_MAX_API" ]] || {
  echo "The Robolectric API must be in range 16..28 and ROBOLECTRIC_MIN_API must be less than or equal to ROBOLECTRIC_MAX_API."
  exit 1
}

robolectricConfigFile=$projectDir/kpermissions/src/test/resources/robolectric.properties
# Writes the API versions which should be used for Robolectric tests.
echo "minSdk=$ROBOLECTRIC_MIN_API" >"$robolectricConfigFile"
echo "maxSdk=$ROBOLECTRIC_MAX_API" >>"$robolectricConfigFile"
# Runs the tests with the given API.
"$projectDir"/gradlew :kpermissions:testReleaseUnitTest -s
