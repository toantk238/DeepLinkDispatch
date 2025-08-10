import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import javax.inject.Inject

abstract class GitInfoSource : ValueSource<String, ValueSourceParameters.None> {

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String? {
        val branch = getOutput("git rev-parse --symbolic-full-name HEAD")
        val rev = getOutput("git rev-parse HEAD")
        return "Branch : $branch\\nRev : $rev"
    }

    private fun getOutput(cmd: String): String {
        val output = ByteArrayOutputStream()
        execOperations.exec {
            commandLine(cmd.split(" "))
            standardOutput = output
        }
        return String(output.toByteArray(), Charset.defaultCharset())
    }
}