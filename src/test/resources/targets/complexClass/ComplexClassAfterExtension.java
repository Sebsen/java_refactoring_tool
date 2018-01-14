package complexClass;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import complexClass.custom.Logger;
import complexClass.custom.MessageLogger;

public class ComplexClass {

    private static final Logger logger = MessageLogger.instance();
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ComplexClass.class);

    public void print(final Object pObject) {
        if (pObject instanceof String) {
            print((String) pObject);
        } else if (pObject instanceof Exception) {
            print((Exception) pObject);
        }
    }

    public void print(final String pStringToLog) {
        logger.info(pStringToLog);
        LOGGER.info(pStringToLog);
        LOGGER.info(pStringToLog);
    }

    public void print(final Exception pExceptionToLog) {
        logger.error("ERROR", pExceptionToLog);
        LOGGER.error("ERROR", pExceptionToLog);
        org.slf4j.Logger logger = LOGGER;
        try {
            logger.error("Error!", cast(pExceptionToLog));
        } catch (IllegalExceptionException e) {
            logger.error("Error casting Exception to CoreException!");
        }
        ComplexClass.logger.error("Over");
        ComplexClass.LOGGER.error("Over");
        logger.error("Over");
    }

    /*
    public Set<String> methodContainingComplexStreamExpression() {
        final List<Integer> listToStreamThrough = Arrays.asList(5, 8, 16, 21, 64, 64, 128, 111, 113, 7, 61, 2048, 5, -2);
        final Set<String> collected = listToStreamThrough.stream().filter(Objects::nonNull).map(Integer::intValue).filter(e -> e % 2 == 0).filter(e -> e > 0).sorted()
                .map(String::valueOf).collect(Collectors.toSet());
        collected.add("Finish!");
        return collected;
    }*/

    public CoreException cast(final Exception pException) throws IllegalExceptionException {
        if (pException instanceof RuntimeException) {
            throw new IllegalExceptionException();
        } else {
            return new CoreException(pException);
        }

    }

    public static class IllegalExceptionException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    public static class CoreException extends Exception {
        private static final long serialVersionUID = 1L;

        public CoreException(Exception pException) {
        }
    }
}
