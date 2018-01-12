KPermissions
===============
[![Build Status](https://travis-ci.org/Fondesa/KPermissions.svg?branch=master)](https://travis-ci.org/Fondesa/KPermissions)

An Android library totally written in Kotlin that helps to request runtime permissions.
This library is compatible also below Android M where runtime permissions doesn't exist so you haven't to handle them separately. 

Usage
------

To discover all the APIs of this library, check the [wiki](https://github.com/Fondesa/KPermissions/wiki). It contains some useful notes and advanced features not explained in the ```README```.

### Basic usage
You can create a ```PermissionRequest``` either from an ```Activity``` or a ```Fragment``` using the extension method ```permissionsBuilder()```:

```kotlin
// Creates the request with the permissions you would like to request.
val request = permissionsBuilder(Manifest.permission.CAMERA, Manifest.permission.SEND_SMS).build()
// Send the request when you want.
request.send() 
```

To be notified about permissions' events, you can attach some listeners in one of the following ways (you can also combine them in the way you prefer most).

**1. DSL**

```kotlin
request.listeners {
    
    onAccepted { permissions ->
        // Notified when the permissions are accepted.
    }

    onDenied { permissions ->
        // Notified when the permissions are denied.
    }
    
    onPermanentlyDenied { permissions ->
        // Notified when the permissions are permanently denied.
    }
    
    onShouldShowRationale { permissions, nonce ->
        // Notified when the permissions should show a rationale.
        // The nonce can be used to request the permissions again.
    }
}
```

**2. Builder's extensions**

```kotlin
request.onAccepted { permissions ->
    // Notified when the permissions are accepted.
}.onDenied { permissions ->
    // Notified when the permissions are denied.
}.onPermanentlyDenied { permissions ->
    // Notified when the permissions are permanently denied.
}.onShouldShowRationale { permissions, nonce ->
    // Notified when the permissions should show a rationale.
    // The nonce can be used to request the permissions again.
}
```

**3. Normal listeners**

```kotlin
// It must implement [PermissionListener.AcceptedListener].
request.acceptedListener(this)
// It must implement [PermissionListener.DeniedListener].
request.deniedListener(this)
// It must implement [PermissionListener.PermanentlyDeniedListener].
request.permanentlyDeniedListener(this)
// It must implement [PermissionListener.RationaleListener].
request.rationaleListener(this)
```

Compatibility
------

**Android SDK**: KPermissions requires a minimum API level of **14** (the same of the latest support libraries).

Integration
------

You can download a jar from GitHub's [releases page](https://github.com/Fondesa/KPermissions/releases) or grab it from ```jcenter()``` or ```mavenCentral()```.

### Gradle ###

```gradle
dependencies {
    compile 'com.github.fondesa:kpermissions:1.0.0'
}
```

### Maven ###

```xml
<dependency>
  <groupId>com.github.fondesa</groupId>
  <artifactId>kpermissions</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```
