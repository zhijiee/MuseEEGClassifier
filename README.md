# Muse EEG Classifier

An Android application that classify real-time EEG data from Muse as Mindfulness/Active State using a SVM_Model.

![text](https://github.com/zhijiee/MuseEEGClassifier/blob/master/documentation/user_guide_img/app.PNG)

# Installation Guide

## Application Developed and tested on
- Android Studio 3.1.2
- Samsung Tab S2 9.7 inch
- Muse 2016

## Gradle dependencies
```
    implementation 'com.android.support:support-v4:27.1.0'
    implementation files('libs/libmuse_android.jar')
    implementation files('libs/libsvm.jar')
    implementation 'com.android.support.constraint:constraint-layout:1.0.2'
    implementation 'com.github.lzyzsd:circleprogress:1.2.1' //Circle Progress Library
    testImplementation 'junit:junit:4.12'
    testImplementation 'org.mockito:mockito-core:1.10.19'
    implementation "com.opencsv:opencsv:4.0"
    implementation 'com.jjoe64:graphview:4.2.1'
```

## Additional Libraries (included in project)
[Muse SDK](http://developer.choosemuse.com/)

## Android Studio Setup
1. Clone the repository using Android Studio
![text](https://github.com/zhijiee/MuseEEGClassifier/blob/master/documentation/user_guide_img/1.PNG)
2. Create an Android Project
![text](https://github.com/zhijiee/MuseEEGClassifier/blob/master/documentation/user_guide_img/2.PNG)
3. Create project from exisitng sources. Use default settings for all import project.
![text](https://github.com/zhijiee/MuseEEGClassifier/blob/master/documentation/user_guide_img/3.PNG)
![text](https://github.com/zhijiee/MuseEEGClassifier/blob/master/documentation/user_guide_img/4.PNG)
![text](https://github.com/zhijiee/MuseEEGClassifier/blob/master/documentation/user_guide_img/5.PNG)
![text](https://github.com/zhijiee/MuseEEGClassifier/blob/master/documentation/user_guide_img/6.PNG)
![text](https://github.com/zhijiee/MuseEEGClassifier/blob/master/documentation/user_guide_img/7.PNG)
![text](https://github.com/zhijiee/MuseEEGClassifier/blob/master/documentation/user_guide_img/8.PNG)

The project is now ready to build and run.


# User Guide

![text](https://github.com/zhijiee/MuseEEGClassifier/blob/master/documentation/user_guide_img/app.PNG)
- Graph on top shows the history of Meditation Level
- Circle progressbar shows the current meditation level
- bottom left shows the headband signal indicator per channel (1= Excellent, 4=Bad)

The application predicts your current state and display on the graph and circle progressbar.