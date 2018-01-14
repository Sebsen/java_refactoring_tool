package code.cafebabe.refactoring.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {

    /**
     * Reads in all lines of the handed in files and returns them as a list (may includes duplicates), regardless of any constraints.
     * 
     * @param pFileToRead
     *            The files to read in the content of.
     * @return A single list (may be including duplicated lines) of all lines contained of these files
     */
    public static List<String> readAllLines(final File pFileToRead) {
        return readAllLines(pFileToRead, null);
    }

    /**
     * Reads the content of the handed in files and returns all lines matching one of the handed in patterns.
     * 
     * @param pFileToRead
     *            Files to read
     * @param pPaterns
     *            Pattern which a line has to match
     * @return A list of all lines matching at least one of the handed in patterns
     */
    public static List<String> readAllLines(final File pFileToRead, final Set<Pattern> pPatterns) {
        try (BufferedReader br = new BufferedReader(new FileReader(pFileToRead))) {
            final List<String> lines = new ArrayList<>();
            String line = br.readLine();

            if (pPatterns == null || pPatterns.isEmpty()) {
                while (line != null) {
                    lines.add(line);
                    line = br.readLine();
                }
            } else {
                while (line != null) {
                    for (final Pattern pattern : pPatterns) {
                        final Matcher matcher = pattern.matcher(line);
                        if (matcher.matches()) {
                            lines.add(matcher.group(1));
                            break;
                        }
                    }
                    line = br.readLine();
                }
            }
            return lines;
        } catch (IOException f) {
            throw new IllegalArgumentException(f);
        }
    }

    /**
     * @see FileUtil.getMatchingFiles except that any filters on directories to include/ files absolute path is performed.
     * 
     * @return A set of files satisfying beforementioned conditions.
     */
    public static Set<File> getMatchingFiles(final File pRootFile, final Collection<Pattern> pFileNamePatternsToInclude, final boolean pRecursive) {
        return getMatchingFiles(pRootFile, pFileNamePatternsToInclude, null, pRecursive);
    }

    /**
     * Returns a set of files satisfying the mentioned conditions.
     * 
     * @param pRootFile
     *            File to start recursive depth search
     * @param pFileNamePatternsToInclude
     *            Filename (RegEx) patterns a filename has to conform with to be collected during search
     * @param pDirectoriesToInclude
     *            RegEx patterns a files absolute path has to be conform with, to be included during search
     * @param pRecursive
     *            Flag which indicates whether or not to search recursively for files in subdirectories or not
     * @return A set of files satisfying beforementioned conditions.
     */
    public static Set<File> getMatchingFiles(final File pRootFile, final Collection<Pattern> pFileNamePatternsToInclude, final Collection<Pattern> pDirectoriesToInclude,
            final boolean pRecursive) {
        // Convert to set so that duplicates are removed
        final Set<Pattern> fileNamepatternsToInclude = new HashSet<>(pFileNamePatternsToInclude);
        final Set<Pattern> directoriesToInclude = pDirectoriesToInclude != null ? new HashSet<>(pDirectoriesToInclude) : null;

        final Set<File> collectedFiles = new HashSet<>();
        for (final File file : pRootFile.listFiles((dir, name) -> {
            final File file = new File(dir, name);
            // Pre checks
            if (file.isDirectory() || file.isHidden() || name.startsWith(".")) {
                return false;
            }
            for (Pattern pattern : fileNamepatternsToInclude) {
                if (pattern.matcher(name).matches()) {
                    // logger.debug("Matched: " + name);
                    return true;
                } else {
                    // logger.debug("Didn't match: " + name);
                }
            }
            return false;
        })) {
            collectedFiles.add(file);
        }
        if (pRecursive) {
            // List all sub directories
            final File[] subDirectories = pRootFile.listFiles(File::isDirectory);

            // And loop trough them recursively
            for (final File next : subDirectories) {
                collectedFiles.addAll(getMatchingFiles(next, fileNamepatternsToInclude, pDirectoriesToInclude, pRecursive));
            }
        }

        return filter(directoriesToInclude, collectedFiles);
    }

    /**
     * Filters a given set of files according to passed in filename filters. Filters are precompiled (RegEx) patterns, which are applied on the whole absolute path of each
     * individual file. Filenames which don't match at least one of the passed in patterns are filtered out and removed from the result set.<br/>
     * <br/>
     * If null is passed in for directories to include, any element is filtered from list. If you pass in an empty set of directoriesToInclude, every element is filtered out from
     * the result set - because any element can satisfy absent filters.
     * 
     * @param directoriesToInclude
     *            List of filters of which a file must satisfy at least one to NOT be filtered out.
     * @param collectedFiles
     *            Set of files which should be filtered (according to given file path filters)
     * @return The filtered set of files according to given file path filters
     */
    private static Set<File> filter(final Set<Pattern> directoriesToInclude, final Set<File> collectedFiles) {
        // If we have no patterns (-> null) then return original list
        if (directoriesToInclude != null) {

            if (directoriesToInclude.isEmpty()) {
                return Collections.unmodifiableSet(new HashSet<>());
            }

            final Iterator<File> fileIterator = collectedFiles.iterator();
            while (fileIterator.hasNext()) {
                final File collectedFile = fileIterator.next();
                boolean matched = false;
                for (Pattern pattern : directoriesToInclude) {
                    // If we have patterns to include - even if list might be empty - only return true if at least one pattern matched!
                    if (pattern.matcher(collectedFile.getAbsolutePath()).matches()) {
                        // logger.debug("Matched: " + pathname.getName());
                        matched = true;
                        continue;
                    } else {
                        // logger.debug("Didn't match: " + pathname.getName());
                    }
                }
                // If any pattern has matched, remove file from result set..
                if (!matched) {
                    fileIterator.remove();
                }
            }
        }

        return Collections.unmodifiableSet(collectedFiles);
    }
}
