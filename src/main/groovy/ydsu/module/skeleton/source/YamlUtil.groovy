package ydsu.module.skeleton.source

import groovy.util.logging.Slf4j
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

import java.nio.charset.StandardCharsets

@Grab(group = 'org.yaml', module = 'snakeyaml', version = '1.24', transitive = false)
@Slf4j
class YamlUtil {
    static LinkedHashMap parseYaml(File yaml) {
        LinkedHashMap content
        if (yaml.exists()) {
            Yaml y = new Yaml()
            content = y.load(yaml.text)
        } else {
            log.warn "$yaml does not exist"
            content = [:]
        }
        content
    }

    static void dumpYaml(def obj, File yaml) {
        DumperOptions options = new DumperOptions()
        options.setPrettyFlow(true)
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
        Yaml y = new Yaml(options)
        Writer writer = new OutputStreamWriter(new FileOutputStream(yaml), StandardCharsets.UTF_8)
        y.dump(obj, writer)
    }
}
