// ============================================================================
// (C) Copyright Schalk W. Cronje 2013-2014
//
// This software is licensed under the Apache License 2.0
// See http://www.apache.org/licenses/LICENSE-2.0 for license details
//
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and limitations under the License.
//
// ============================================================================

import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.compile.GroovyCompile

buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
        jcenter()
    }
    dependencies {
        classpath("net.saliman:gradle-cobertura-plugin:2.2.2")
        classpath("gradle.plugin.nl.javadude.gradle.plugins:license-gradle-plugin:0.12.1")
        classpath("com.netflix.nebula:gradle-extra-configurations-plugin:2.2.2")
    }
}

allprojects {
    extra["versionModifier"] = ""
    extra["versionNumber"] = "1.0.1"
    extra["modulesWithGroovyDoc"] = listOf(
        "dsl",
        "gradle-plugin",
        "cloud-provider-core",
        "smb-provider"
    )
    
    val versionModifier: String by extra
    val versionNumber: String by extra
    version = versionNumber + if (versionModifier.isNotEmpty()) "-$versionModifier" else ""
}

subprojects {
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

    extra["vfsVersion"] = "2.1"
    extra["groovyVer"] = "[2.1,2.3.9]"
    extra["jackrabbitVer"] = "1.6.5" // "2.11.1"
    extra["jettyVer"] = "8.1.5.v20120716" // "9.3.5.v20151012"
    extra["slf4jVer"] = "1.7.5"
    extra["bintrayRepo"] = "grysb33r"
    extra["bintrayUser"] = "ysb33r"
    extra["bintrayLicense"] = "Apache-2.0"
    extra["bintrayAttributes"] = emptyMap<String, Any>()
    extra["websitePublishFolder"] = File(rootProject.projectDir, "website")

    repositories {
        jcenter()
    }
}

configure(subprojects.filter { !listOf("jlan", "test-servers", "docs").contains(it.name) }) {
    apply(plugin = "groovy")
    apply(plugin = "maven")
    apply(plugin = "com.github.hierynomus.license")

    val sourceCompatibilityVersion = "1.7"
    val targetCompatibilityVersion = "1.7"

    project.setProperty("sourceCompatibility", sourceCompatibilityVersion)
    project.setProperty("targetCompatibility", targetCompatibilityVersion)

    plugins.withType<JavaPlugin> {
        project.tasks.withType<JavaCompile>().configureEach {
            sourceCompatibility = sourceCompatibilityVersion
            targetCompatibility = targetCompatibilityVersion
        }

        project.tasks.withType<GroovyCompile>().configureEach {
            sourceCompatibility = sourceCompatibilityVersion
            targetCompatibility = targetCompatibilityVersion
        }
    }

    val vfsVersion: String by extra
    val slf4jVer: String by extra

    dependencies {
        "compile"("org.codehaus.groovy.modules.http-builder:http-builder:0.7+")
        "compile"("org.apache.commons:commons-vfs2:$vfsVersion") {
            exclude(group = "org.apache.maven.scm")
        }
        "compile"("org.apache.commons:commons-compress:1.9")
        "testCompile"("org.apache.ftpserver:ftpserver-core:1.0.6")
        "testCompile"("commons-io:commons-io:2.4")
        "testCompile"("commons-net:commons-net:3.+")
        "testRuntime"("commons-httpclient:commons-httpclient:3.1")
        "testRuntime"("org.slf4j:slf4j-simple:$slf4jVer")

        "testCompile"("org.spockframework:spock-core:1.0-groovy-2.3") {
            exclude(module = "groovy-all")
        }
    }

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(the<SourceSetContainer>()["main"].allSource)
        dependsOn("classes")
    }

    val javadocJar by tasks.registering(Jar::class) {
        description = "An archive of the JavaDocs for Maven Central"
        archiveClassifier.set("javadoc")
        from(tasks.named("javadoc"))
    }

    artifacts {
        add("archives", sourcesJar)
        add("archives", javadocJar)
    }

    configure<nl.javadude.gradle.plugins.license.LicenseExtension> {
        header = rootProject.file("config/apache-header")
        strictCheck = true
        ignoreFailures = false
        mapping(mapOf("groovy" to "SLASHSTAR_STYLE"))
        extra.set("year", "2013-2015")
        excludes(setOf("**/*.ad", "**/*.asciidoc", "**/*.adoc", "**/*.md", "**/*.properties", "**/*.txt", "**/*.bz2"))
    }
}

val modulesWithGroovyDoc: List<String> by extra

configure(subprojects.filter { modulesWithGroovyDoc.contains(it.name) }) {
    val websitePublishFolder: File by extra

    tasks.register<Copy>("installDocs") {
        group = "documentation"
        description = "Copy groovydocs to an install directory"
        dependsOn("groovydoc")

        from(tasks.named("groovydoc"))
        into("${websitePublishFolder}/${project.version}/api/${project.name}")
    }
}

tasks.register<Wrapper>("wrapper") {
    gradleVersion = "6.9.4"
}
