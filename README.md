
# Boomware Android-sdk
Boomware sdk allows you to verify that a phone number is valid, reachable, and accessible by your user.
Boomware sdk allows you to use push notifications (FCM) with SMS failback. 

## Installation

Add  [boomware-sdk-1.08.jar](sample/libs/boomware-sdk-1.08.jar) to `libs` folder of your app.

Add the following dependencies to your app's `build.gradle` file with actual versions:
```groovy
dependencies {
     compile 'com.squareup.okhttp3:okhttp:3.9.0'
     compile 'com.google.code.gson:gson:2.8.1'       
     compile 'com.google.firebase:firebase-core:15.0.2' 
     compile 'com.google.firebase:firebase-messaging:15.0.2'
}
```

## Permissions

Default permissions:
```xml
<uses-permission android:name="android.permission.INTERNET"/>
```  

Optional permissions (for automatically receive verification SMS/Call): 

```xml
<uses-permission android:name="android.permission.RECEIVE_SMS"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>

```

## Sdk Initialization

Initialize Boomware sdk in your Application class:

```java
public class App extends Application {

    public void onCreate(){
        super.onCreate();

        //Receive api key in your console on https://console.boomware.com)
        Boomware.initialize(this, "<api key>");
    }
}
```

## Using Notifications with SMS failback.

- Bind a valid user phone number (f.ex.: `+37100000000`) using the method below:

```java
    Boomware.getInstance().bindNumber("<user phone number>");
```

- Set up Firebase and the FCM SDK using the following instruction:
https://firebase.google.com/docs/cloud-messaging/android/client

- Add `BoomwareFirebaseIdService` to your AndroidManifest.xml:

```xml
<service android:name="com.boomware.sdk.receiver.BoomwareFirebaseIdService">
   <intent-filter>
       <action android:name="com.google.firebase.INSTANCE_ID_EVENT" />
   </intent-filter>
</service>
```

- Show Boomware notification message in your implementation of `FirebaseMessagingService`

```java
public class MyNotificationService extends FirebaseMessagingService {

    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (Boomware.getInstance().onPushMessageReceived(remoteMessage)) {

            //implement your notification
            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"));
        }
        
    }
}
```
        
        
## Using Number verification

Implement BoomwareCallback in your class where you need to verify user phone number like this:

```java
public class MainActivity extends AppCompatActivity implements BoomwareCallback {
    
    protected void onCreate(Bundle savedInstanceState) {
               
        //Send verification SMS to number
        findViewById(R.id.verify_btn).setOnClickListener(view ->
              Boomware.getInstance().verifyNumber("<user phone number>", Boomware.Method.sms, this)
        );
        
        //Check entered verification code
        findViewById(R.id.check_btn).setOnClickListener(view ->
              Boomware.getInstance().check("<code>", this)
        );
    }
    
    @Override 
    public void onVerificationCompleted(BoomwareRequest request) {
        // you should complete registration on your backend side
        // use API method POST https://api.boomware.com/v1/verify/info       
    }
    
    @Override 
    public void onVerificationFailed(BoomwareException e) {
        switch (e.getErrorCode()) {
            case BoomwareException.INVALID_NUMBER:
            // show error message...
            case BoomwareException.INVALID_CODE:
            // show error message...           
        }
    }
}
```  

You can use `<com.boomware.sdk.ui.PhoneEditText/>` view with phone number formating and resolving of phone prefix by user country.
You can use `<com.boomware.sdk.ui.CodeEditText/>`view for verification code enter.
See a full example below. 

## Example App
An example app is provided [here](sample) that shows a simple integration in one Activity flow
