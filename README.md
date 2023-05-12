# RecyclerKit
![Version](https://img.shields.io/github/license/revolut-mobile/RecyclerKit) ![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)

## Installation

Gradle is the only supported build configuration. Since 1.1 RecyclerKit is hosted on mavenCentral,
in order to fetch the dependency, add the following lines to your project level `build.gradle.kts`:

```
allprojects {
    repositories {
        mavenCentral()
    }
}
```

And then to the module level `build.gradle.kts`:

```
dependencies {
    implementation 'com.revolut.recyclerkit:delegates:1.1.0'
    implementation 'com.revolut.recyclerkit:rxdiffadapter:1.1.0'
    implementation 'com.revolut.recyclerkit:decorations:1.1.0'
    implementation 'com.revolut.recyclerkit:kextensions:1.1.0'
    implementation 'com.revolut.recyclerkit:animators:1.1.0'
}
```

## Snapshots

For using snapshot dependencies use the separate repository:

```
allprojects {
    repositories {
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}
```

License
-------

    Copyright 2019 Revolut
    Copyright 2018 Wasabeef

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
