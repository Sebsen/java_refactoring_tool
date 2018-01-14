package code.cafebabe.refactoring;

import java.io.File;

public class CodeBaseRootFile {
    
    private final File referencedFile;
    private final TYPE type;
    
    public enum TYPE {
        JAVA_SOURCE_FILE,
        JAR;
    }
    
    private CodeBaseRootFile(final File pReferencedFile, final TYPE pType) {
        referencedFile = pReferencedFile;
        type = pType;
    }
    
    public static CodeBaseRootFile fromFile(final File pReferencedFile) {
        return new CodeBaseRootFile(pReferencedFile, TYPE.JAVA_SOURCE_FILE);
    }

    public static CodeBaseRootFile fromJar(final File pReferencedFile) {
        return new CodeBaseRootFile(pReferencedFile, TYPE.JAR);
    }
    
    public final boolean isJar() {
        return isType(TYPE.JAR);
    }

    public final boolean isSourceFile() {
        return isType(TYPE.JAVA_SOURCE_FILE);
    }

    private final boolean isType(final TYPE pType) {
        return type == pType;
    }

    /**
     * @return
     * @see java.io.File#getName()
     */
    public String getName() {
        return referencedFile.getName();
    }

    /**
     * @param pathname
     * @return
     * @see java.io.File#compareTo(java.io.File)
     */
    public int compareTo(File pathname) {
        return referencedFile.compareTo(pathname);
    }

    /**
     * @param obj
     * @return
     * @see java.io.File#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return referencedFile.equals(obj);
    }

    /**
     * @return
     * @see java.io.File#hashCode()
     */
    public int hashCode() {
        return referencedFile.hashCode();
    }

    /**
     * @return
     * @see java.io.File#toString()
     */
    public String toString() {
        return referencedFile.toString();
    }
    
    /**
     * @return the referencedFile
     */
    public File getReferencedFile() {
        return referencedFile;
    }

    /**
     * @return the type
     */
    public TYPE getType() {
        return type;
    }
}
