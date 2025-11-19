publish_maven_local:
	./gradlew clean \
		:deeplinkdispatch:assemble \
		:deeplinkdispatch-base:assemble \
		:deeplinkdispatch-processor:assemble \
		:deeplinkdispatch-processor:dokkaJavadocJar \
		:deeplinkdispatch-processor:sourcesJar \
		:deeplinkdispatch-base:sourcesJar \
		:deeplinkdispatch-base:dokkaJavadocJar
	./gradlew publishToMavenLocal \
		-x dokkaHtml \
		-x dokkaJavadocJar \
		-x dokkaGfm \
		-x dokkaGenerate \
		-x dokkaGenerateHtml \
		-x dokkaJavadoc \
		-x dokkaJavadocPartial \
		-x javaDocReleaseGeneration
	@echo "The output dir is: $$HOME/.m2/repository/com/airbnb"
