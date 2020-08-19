# BillMate

<img src="/presentation/demo.gif" width="300" align="right" hspace="0" />

**Easy transaction with roommates**

_min API Version 26_

Layout sketch ready to use. Requires to be connected to your Firebase project.

# Documentation

**[Dokumentacja projektu PL](https://github.com/wgalka/BillMate/blob/master/BillMate%20Dokumentacja%20Projektowa.pdf)**

**[Instrukcja obsługi PL](https://github.com/wgalka/BillMate/blob/master/Manual%20In%C5%BCynierski%20projekt%20specjalno%C5%9Bciowy%20BillMate.pdf)**

# How to run

1. Clone this repository.
2. Get **google-services.json** from your Firebase project.
    1. Follow only the first **three steps** in this [documentation](https://firebase.google.com/docs/android/setup?authuser=0) (the last step is **3.1 b**).
3. Enable Google Sign-In in the Firebase console:
    1. In the Firebase console, open the **Auth** section.
    2. On the **Sign in method** tab, enable the **Google** sign-in method and click **Save**.
4. Once you have placed the **google-services.json** in a cloned project, in the IDE do **rebuild project**.
5. Run ‘app’.

# How to add function to firebase project

**[Deploy functions](https://github.com/wgalka/BillMate/tree/master/Firestore%20Functions)** **[ according to this manual.](https://youtu.be/DYfP-UIKxH0?list=PLl-K7zZEsYLkPZHe41m4jfAxUi0JjLgSM)**

**[Don't forget about add the Firebase Admin SDK.](https://firebase.google.com/docs/admin/setup?authuser=1#add-sdk)**

# Used libraries

**[Picasso](https://github.com/square/picasso)**

**[CircleImageView](https://github.com/hdodenhof/CircleImageView)**

**[Animated-Icons](https://github.com/tarek360/Animated-Icons)**

**[PullToMakeSoup](https://github.com/Yalantis/pull-to-make-soup)**