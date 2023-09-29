# Creation d'une paire de clés pour une application Scout

PRIVATE_KEY=$(openssl ecparam -name prime256v1 -genkey -text)
printf "################ Public Private Keypair ################"
printf "\nBase64 encoded privateKey: "
echo "$PRIVATE_KEY" | openssl ec -outform pem -no_public 2>/dev/null | openssl pkcs8 -topk8 -nocrypt -inform pem -outform der | base64 -w0
printf "\nBase64 encoded publicKey: "
echo "$PRIVATE_KEY" | openssl ec -outform der -pubout 2>/dev/null | base64 -w0
printf "\n"
