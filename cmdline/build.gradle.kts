// ============================================================================
// (C) Copyright Schalk W. Cronje 2012 - 2014
//
// This software is licensed under the Apache License 2.0
// See http://www.apache.org/licenses/LICENSE-2.0 for license details
//
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and limitations under the License.
// ============================================================================

// Asciidoctor configuration has been commented out due to unavailable dependencies
// If you need documentation generation, please update the dependencies and repositories

/*
buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath("org.asciidoctor:asciidoctor-gradle-jvm:3.3.2")
    }
}

apply(plugin = "application")
apply(plugin = "org.asciidoctor.jvm.convert")
*/

apply(plugin = "application")

group = "org.ysb33r.groovy"
setProperty("archivesBaseName", "cmdline-vfs")

configure<JavaApplication> {
    mainClass.set("org.ysb33r.groovy.vfs.app.Run")
    applicationName = "vfs"
    applicationDefaultJvmArgs = listOf("-Dvfs.scriptname=vfs")
}

extra["moduleName"] = "vfs"
extra["bintrayDescription"] = "A command-line utility for file operations on various local & remote virtual filesystems"
extra["bintrayTags"] = listOf("groovy", "vfs")
extra["bintrayRepo"] = "nanook"

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Implementation-Title" to "VFS Command-line Utility",
            "Implementation-Version" to project.version
        )
    }
}

repositories {
    mavenCentral()
}

val groovyVer: String by extra

dependencies {
    "compile"("org.codehaus.groovy:groovy-all:$groovyVer")
    "compile"("commons-cli:commons-cli:1.2")
    "runtime"("commons-io:commons-io:2.4")
    "runtime"("commons-net:commons-net:3.+")
    "runtime"("commons-httpclient:commons-httpclient:3.1")
    "runtime"("org.slf4j:slf4j-simple:1.7.5")
    "runtime"("com.jcraft:jsch:0.1.48")
    "compile"(project(":groovy-vfs"))
    // "compile"(project(":dsl"))
    "runtime"(project(":smb-provider"))
    "runtime"(project(":cloud-provider-core"))
}

tasks.named<Test>("test") {
    systemProperty("TESTFSREADROOT", "${projectDir}/src/test/resources")
    systemProperty("TESTFSWRITEROOT", "${buildDir}/tmp/test/cmdline-vfs")
}

/*
configure<org.asciidoctor.gradle.AsciidoctorExtension> {
    setBackends(listOf("html5"))
    options(mapOf("eruby" to "erubis"))

    attributes(mapOf(
        "icons" to "font",
        "source-highlighter" to "prettify",
        "experimental" to true,
        "copycss" to true,
        "idprefix" to "",
        "idseparator" to "-",
        "revnumber" to project.version
    ))
}
*/

val applicationName = "vfs"

tasks.named<Tar>("distTar") {
    extension = "tgz"
    compression = Compression.GZIP
    /* Commented out due to asciidoctor being disabled
    from(File(buildDir, "asciidoc")) {
        into("$applicationName-${project.version}/docs")
    }

    dependsOn("asciidoctor")
    */
}

tasks.named<Zip>("distZip") {
    /* Commented out due to asciidoctor being disabled
    from(File(buildDir, "asciidoc")) {
        into("$applicationName-${project.version}/docs")
    }

    dependsOn("asciidoctor")
    */
}

val sourcesZip by tasks.registering(Zip::class) {
    archiveClassifier.set("sources")
    from(project.the<SourceSetContainer>()["main"].allSource)
    archiveBaseName.set(applicationName)
    dependsOn("classes")
}

// TODO: ISSUE #20 - Generate man pages from asciidocs

apply(from = "../gradle/publish.gradle.kts")

/*
// uploadArchives configuration has been commented out since publish.gradle.kts is disabled
tasks.named<Upload>("uploadArchives") {
    val sources = configurations.create("sources")
    sources.dependencies.add(files(tasks.named("distZip").get().outputs.files))
    sources.dependencies.add(files(tasks.named("distTar").get().outputs.files))
    sources.dependencies.add(files(sourcesZip.get().outputs.files))

    dependsOn("distZip", "distTar", sourcesZip)
}
*/

// bintray {
//     filesSpec { // When uploading any arbitrary files ('filesSpec' is a standard Gradle CopySpec)
//         from distZip.outputs.files
//         from distTar.outputs.files
//         from sourcesZip.outputs.files
// //        into 'standalone_files/level1'
// //        rename '(.+)\\.(.+)', '$1-suffix.$2'
//     }
// }
