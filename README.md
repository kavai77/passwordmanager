# passwordmanager

[![Build Status](https://travis-ci.org/kavai77/passwordmanager.svg?branch=master)](https://travis-ci.org/kavai77/passwordmanager)

This project is an open source cryptography based password manager.

It uses symmetric key encryption for (AES) for stroring passwords
and Password-Based-Key-Derivation-Function (PBKDF2) for deriving
the symmetric key from the Master Password.

The master password needs to be entered every time in order to be able
the decrypt the encrypted passwords stored in the database.
 
Since it does not store the master password in any persistent storage,
the project could not offer a password reminder feature. If the master password
is lost, there is no way to decode the encrypted password.
However, Passwordmanager does offer a backup function and performs
it before password change. So if the new master password is forgotten,
we can restore the old encrypted data, so it can be decrypted with the old
master password.

Please note that this project is a Google Cloud Platform based application.
We use the following libraries from GCP:
* Google Users API
* Datastore

## Building
### Test
```
mvn test
```
### Creating the deployable artifact (WAR)
```
mvn package
```
### Deploying to Google Cloud Platform
```
mvn com.google.appengine:appengine-maven-plugin:update
```
*Note: for this step, you do not need to run the previous steps, testing and creating the WAR are performed as automatically.*
### Running local server
```
mvn com.google.appengine:appengine-maven-plugin:devserver
```

## Architecture
### Retrieving and decoding passwords
![Retrieving and decoding passwords](https://github.com/kavai77/passwordmanager/blob/master/src/main/webapp/image/password-retrieval.svg)

### Stroring passwords
![Stroring passwords](https://github.com/kavai77/passwordmanager/blob/master/src/main/webapp/image/password-storage.svg)
