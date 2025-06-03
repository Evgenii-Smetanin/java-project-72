plugins {
    application
    checkstyle
    jacoco
}

repositories {
    mavenCentral()
}

dependencies {
    implementation ("io.javalin:javalin:6.6.0")
    implementation ("org.slf4j:slf4j-simple:2.0.17")

    implementation("com.h2database:h2:2.3.232")
    implementation("com.zaxxer:HikariCP:6.3.0")

    implementation("gg.jte:jte:3.2.1")
    implementation("io.javalin:javalin-rendering:6.6.0")

//    implementation ("org.thymeleaf:thymeleaf:3.0.15.RELEASE")
//    implementation ("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.1.0")
//    implementation ("org.thymeleaf.extras:thymeleaf-extras-java8time:3.0.4.RELEASE")
    implementation ("org.webjars:bootstrap:5.1.3")

    implementation ("org.postgresql:postgresql:42.3.3")

    implementation ("org.projectlombok:lombok:1.18.22")
    implementation ("org.projectlombok:lombok:1.18.22")

    implementation ("org.glassfish.jaxb:jaxb-runtime:2.3.5")
    implementation ("javax.activation:activation:1.1.1")

    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation ("org.junit.jupiter:junit-jupiter-params:5.8.2")
    testImplementation ("org.assertj:assertj-core:3.22.0")
    testImplementation ("com.konghq:unirest-java:3.13.6")

    testImplementation ("com.squareup.okhttp3:mockwebserver:4.9.2")
    implementation ("com.konghq:unirest-java:3.13.6")
    implementation ("org.jsoup:jsoup:1.15.2")

    compileOnly ("org.projectlombok:lombok:1.18.30")
    annotationProcessor ("org.projectlombok:lombok:1.18.30")
    testCompileOnly ("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor ("org.projectlombok:lombok:1.18.30")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "hexlet.code.App"
}

checkstyle {
    toolVersion = "10.0"
}

jacoco {
    toolVersion = "0.8.13"
    reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
    }
}