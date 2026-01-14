// Publishing configuration has been commented out due to unavailable dependencies in JCenter
// If you need publishing functionality, please update the dependencies and repositories

/*
import org.ysb33r.gradle.bintray.BintrayGenericUpload

buildscript {
    repositories {
        jcenter()
        mavenLocal()
    }
    dependencies {
        classpath("org.ysb33r.gradle:bintray:1.6")
        classpath("org.jfrog.buildinfo:build-info-extractor-gradle:3.0.1")
    }
}

// apply(plugin = "com.jfrog.bintray")
apply(plugin = "org.ysb33r.bintray")

val bintrayRepo: String by extra
val moduleName: String by extra
val bintrayTags: List<String> by extra
val bintrayLicense: String by extra
val bintrayUser: String by extra
val bintrayDescription: String by extra
val bintrayAttributes: Map<String, Any> by extra

if (bintrayRepo != "grysb33r") {
    tasks.register<BintrayGenericUpload>("uploadArchives") {
        username = project.property("bintrayUserName") as String?
        apiKey = project.property("bintrayApiKey") as String?
        repoOwner = project.property("bintrayUserName") as String?
        repoName = bintrayRepo
        packageName = moduleName
        tags = bintrayTags.toTypedArray()
        licenses = arrayOf(bintrayLicense)
        vcsUrl = "https://github.com/ysb33r/groovy-vfs.git"
        autoCreatePackage = true
        updatePackage = true

        gpgSign = true
        gpgPassphrase = project.property("bintrayGpgPassphrase") as String?

        onlyIf { !(version as String).endsWith("SNAPSHOT") }
    }
} else {
    apply(plugin = "maven")
    // apply(plugin = "com.jfrog.artifactory")

    tasks.named<Upload>("install") {
        repositories.withGroovyBuilder {
            "mavenInstaller" {
                "pom" {
                    setProperty("artifactId", project.property("archivesBaseName"))
                }
            }
        }
    }

    tasks.named<Upload>("uploadArchives") {
        repositories.withGroovyBuilder {
            if (!(version as String).endsWith("SNAPSHOT")) {
                "bintrayMavenDeployer" {
                    setProperty("username", project.property("bintrayUserName"))
                    setProperty("apiKey", project.property("bintrayApiKey"))
                    setProperty("repoOwner", bintrayUser)
                    setProperty("repoName", bintrayRepo)
                    setProperty("packageName", moduleName)
                    setProperty("description", bintrayDescription)
                    setProperty("tags", bintrayTags)
                    setProperty("licenses", bintrayLicense)
                    setProperty("vcsUrl", "https://github.com/ysb33r/groovy-vfs.git")

                    if (bintrayAttributes.isNotEmpty()) {
                        "versionAttributes"(bintrayAttributes)
                    }
                    setProperty("autoCreatePackage", true)
                    setProperty("updatePackage", true)
                }
            }
        }
    }

    // artifactory {
    //     publish {
    //         contextUrl = "http://oss.jfrog.org"
    //         repository {
    //             repoKey = "oss-snapshot-local"
    //             username = project.properties.bintrayUserName
    //             password = project.properties.bintrayApiKey
    //         }
    //     }
    // }
    //
    // artifactoryPublish {
    //     onlyIf { version.endsWith("SNAPSHOT") }
    // }
}
*/
