// ============================================================================
// (C) Copyright Schalk W. Cronje 2014
//
// This software is licensed under the Apache License 2.0
// See http://www.apache.org/licenses/LICENSE-2.0 for license details
//
// Unless required by applicable law or agreed to in writing, software distributed under the License is
// distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and limitations under the License.
//
// ============================================================================

buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath("org.ysb33r.gradle:vfs-gradle-plugin:0.5")
        classpath("commons-httpclient:commons-httpclient:3.1")
        classpath("xerces:xercesImpl:2.9.1")
    }
}

apply(plugin = "vfs")

val jlan by configurations.creating

dependencies {
    jlan("xerces:xercesImpl:2.9.1")
}

extra["unpackDir"] = File(buildDir, "alfresco")

val unpackDir: File by extra

tasks.register("download") {
    doLast {
        if (!buildDir.exists()) {
            mkdir(buildDir)
        }
        ant.withGroovyBuilder {
            "vfs" {
                "options" {
                    "http" {
                        setProperty("followRedirect", true)
                    }
                }
                "cp"(
                    "http://download.sourceforge.net/project/alfresco/JLAN/Alfresco%20JLAN%205.0/${property("archive")}",
                    buildDir,
                    "overwrite" to true
                )
            }
        }
    }

    // TODO: Fix this message:
    // Creating properties on demand (a.k.a. dynamic properties) has been deprecated and is scheduled to be removed
    // in Gradle 2.0. Please read http://gradle.org/docs/current/dsl/org.gradle.api.plugins.ExtraPropertiesExtension.html
    // for information on the replacement for dynamic properties.
    // Deprecated dynamic property: "archive" on "task ':jlan:download'", value: "alfresco-jlan-source_5...".
    extra["archive"] = "alfresco-jlan-source_5_0_0.zip"

    onlyIf {
        !File(buildDir, property("archive") as String).exists()
    }
}

tasks.register<Delete>("clean") {
    delete(buildDir)
}

tasks.register<Copy>("unpack") {
    from(zipTree(File(buildDir, tasks.named("download").get().property("archive") as String)))
    into(unpackDir)
    dependsOn("download")
}

tasks.register("build") {
    doLast {
        val buildxml = File(unpackDir, "build.xml")
        ant.withGroovyBuilder {
            getProperty("antProject").let { antProject ->
                (antProject as org.apache.tools.ant.Project).apply {
                    setBaseDir(unpackDir)
                    setUserProperty(org.apache.tools.ant.MagicNames.ANT_FILE, buildxml.absolutePath)
                }
            }
        }
        org.apache.tools.ant.ProjectHelper.configureProject(ant.antProject as org.apache.tools.ant.Project, buildxml)
        (ant.antProject as org.apache.tools.ant.Project).executeTarget("createJar")
    }

    inputs.file(File(unpackDir, "build.xml"))
    outputs.file(File(unpackDir, "jars/alfresco-jlan.jar"))
    outputs.file(File(unpackDir, "libs/cryptix-jce-provider.jar"))
    dependsOn("unpack")

    onlyIf {
        !File(unpackDir, "jars/alfresco-jlan.jar").exists() &&
        !File(unpackDir, "libs/cryptix-jce-provider.jar").exists()
    }
}

tasks.register<Copy>("jar") {
    from((tasks.named("build").get().outputs.files as org.gradle.api.file.FileCollection))
    into(File(buildDir, "libs"))
    dependsOn("build")
}
