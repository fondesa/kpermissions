KPermissions
===============
[![Build Status](https://travis-ci.org/Fondesa/KPermissions.svg?branch=master)](https://travis-ci.org/Fondesa/KPermissions)

An Android library totally written in Kotlin that helps to request runtime permissions.
This library is compatible also below Android M (API 23) where runtime permissions doesn't exist, so you haven't to handle them separately. 

Usage
------

To discover all the APIs of this library, check the [wiki](https://github.com/Fondesa/KPermissions/wiki). It contains some useful notes and advanced features not explained in the ```README```.
For further samples, check the [sample](https://github.com/Fondesa/KPermissions/tree/master/sample) provided by this library. It shows how to integrate this library and request the permissions from an Activity or a Fragment.

### Basic usage
You can create a ```PermissionRequest``` either from an ```Activity``` or a ```Fragment``` using the extension method ```permissionsBuilder()```:

```kotlin
// Create the request with the permissions you would like to request.
val request = permissionsBuilder(Manifest.permission.CAMERA, Manifest.permission.SEND_SMS).build()
// "this" must implement PermissionRequest.Listener.
request.addListener(this)
// OR
request.addListener { result ->
    // Handle the result, for example check if all the requested permissions are granted.
    if (result.allGranted()) {
       // All the permissions are granted.
    }
}
// Send the request when you want.
request.send() 
```

Compatibility
------

**Android SDK**: KPermissions requires a minimum API level of **14** (the same of the latest support libraries).

**AndroidX**: this library requires AndroidX. To use it in a project without AndroidX, refer to the version **1.x**

Integration
------

You can download a jar from GitHub's [releases page](https://github.com/Fondesa/KPermissions/releases) or grab it from ```jcenter()``` or ```mavenCentral()```.

### Gradle ###

```gradle
dependencies {
    compile 'com.github.fondesa:kpermissions:3.0.0'
}
```

### Maven ###

```xml
<dependency>
  <groupId>com.github.fondesa</groupId>
  <artifactId>kpermissions</artifactId>
  <version>3.0.0</version>
  <type>pom</type>
</dependency>
```

### Contributing ###
Feel free to contribute to this project following the [contributing guidelines](https://github.com/Fondesa/KPermissions/blob/master/.github/CONTRIBUTING.md).