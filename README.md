KPermissions
===============
[![Build Status](https://travis-ci.org/fondesa/KPermissions.svg?branch=master)](https://travis-ci.org/fondesa/KPermissions)

An Android library totally written in Kotlin that helps to request runtime permissions.
This library is compatible also below Android M (API 23) where runtime permissions doesn't exist, so you haven't to handle them separately. 

Usage
------

To discover all the APIs of this library, check the [wiki](https://github.com/fondesa/KPermissions/wiki). It contains some useful notes and advanced features not explained in the ```README```.
For further samples, check the [sample](https://github.com/fondesa/KPermissions/tree/master/sample) provided by this library. It shows how to integrate this library and request the permissions from an Activity or a Fragment.

### Basic usage
You can create a ```PermissionRequest``` either from an ```Activity``` or a ```Fragment``` using the extension method ```permissionsBuilder()```:

```kotlin
// Build the request with the permissions you would like to request and send it.
permissionsBuilder(Manifest.permission.CAMERA, Manifest.permission.SEND_SMS).build().send { result ->
    // Handle the result, for example check if all the requested permissions are granted.
    if (result.allGranted()) {
       // All the permissions are granted.
    }
}
```

#### Coroutines
The artifact `kpermissions-coroutines` adds the integration with the Kotlin coroutines:
```kotlin
launch {
    val result = permissionsBuilder(Manifest.permission.CAMERA).build().sendSuspend()
    // Handle the result.
}
```

It also supports the Kotlin coroutines `Flow` API:
```kotlin
val request = permissionsBuilder(Manifest.permission.CAMERA).build()
launch {
    request.flow().collect { result ->
        // Handle the result. 
    }
}
request.send()
```

#### RxJava
The artifacts `kpermissions-rx2` and `kpermissions-rx3` adds the integration with RxJava 2 and RxJava 3 respectively:
```kotlin
val request = permissionsBuilder(Manifest.permission.CAMERA).build()
request.observe().subscribe { result ->
    // Handle the result.
}
request.send()
```

#### LiveData
In the core artifact, there's a useful extension on `PermissionRequest` to get a `LiveData`:
```kotlin
val request = permissionsBuilder(Manifest.permission.CAMERA).build()
request.liveData().observe(this) { result ->
    // Handle the result.
}
request.send()
```

Compatibility
------

**Android SDK**: KPermissions requires a minimum API level of **14** (the same of the latest support libraries).

**AndroidX**: this library requires AndroidX. To use it in a project without AndroidX, refer to the version **1.x**

Integration
------

You can download a jar from GitHub's [releases page](https://github.com/fondesa/KPermissions/releases) or grab it from ```jcenter()``` or ```mavenCentral()```.

### Gradle ###

```gradle
dependencies {
    // The core artifact.
    implementation 'com.github.fondesa:kpermissions:3.1.0'
    // If you want the extensions for RxJava 2.
    implementation 'com.github.fondesa:kpermissions-rx2:3.1.0'
    // If you want the extensions for RxJava 3.
    implementation 'com.github.fondesa:kpermissions-rx3:3.1.0'
    // If you want the extensions for the Kotlin coroutines.
    implementation 'com.github.fondesa:kpermissions-coroutines:3.1.0'
}
```

### Contributing ###
Feel free to contribute to this project following the [contributing guidelines](https://github.com/fondesa/KPermissions/blob/master/.github/CONTRIBUTING.md).