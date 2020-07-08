buildscript {
    repositories {
        jcenter()
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.0-alpha03")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
    }
}

allprojects {
    repositories {
        jcenter()
        google()
    }
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}
