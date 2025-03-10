# WebSimplifier

A simple Android library that **simplifies** web articles (using **Readability.js** in a **WebView**) and returns string with:
1. **HTML** – a cleaned-up HTML page with a title and content  
2. **JSON** – an object containing `"title"` and `"content"`  
3. **Plain text** – only the article text  


## Features
- **Three return types** (`HTML`, `JSON`, `PLAIN`) to fit different needs  
- **Suspending function** (`simplifyPage`) that runs on the main/UI thread via `withContext(Dispatchers.Main)`  
- **Optional error callback** – get an error message if something goes wrong  
- Internally uses a lightweight `WebView` + `Readability.js`  
- **Easy to integrate**: just call one function to get simplified content


## Installation
1. Add JitPack to your root `build.gradle` or `settings.gradle` repositories:
   ```groovy
   repositories {
       maven { url "https://jitpack.io" }
       google()
       mavenCentral()
   }
2. Add the dependency in your module-level build.gradle:
   
   ```implementation "com.github.levice1:WebSimplifier:1.0.0"``` 

## Usage
1. Make sure your Android app has Internet permission

  ```  <uses-permission android:name="android.permission.INTERNET" />```

3. Call simplifyPage() from a coroutine context in MAIN thread:
   ``` fun loadAndSimplify(context: Context, url: String) {
    CoroutineScope(Dispatchers.Main).launch {
        val simplifiedResult = WebSimplifier.simplifyPage(
            context = context,
            link = url,
            returnType = ReturnType.PLAIN,
            onError = { errorMessage ->
                // Optional error handling
                Log.e("WebSimplifierDemo", "Error: $errorMessage")
            }
        )
        Log.d("WebSimplifierDemo", "Result: $simplifiedResult")
    }

## Demo
### Below are two images demonstrating the library in action:
<img src="./screenshots/1.jpg" alt="Without" width="300" /><img src="./screenshots/2.png" alt="With" width="300" />




