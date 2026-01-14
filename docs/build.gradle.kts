import org.asciidoctor.gradle.AsciidoctorTask

buildscript {
    dependencies {
        classpath("org.asciidoctor:asciidoctor-gradle-plugin:1.5.3")
    }
}

apply(plugin = "org.asciidoctor.convert")

extra["editCSV"] = fun(file: File, newText: String) {
    val lines = file.readLines().toMutableList()

    if (!(version as String).endsWith("-SNAPSHOT")) {
        lines.removeAll { it.endsWith("-SNAPSHOT") }
    }

    if (!lines.contains(version as String)) {
        logger.lifecycle("Adding '$newText' to $file")
        file.writeText(buildString {
            appendLine(newText)
            lines.forEach { appendLine(it) }
        })
    }
}

tasks.named<AsciidoctorTask>("asciidoctor") {
    sources(delegateClosureOf<PatternSet> {
        include("product-documentation.adoc")
    })

    attributes(mapOf(
        "revnumber" to "${project.version}",
        // "dslSrcRoot" to "${project(":dsl").projectDir}/src",
        "dslSrcRoot" to "${project(":groovy-vfs").projectDir}/src",
        "gradleSrcRoot" to "${project(":gradle-plugin").projectDir}/src",
        "smbSrcRoot" to "${project(":smb-provider").projectDir}/src",
        "cloudSrcRoot" to "${project(":cloud-provider-core").projectDir}/src",
        "cmdlineSrcRoot" to "${project(":cmdline").projectDir}/src"
    ))
}

tasks.register<AsciidoctorTask>("landingPage") {
    inputs.files("src/docs/landingPage/docs.csv")
    inputs.files("src/docs/landingPage/api.csv")

    sourceDir = file("src/docs/landingPage")
    outputDir = file("${buildDir}/landingPage")
    separateOutputDirs = false

    sources(delegateClosureOf<PatternSet> {
        include("index.adoc")
    })

    resources(delegateClosureOf<CopySpec> {
        from("stylesheets")
        from("images")
    })

    mustRunAfter("updateversionDoc")
}

val websitePublishFolder: File by extra

tasks.register<Copy>("installDocs") {
    with(copySpec {
        from(File((tasks.named("asciidoctor").get() as AsciidoctorTask).outputDir, "html5"))
        into("${project.version}/docs")
    })

    from(tasks.named("landingPage").get().outputs)

    into(websitePublishFolder)

    dependsOn("asciidoctor", "landingPage")

    doLast {
        logger.lifecycle("Files copied to website repo. To publish, change to ${websitePublishFolder} and commit and push from there.")
    }

    onlyIf { !(version as String).endsWith("-SNAPSHOT") }
}

tasks.register("updateversionDoc") {
    description = "Updates the local CSV version file"
    val outputFile = file("src/docs/landingPage/docs.csv")
    outputs.file(outputFile)

    outputs.upToDateWhen {
        outputFile.readLines().contains(version as String) &&
            !(version as String).endsWith("-SNAPSHOT") && !outputFile.readText().contains("-SNAPSHOT")
    }

    doLast {
        val editCSV = extra["editCSV"] as (File, String) -> Unit
        editCSV(outputFile, "link:${project.version}/docs/product-documentation.html[${project.version}]")
    }
}

// task updateApiVersionDoc {
//     description "Updates the local CSV API versions file"
//     outputs.file "src/docs/landingPage/api.csv"
//
// //    outputs.upToDateWhen { t ->
// //        t.outputs.files.singleFile.text.readLines().contains(version) &&
// //            !version.endsWith("-SNAPSHOT") && !t.outputs.files.singleFile.text.contains("-SNAPSHOT")
// //    }
// }

// updateApiVersionDoc << {
//     rootProject.modulesWithGroovyDoc.each {
//
//     }
//     editCSV outputs.files.singleFile,"${version},link:"
//
// }
