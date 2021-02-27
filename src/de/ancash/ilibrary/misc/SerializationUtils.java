package de.ancash.ilibrary.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Assists with the serialization process and performs additional functionality based
 * on serialization.</p>
 *
 * <ul>
 * <li>Deep clone using serialization
 * <li>Serialize managing finally and IOException
 * <li>Deserialize managing finally and IOException
 * </ul>
 *
 * <p>This class throws exceptions for invalid {@code null} inputs.
 * Each method documents its behavior in more detail.</p>
 *
 * <p>#ThreadSafe#</p>
 * @since 1.0
 */
public class SerializationUtils {

    /**
     * <p>Custom specialization of the standard JDK {@link java.io.ObjectInputStream}
     * that uses a custom  {@code ClassLoader} to resolve a class.
     * If the specified {@code ClassLoader} is not able to resolve the class,
     * the context classloader of the current thread will be used.
     * This way, the standard deserialization work also in web-application
     * containers and application servers, no matter in which of the
     * {@code ClassLoader} the particular class that encapsulates
     * serialization/deserialization lives. </p>
     *
     * <p>For more in-depth information about the problem for which this
     * class here is a workaround, see the JIRA issue LANG-626. </p>
     */
     static class ClassLoaderAwareObjectInputStream extends ObjectInputStream {
        private static final Map<String, Class<?>> primitiveTypes =
                new HashMap<>();

        static {
            primitiveTypes.put("byte", byte.class);
            primitiveTypes.put("short", short.class);
            primitiveTypes.put("int", int.class);
            primitiveTypes.put("long", long.class);
            primitiveTypes.put("float", float.class);
            primitiveTypes.put("double", double.class);
            primitiveTypes.put("boolean", boolean.class);
            primitiveTypes.put("char", char.class);
            primitiveTypes.put("void", void.class);
        }

        private final ClassLoader classLoader;

        /**
         * Constructor.
         * @param in The {@code InputStream}.
         * @param classLoader classloader to use
         * @throws IOException if an I/O error occurs while reading stream header.
         * @see java.io.ObjectInputStream
         */
        ClassLoaderAwareObjectInputStream(final InputStream in, final ClassLoader classLoader) throws IOException {
            super(in);
            this.classLoader = classLoader;
        }

        /**
         * Overridden version that uses the parameterized {@code ClassLoader} or the {@code ClassLoader}
         * of the current {@code Thread} to resolve the class.
         * @param desc An instance of class {@code ObjectStreamClass}.
         * @return A {@code Class} object corresponding to {@code desc}.
         * @throws IOException Any of the usual Input/Output exceptions.
         * @throws ClassNotFoundException If class of a serialized object cannot be found.
         */
        @Override
        protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            final String name = desc.getName();
            try {
                return Class.forName(name, false, classLoader);
            } catch (final ClassNotFoundException ex) {
                try {
                    return Class.forName(name, false, Thread.currentThread().getContextClassLoader());
                } catch (final ClassNotFoundException cnfe) {
                    final Class<?> cls = primitiveTypes.get(name);
                    if (cls != null) {
                        return cls;
                    }
                    throw cnfe;
                }
            }
        }

    }

    /**
     * <p>Deep clone an {@code Object} using serialization.</p>
     *
     * <p>This is many times slower than writing clone methods by hand
     * on all objects in your object graph. However, for complex object
     * graphs, or for those that don't support deep cloning this can
     * be a simple alternative implementation. Of course all the objects
     * must be {@code Serializable}.</p>
     *
     * @param <T> the type of the object involved
     * @param object  the {@code Serializable} object to clone
     * @return the cloned object
     * @throws SerializationException (runtime) if the serialization fails
     */
    public static <T extends Serializable> T clone(final T object) {
        if (object == null) {
            return null;
        }
        final byte[] objectData = serialize(object);
        final ByteArrayInputStream bais = new ByteArrayInputStream(objectData);

        try (ClassLoaderAwareObjectInputStream in = new ClassLoaderAwareObjectInputStream(bais,
                object.getClass().getClassLoader())) {
            /*
             * when we serialize and deserialize an object,
             * it is reasonable to assume the deserialized object
             * is of the same type as the original serialized object
             */
            @SuppressWarnings("unchecked") // see above
            final T readObject = (T) in.readObject();
            return readObject;

        } catch (final ClassNotFoundException ex) {
        	ex.printStackTrace();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
		return null;
    }

    /**
     * <p>
     * Deserializes a single {@code Object} from an array of bytes.
     * </p>
     *
     * <p>
     * If the call site incorrectly types the return value, a {@link ClassCastException} is thrown from the call site.
     * Without Generics in this declaration, the call site must type cast and can cause the same ClassCastException.
     * Note that in both cases, the ClassCastException is in the call site, not in this method.
     * </p>
     *
     * @param <T>  the object type to be deserialized
     * @param objectData
     *            the serialized object, must not be null
     * @return the deserialized object
     * @throws Exception 
     * @throws NullPointerException if {@code objectData} is {@code null}
     * @throws SerializationException (runtime) if the serialization fails
     */
    public static <T> T deserialize(final byte[] objectData) throws Exception {
        Validate.notNull(objectData, "objectData");
        return deserialize(new ByteArrayInputStream(objectData));
    }

    /**
     * <p>
     * Deserializes an {@code Object} from the specified stream.
     * </p>
     *
     * <p>
     * The stream will be closed once the object is written. This avoids the need for a finally clause, and maybe also
     * exception handling, in the application code.
     * </p>
     *
     * <p>
     * The stream passed in is not buffered internally within this method. This is the responsibility of your
     * application if desired.
     * </p>
     *
     * <p>
     * If the call site incorrectly types the return value, a {@link ClassCastException} is thrown from the call site.
     * Without Generics in this declaration, the call site must type cast and can cause the same ClassCastException.
     * Note that in both cases, the ClassCastException is in the call site, not in this method.
     * </p>
     *
     * @param <T>  the object type to be deserialized
     * @param inputStream
     *            the serialized object input stream, must not be null
     * @return the deserialized object
     * @throws Exception 
     * @throws NullPointerException if {@code inputStream} is {@code null}
     * @throws SerializationException (runtime) if the serialization fails
     */
    public static <T> T deserialize(final InputStream inputStream) throws Exception {
        Validate.notNull(inputStream, "inputStream");
        ObjectInputStream in = null;
        while(in == null) {
        	try {
        		in = new ObjectInputStream(inputStream);
        	} catch(Exception e) {
        		throw new Exception(e.getMessage());
        	}
        }
        try{
            @SuppressWarnings("unchecked")
            final T obj = (T) in.readObject();
            return obj;
        } catch (final ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Performs a serialization roundtrip. Serializes and deserializes the given object, great for testing objects that
     * implement {@link Serializable}.
     *
     * @param <T>
     *           the type of the object involved
     * @param obj
     *            the object to roundtrip
     * @return the serialized and deserialized object
     * @throws Exception 
     * @since 3.3
     */
    @SuppressWarnings("unchecked") // OK, because we serialized a type `T`
    public static <T extends Serializable> T roundtrip(final T obj) throws Exception {
        return (T) deserialize(serialize(obj));
    }

    /**
     * <p>Serializes an {@code Object} to a byte array for
     * storage/serialization.</p>
     *
     * @param obj  the object to serialize to bytes
     * @return a byte[] with the converted Serializable
     * @throws SerializationException (runtime) if the serialization fails
     */
    public static byte[] serialize(final Serializable obj) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        serialize(obj, baos);
        return baos.toByteArray();
    }

    /**
     * <p>Serializes an {@code Object} to the specified stream.</p>
     *
     * <p>The stream will be closed once the object is written.
     * This avoids the need for a finally clause, and maybe also exception
     * handling, in the application code.</p>
     *
     * <p>The stream passed in is not buffered internally within this method.
     * This is the responsibility of your application if desired.</p>
     *
     * @param obj  the object to serialize to bytes, may be null
     * @param outputStream  the stream to write to, must not be null
     * @throws NullPointerException if {@code outputStream} is {@code null}
     * @throws SerializationException (runtime) if the serialization fails
     */
    public static void serialize(final Serializable obj, final OutputStream outputStream) {
        Validate.notNull(outputStream, "outputStream");
        try (ObjectOutputStream out = new ObjectOutputStream(outputStream)) {
            out.writeObject(obj);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * <p>SerializationUtils instances should NOT be constructed in standard programming.
     * Instead, the class should be used as {@code SerializationUtils.clone(object)}.</p>
     *
     * <p>This constructor is public to permit tools that require a JavaBean instance
     * to operate.</p>
     * @since 2.0
     */
    public SerializationUtils() {
    }

}
