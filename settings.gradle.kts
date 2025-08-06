pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://s01.oss.sonatype.org/content/groups/public") }
        maven { setUrl("https://maven.google.com") }
        //阿里云镜像
        maven { setUrl("https://maven.aliyun.com/repository/jcenter") }
        maven { setUrl("https://repo1.maven.org/maven2/") }
    }
}

rootProject.name = "media-mate"
include(":app")
include(":mediamate")
