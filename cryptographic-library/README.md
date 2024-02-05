execute maven stuff:

mvn clean compile
maven install
export PATH=$PATH:/media/sf_KALI_FOLDER/g14_05_12/g14/target/appassembler/bin

run program:

protect json_files/test.json json_files/test2.json keys/clientPublic.pub keys/serverPrivate.key
check json_files/test2.json
unprotect json_files/test2.json json_files/test3.json keys/clientPrivate.key