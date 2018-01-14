package code.cafebabe.refactoring.util;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import code.cafebabe.refactoring.util.FileUtil;

@RunWith(JUnitPlatform.class)
public class FileUtilTest {

    private static final boolean RECURSIVE = true;

    @Test
    public void resolveJavaClasses() {
        assertEquals(9, FileUtil.getMatchingFiles(new File("src/test/resources/classesWithStringBuilderVariants/"), asList(Pattern.compile(".*\\.java")), RECURSIVE).size());
    }
    
}
