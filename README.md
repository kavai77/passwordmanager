# passwordmanager

[![CICD](https://github.com/kavai77/passwordmanager/actions/workflows/cicd.yaml/badge.svg)](https://github.com/kavai77/passwordmanager/actions/workflows/cicd.yaml)

This project is an open source cryptography based password manager.

It uses symmetric key encryption for (AES) for storing passwords
and Password-Based-Key-Derivation-Function (PBKDF2) for deriving
the symmetric key from the Master Password.

The master password needs to be entered every time in order to be able
the decrypt the encrypted passwords stored in the database.
 
Since it does not store the master password in any persistent storage,
the project could not offer a password reminder feature. If the master password
is lost, there is no way to decode the encrypted password.
However, PasswordManager does offer a backup function and performs
it before password change. So if the new master password is forgotten,
we can restore the old encrypted data, so it can be decrypted with the old
master password.

Please note that this project is a Google Cloud Platform based application.
We use the following libraries from GCP:
* Google Users API
* Datastore

## Building & Running
```
mvn package
java -jar ./target/passwordmanager-1.0.jar
```
## Docker
Build image:
```
mvn package
docker build -t passwordmanager .
```
Run on `localhost:8080` (assuming you have the necessary Google Credentials in `~/project/secrets`):
```
docker run -v ~/project/secrets:/usr/local/secrets -p 8080:8080 passwordmanager
```

## Architecture
### Retrieving and decoding passwords
![Retrieving and decoding passwords](https://raw.githubusercontent.com/kavai77/passwordmanager/main/src/main/resources/static/image/password-retrieval.svg)

### Storing passwords
![Stroring passwords](https://raw.githubusercontent.com/kavai77/passwordmanager/main/src/main/resources/static/image/password-storage.svg)
