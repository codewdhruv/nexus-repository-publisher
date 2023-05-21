import com.sonatype.nexus.api.common.Authentication
import com.sonatype.nexus.api.common.ServerConfig
import com.sonatype.nexus.api.repository.v3.DefaultAsset
import com.sonatype.nexus.api.repository.v3.DefaultComponent
import com.sonatype.nexus.api.repository.v3.RepositoryManagerV3ClientBuilder
import java.net.URI
import groovy.cli.commons.CliBuilder

@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.25')
@Grab(group='com.sonatype.nexus', module='nexus-platform-api', version='3.5.20190215-094356.8a0ba7f')

cli = new CliBuilder(usage: 'Repository', expandArgumentFiles: true)
cli.h(type: Boolean, longOpt: 'help', 'Prints this help text')
cli.PLUGIN_SERVERURL(type: String, longOpt: 'serverurl', 'Server url', required: true)
cli.PLUGIN_USERNAME(type: String, longOpt: 'username', 'Username', required: true)
cli.PLUGIN_PASSWORD(type: String, longOpt: 'password', 'Password', required: true)
cli.PLUGIN_FORMAT(type: String, longOpt: 'format', 'Artifact format. Examples: maven2', required: true)
cli.PLUGIN_FILENAME(longOpt: 'filename', 'Filename to upload', convert: {new File(it)}, required: true)
cli.PLUGIN_COMPONENT_ATTRIBUTES(longOpt: 'component-attributes', args:2, valueSeparator:'=', argName:'key=value', 'Component coordinates, can be used multiple times. Example: ' +
    '-CgroupId=com.example -CartifactId=myapp -Cversion=1.0', required: true)
cli.PLUGIN_ASSET_ATTRIBUTES(longOpt: 'asset-attributes', args:2, valueSeparator:'=', argName:'key=value', 'Asset attributes, can be used multiple times. Example: ' +
    '-Aextension=jar -Aclassifier=bin', required: true)
cli.PLUGIN_REPOSITORY(type: String, longOpt: 'repository', 'Name of target repository on Nexus. Example: maven-releases', required: true)
cli._(type: String, longOpt: 'tagname', 'The tag to apply (tag must already exist)')
options = cli.parse(args)
if (!options) {
  System.exit(1)
}
if (options.h) {
  cli.usage()
  System.exit(0)
}

println "PLUGIN_SERVERURL: ${options.PLUGIN_SERVERURL}"

// Create client
serverConfig = new ServerConfig(URI.create(options.PLUGIN_SERVERURL), new Authentication(options.PLUGIN_USERNAME, options.PLUGIN_PASSWORD))
client = new RepositoryManagerV3ClientBuilder().withServerConfig(serverConfig).build()

// Utility function to convert attribute list to map
def toMap = { list ->
  def map = [:]
  list.each { attribute ->
    def parts = attribute.split('=')
    if (parts.size() == 2) {
      map[parts[0]] = parts[1]
    }
  }
  map
}

// Set component coordinates
component = new DefaultComponent(options.PLUGIN_FORMAT)
def componentAttributesMap = toMap(options.PLUGIN_COMPONENT_ATTRIBUTES)
componentAttributesMap.each { key, value ->
  println "Attribute: key=$key, value=$value"
  component.addAttribute(key, value)
}

// Set asset attributes
asset = new DefaultAsset(options.PLUGIN_FILENAME.name, options.PLUGIN_FILENAME.newInputStream())
def assetAttributesMap = toMap(options.PLUGIN_ASSET_ATTRIBUTES)
assetAttributesMap.each { key, value ->
  println "Asset Attribute: key=$key, value=$value"
  asset.addAttribute(key, value)
}
component.addAsset(asset)

// Upload to Nexus repository
println "Uploading to Nexus repository: ${options.PLUGIN_REPOSITORY}"
println "Component: $component"
try {
  client.upload(options.PLUGIN_REPOSITORY, component)
  println "Upload successful!"
} catch (Exception e) {
  println "Upload failed: ${e.message}"
  e.printStackTrace()
}
