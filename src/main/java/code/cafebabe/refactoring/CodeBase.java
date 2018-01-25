package code.cafebabe.refactoring;

import static java.util.Arrays.asList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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

	private final BlockingQueue<File> matchingFiles;
	private final Set<CodeBaseRootFile> codeBaseRoots;

	/**
	 * Pattern which files have to satisfy for being added as jar file roots to
	 * a codebase
	 */
	private static final List<Pattern> JAR_FILE_PATTERN = asList(Pattern.compile(".*\\.jar"));

	/**
	 * Pattern which files have to satisfy for being added as source files to a
	 * codebase
	 */
	private static final List<Pattern> SOURCE_FILE_PATTERN = asList(Pattern.compile(".*\\.java"));

	/**
	 * Pattern which directories have to satisfy for having their contained
	 * files added as source files to a codebase
	 */
	private static final List<Pattern> DIRECTORY_TO_INCLUDE_PATTERN = new ArrayList<>();

	private CodeBase(final Set<CodeBaseRootFile> pCodeBaseRoots, final boolean pRecursive) {
		this(pCodeBaseRoots, new HashSet<>(asList(Pattern.compile(".*"))), pRecursive);
	}

	private CodeBase(final Set<CodeBaseRootFile> pCodeBaseRoots, final Set<Pattern> pPackagesToInclude,
			final boolean pRecursive) {
		matchingFiles = new LinkedBlockingQueue<>();
		if (pPackagesToInclude != null) {
			DIRECTORY_TO_INCLUDE_PATTERN.addAll(pPackagesToInclude);
		}
		codeBaseRoots = Collections.unmodifiableSet(new LinkedHashSet<>(pCodeBaseRoots));
		codeBaseRoots.stream()
				.filter(CodeBaseRootFile::isSourceFile).flatMap(f -> FileUtil.getMatchingFiles(f.getReferencedFile(),
						SOURCE_FILE_PATTERN, DIRECTORY_TO_INCLUDE_PATTERN, pRecursive).stream())
				.forEach(matchingFiles::add);
	}

	@Override
	public String toString() {
		return matchingFiles.toString();
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
	 * @see java.util.concurrent.BlockingQueue#offer(java.lang.Object, long,
	 *      java.util.concurrent.TimeUnit)
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
	 * @see java.util.concurrent.BlockingQueue#poll(long,
	 *      java.util.concurrent.TimeUnit)
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
	 * @see java.util.concurrent.BlockingQueue#drainTo(java.util.Collection,
	 *      int)
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

	public static final class CodeBaseBuilder {

		private boolean recursive = true;
		private Set<CodeBaseRootFile> codeBaseRoots = new LinkedHashSet<>();
		private Set<Pattern> packagesToInclude = new HashSet<>();

		private CodeBaseBuilder(final Collection<CodeBaseRootFile> pCodeBaseRoots) {
			codeBaseRoots.addAll(pCodeBaseRoots);
		}

		public static CodeBaseBuilder fromRoots(final String... pCodeBaseRoots) {
			return CodeBaseBuilder
					.fromRoots(asList(pCodeBaseRoots).stream().map(File::new).collect(Collectors.toSet()));
		}

		public static CodeBaseBuilder fromRoots(final File... pCodeBaseRoots) {
			return fromRoots(new LinkedHashSet<>(asList(pCodeBaseRoots)));
		}

		public static CodeBaseBuilder fromRoots(final Set<File> pCodeBaseRoots) {
			final Map<Boolean, List<CodeBaseRootFile>> collect = pCodeBaseRoots.stream().map(CodeBaseRootFile::fromFile)
					.collect(Collectors.partitioningBy(CodeBaseRootFile::exists));
			if (!collect.get(false).isEmpty()) {
				final CodeBaseRootFile firstMissingRoot = collect.get(false).get(0);
				throwFileNotFoundException(firstMissingRoot.getReferencedFile());
			}
			return new CodeBaseBuilder(collect.get(true));
		}

		public CodeBaseBuilder addRoot(final String pRootToAdd) {
			return addRoot(new File(pRootToAdd));
		}

		public CodeBaseBuilder packagesToInclude(final Collection<Pattern> pPackagesToInclude) {
			packagesToInclude.addAll(pPackagesToInclude);
			return this;
		}

		public CodeBaseBuilder addRoot(final File pRootToAdd) {
			assertFileExists(pRootToAdd);
			codeBaseRoots.add(CodeBaseRootFile.fromFile(pRootToAdd));
			return this;
		}

		private static void assertFileExists(final File pFileToCheck) {
			if (!pFileToCheck.exists()) {
				throwFileNotFoundException(pFileToCheck);
			}
		}

		private static void throwFileNotFoundException(final File pMissingFile) {
			throw new FileNotFoundException(pMissingFile.getAbsolutePath() + " not found!");
		}

		public CodeBaseBuilder recursive(final boolean pRecursive) {
			recursive = pRecursive;
			return this;
		}

		public CodeBase build() {
			if (!packagesToInclude.isEmpty()) {
				return new CodeBase(codeBaseRoots, packagesToInclude, recursive);
			} else {
				return new CodeBase(codeBaseRoots, recursive);
			}
		}

		public CodeBaseBuilder addJarRoot(final String pPathToJar) {
			addJarRoot(new File(pPathToJar));
			return this;
		}

		public CodeBaseBuilder addJarRoot(final File pJar) {
			assertFileExists(pJar);
			codeBaseRoots.addAll(expandJarRootDirectory(pJar));
			return this;
		}

		private Collection<? extends CodeBaseRootFile> expandJarRootDirectory(final File pJarToExpand) {
			if (!pJarToExpand.isDirectory()) {
				return asList(CodeBaseRootFile.fromJar(pJarToExpand));
			}

			return FileUtil.getMatchingFiles(pJarToExpand, CodeBase.JAR_FILE_PATTERN, true).stream()
					.map(CodeBaseRootFile::fromJar).collect(Collectors.toSet());
		}

		public static final class FileNotFoundException extends IllegalArgumentException {
			private static final long serialVersionUID = 1L;

			public FileNotFoundException(String string) {
				super(string);
			}
		}

	}
}
