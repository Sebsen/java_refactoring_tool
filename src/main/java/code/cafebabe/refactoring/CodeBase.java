package code.cafebabe.refactoring;

import static java.util.Arrays.asList;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import code.cafebabe.refactoring.util.FileUtil;

public class CodeBase implements BlockingQueue<File> {

    @Override
    public String toString() {
        return matchingFiles.toString();
    }

    private final BlockingQueue<File> matchingFiles;
    private final Set<CodeBaseRootFile> codeBaseRoots;

    private CodeBase(final Set<CodeBaseRootFile> pCodeBaseRoot, final boolean pRecursive) {
        matchingFiles = new LinkedBlockingQueue<>();
        codeBaseRoots = Collections.unmodifiableSet(new LinkedHashSet<>(pCodeBaseRoot));
        codeBaseRoots.stream().filter(CodeBaseRootFile::isSourceFile)
                .flatMap(f -> FileUtil.getMatchingFiles(f.getReferencedFile(), asList(Pattern.compile(".*\\.java")), pRecursive).stream()).forEach(matchingFiles::add);
    }

    /**
     * @param action
     * @see java.lang.Iterable#forEach(java.util.function.Consumer)
     */
    public void forEach(Consumer<? super File> action) {
        matchingFiles.forEach(action);
    }

    /**
     * @param e
     * @return
     * @see java.util.concurrent.BlockingQueue#add(java.lang.Object)
     */
    public boolean add(File e) {
        return matchingFiles.add(e);
    }

    /**
     * @return
     * @see java.util.Collection#size()
     */
    public int size() {
        return matchingFiles.size();
    }

    /**
     * @return
     * @see java.util.Collection#isEmpty()
     */
    public boolean isEmpty() {
        return matchingFiles.isEmpty();
    }

    /**
     * @return
     * @see java.util.Queue#remove()
     */
    public File remove() {
        return matchingFiles.remove();
    }

    /**
     * @param e
     * @return
     * @see java.util.concurrent.BlockingQueue#offer(java.lang.Object)
     */
    public boolean offer(File e) {
        return matchingFiles.offer(e);
    }

    /**
     * @return
     * @see java.util.Queue#poll()
     */
    public File poll() {
        return matchingFiles.poll();
    }

    /**
     * @return
     * @see java.util.Queue#element()
     */
    public File element() {
        return matchingFiles.element();
    }

    /**
     * @return
     * @see java.util.Collection#iterator()
     */
    public Iterator<File> iterator() {
        return matchingFiles.iterator();
    }

    /**
     * @return
     * @see java.util.Queue#peek()
     */
    public File peek() {
        return matchingFiles.peek();
    }

    /**
     * @param e
     * @throws InterruptedException
     * @see java.util.concurrent.BlockingQueue#put(java.lang.Object)
     */
    public void put(File e) throws InterruptedException {
        matchingFiles.put(e);
    }

    /**
     * @return
     * @see java.util.Collection#toArray()
     */
    public Object[] toArray() {
        return matchingFiles.toArray();
    }

    /**
     * @param e
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     * @see java.util.concurrent.BlockingQueue#offer(java.lang.Object, long, java.util.concurrent.TimeUnit)
     */
    public boolean offer(File e, long timeout, TimeUnit unit) throws InterruptedException {
        return matchingFiles.offer(e, timeout, unit);
    }

    /**
     * @param a
     * @return
     * @see java.util.Collection#toArray(java.lang.Object[])
     */
    public <T> T[] toArray(T[] a) {
        return matchingFiles.toArray(a);
    }

    /**
     * @return
     * @throws InterruptedException
     * @see java.util.concurrent.BlockingQueue#take()
     */
    public File take() throws InterruptedException {
        return matchingFiles.take();
    }

    /**
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     * @see java.util.concurrent.BlockingQueue#poll(long, java.util.concurrent.TimeUnit)
     */
    public File poll(long timeout, TimeUnit unit) throws InterruptedException {
        return matchingFiles.poll(timeout, unit);
    }

    /**
     * @return
     * @see java.util.concurrent.BlockingQueue#remainingCapacity()
     */
    public int remainingCapacity() {
        return matchingFiles.remainingCapacity();
    }

    /**
     * @param o
     * @return
     * @see java.util.concurrent.BlockingQueue#remove(java.lang.Object)
     */
    public boolean remove(Object o) {
        return matchingFiles.remove(o);
    }

    /**
     * @param o
     * @return
     * @see java.util.concurrent.BlockingQueue#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        return matchingFiles.contains(o);
    }

    /**
     * @param c
     * @return
     * @see java.util.concurrent.BlockingQueue#drainTo(java.util.Collection)
     */
    public int drainTo(Collection<? super File> c) {
        return matchingFiles.drainTo(c);
    }

    /**
     * @param c
     * @param maxElements
     * @return
     * @see java.util.concurrent.BlockingQueue#drainTo(java.util.Collection, int)
     */
    public int drainTo(Collection<? super File> c, int maxElements) {
        return matchingFiles.drainTo(c, maxElements);
    }

    /**
     * @param c
     * @return
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> c) {
        return matchingFiles.containsAll(c);
    }

    /**
     * @param c
     * @return
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(Collection<? extends File> c) {
        return matchingFiles.addAll(c);
    }

    /**
     * @param c
     * @return
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c) {
        return matchingFiles.removeAll(c);
    }

    /**
     * @param filter
     * @return
     * @see java.util.Collection#removeIf(java.util.function.Predicate)
     */
    public boolean removeIf(Predicate<? super File> filter) {
        return matchingFiles.removeIf(filter);
    }

    /**
     * @param c
     * @return
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection<?> c) {
        return matchingFiles.retainAll(c);
    }

    /**
     * 
     * @see java.util.Collection#clear()
     */
    public void clear() {
        matchingFiles.clear();
    }

    /**
     * @return
     * @see java.util.Collection#spliterator()
     */
    public Spliterator<File> spliterator() {
        return matchingFiles.spliterator();
    }

    /**
     * @return
     * @see java.util.Collection#stream()
     */
    public Stream<File> stream() {
        return matchingFiles.stream();
    }

    /**
     * @return
     * @see java.util.Collection#parallelStream()
     */
    public Stream<File> parallelStream() {
        return matchingFiles.parallelStream();
    }

    public Set<CodeBaseRootFile> getCodeBaseRoots() {
        return codeBaseRoots;
    }

    public static class CodeBaseBuilder {

        private boolean recursive = true;
        private Set<CodeBaseRootFile> codeBaseRoots = new LinkedHashSet<>();

        private CodeBaseBuilder(final Set<CodeBaseRootFile> pCodeBaseRoots) {
            codeBaseRoots.addAll(pCodeBaseRoots);
        }

        public static CodeBaseBuilder fromRoots(final String... pCodeBaseRoots) {
            return CodeBaseBuilder.fromRoots(asList(pCodeBaseRoots).stream().map(File::new).collect(Collectors.toSet()));
        }

        public static CodeBaseBuilder fromRoots(final File... pCodeBaseRoots) {
            return fromRoots(new LinkedHashSet<>(asList(pCodeBaseRoots)));
        }

        public static CodeBaseBuilder fromRoots(final Set<File> pCodeBaseRoots) {
            return new CodeBaseBuilder(pCodeBaseRoots.stream().map(CodeBaseRootFile::fromFile).collect(Collectors.toSet()));
        }

        public CodeBaseBuilder addRoot(final String pRootToAdd) {
            return addRoot(new File(pRootToAdd));
        }

        public CodeBaseBuilder addRoot(final File pRootToAdd) {
            codeBaseRoots.add(CodeBaseRootFile.fromFile(pRootToAdd));
            return this;
        }

        public CodeBaseBuilder recursive(final boolean pRecursive) {
            recursive = pRecursive;
            return this;
        }

        public CodeBase build() {
            codeBaseRoots.forEach(f -> {
                if (!f.getReferencedFile().exists()) {
                    throw new FileNotFoundExceptionException(f.getReferencedFile().getAbsolutePath() + " not found!");
                }
            });
            return new CodeBase(codeBaseRoots, recursive);
        }

        public CodeBaseBuilder addJarRoot(final String pPathToJar) {
            addJarRoot(new File(pPathToJar));
            return this;
        }

        public CodeBaseBuilder addJarRoot(final File pJar) {
            codeBaseRoots.add(CodeBaseRootFile.fromJar(pJar));
            return this;
        }

        public class FileNotFoundExceptionException extends IllegalArgumentException {

            public FileNotFoundExceptionException(String string) {
                super(string);
            }

            private static final long serialVersionUID = 1L;

        }

    }
}
