package sakura.kooi.NMSTranslator;

import java.util.HashMap;
import java.util.HashSet;

class ImplClassLoader extends ClassLoader {
    ImplClassLoader(ClassLoader parent) {
        super(parent);
    }

    private final HashMap<String, byte[]> waitingLoadClasses = new HashMap<>();
    private final HashSet<String> loadedClasses = new HashSet<>();

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        if (waitingLoadClasses.containsKey(name)) {
            final byte[] bytecode = waitingLoadClasses.get(name);
            waitingLoadClasses.remove(name);
            loadedClasses.add(name);
            return defineClass(name, bytecode, 0, bytecode.length);
        }
        return super.findClass(name);
    }

    private void addClass(final String name, final byte[] bytecode) {
        waitingLoadClasses.put(name, bytecode);
    }

    public boolean isLoaded(final String name) {
        return loadedClasses.contains(name);
    }

    public Class<?> loadClass(final byte[] bytecode, final String clsName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> cls;
        if (!isLoaded(clsName)) {
            addClass(clsName, bytecode);
        }
        cls = Class.forName(clsName, true, this);
        return cls;
    }
}

