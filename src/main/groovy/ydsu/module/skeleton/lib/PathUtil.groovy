package ydsu.module.skeleton.lib

class PathUtil {
    static File getScriptFile() {
        new File(PathUtil.class.protectionDomain.codeSource.location.path)
    }

    static File getConfDir() {
        new File(scriptFile.parent, 'conf')
    }
}
