# Contributor & Development Notes

## Build for local development

In order to build a development apk run:

```shell script
./gradlew buildRelease
```

Please do not run `./gradlew build` unless you have configured the signing options for release build as well.

## Signing for release

In order to build your own release you'll need to configure the following environmental variables:

Variable | Value 
--- | ---
KEYSTORE_FILE | Path containing the keystore for release app.
KEYALIAS | The key alias used for signing
MYAPP_RELEASE_STORE_PASSWORD | Keystore Password
MYAPP_RELEASE_KEY_PASSWORD | Key  password
  
If not `KEYSTORE_FILE` is set then the keystore is assumed to be the `.keys/h300s.keystore`. The `.keys` folder is a folder located int the project's root.

## Accepting your contribution

1. The least ammount of testing that project should have is for the classes inside the `./logic` folder.
2. Any changes to the code should be done via pull request.
3. Any change should also have jUnit unit tests as 1 explains.
4. Any issue or new feature should reside inside is own branch not named `master` or `dev`.

## File structure and architecture

The project should have thw following folders:

1. `logic` where core logic exists. 
2. `screens` where all activities reside
3. `exceptions` where android and android-specific exceptions reside.

Inside the `logic` contains any piece of code that is used for core logic. 
Any class that resides in it should not have any android-specific dependencies and api calls. 
Any class that resides inside this folder it *must* be unit tested, third party libraries must be provided via depencency injection. 
If dependency injection does not happen via constructor method it should be done via setter method.

## Versioning

Each apk is build using the build date, then its is releases in a tag generated from current date in the following format `YMDHMS` 
(`Υ` is the current year, `M` is the current month or the current minute, `D` is the current day, `H` is the current hour, `S` is the current second).

The apk version is in the following format `YMD` (`Υ` is the current year, `M` is the current month or the current minute, `D` is the current day, `H` is the current hour, `S` is the current second).