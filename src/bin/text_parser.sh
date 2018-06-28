#!/bin/bash

help() {
  echo -e "Usage $0 -d [extractor directory] -o [output directory] -i [input log file]"
  echo -e " * The extractor directory must contain a file named 'fieldextraction.properties'"
  echo -e " Example: ./test_extractor.sh -d ~/project/src/sops-field-extraction-rules -o /tmp/out -i /tmp/input_log"
  echo

}

while getopts d:o:i:c:? flag; do
  case $flag in
    d)
      EXTRACTOR_DIR=$OPTARG
      ;;
    o)
      OUTPUT_DIR=$OPTARG
      ;;
    i)
      INPUT_FILE=$OPTARG
      ;;
    c)
      CLASSPATH=$OPTARG
      ;;
    *)
      help
      exit;
      ;;
  esac
done

if [[ $EXTRACTOR_DIR"x" == "x" || $OUTPUT_DIR"x" == "x" || $INPUT_FILE"x" == "x" ]]
then
  help
  exit 1
fi

EXTRACTOR_PROP_FILE=fieldextraction.properties
CLASSPATH=${CLASSPATH:-/opt/redlabnet/lib/sops-fieldextraction*.jar:/opt/redlabnet/lib/*}
EXT_CLASSPATH=${CLASSPATH}:${EXTRACTOR_DIR}

java -cp "${EXT_CLASSPATH}" com.secureops.fieldextraction.TextParser \
  -properties_file "${EXTRACTOR_DIR}/${EXTRACTOR_PROP_FILE}" \
  -output_dir "$OUTPUT_DIR" \
  -input_file "$INPUT_FILE"
exit $?
