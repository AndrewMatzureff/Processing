import org.gradle.kotlin.dsl.application

plugins {
    id("java")
    id("jacoco")
    //id("io.freefair.lombok") version "9.2.0"
    application
}

group = "com.matzua"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.projectlombok:lombok:1.18.44")

    // Add the launcher as a testRuntimeOnly dependency to ensure it is available during test execution.
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // required if you want to use Mockito for unit tests
    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")

    // lombok
    compileOnly("org.projectlombok:lombok:1.18.44")
    annotationProcessor("org.projectlombok:lombok:1.18.44")

    testCompileOnly("org.projectlombok:lombok:1.18.44")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.44")

    compileOnly("org.slf4j:slf4j-api:1.7.+")
    implementation("org.slf4j:slf4j-log4j12:1.7.29")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Add Dagger dependencies
    implementation("com.google.dagger:dagger:2.52")
    annotationProcessor("com.google.dagger:dagger-compiler:2.52")
}

//for including in the copy task
val natives = copySpec {
    from("natives")
}

tasks {
    register("copyNatives", Copy::class) {

        into("build/libs/natives")
        with(natives)
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport") // Generate the report after tests run
}

tasks.assemble {
    dependsOn(tasks.named("copyNatives").get())
}

application {
    mainClass.set("com.matzua.Main")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.matzua.Main"
    }

    // To avoid the duplicate handling strategy error
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To add all of the dependencies otherwise a "NoClassDefFoundError" error
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

tasks.withType<JacocoReport> {
    classDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                exclude("org/example/B.class")
            }
    )
}
