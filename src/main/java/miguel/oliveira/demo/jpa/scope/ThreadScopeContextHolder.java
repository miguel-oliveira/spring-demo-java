package miguel.oliveira.demo.jpa.scope;

public class ThreadScopeContextHolder {

  private static final ThreadLocal<ThreadScopeAttributes> threadScopeAttributesHolder = new InheritableThreadLocal<ThreadScopeAttributes>() {
    protected ThreadScopeAttributes initialValue() {
      return new ThreadScopeAttributes();
    }

    protected ThreadScopeAttributes childValue(ThreadScopeAttributes parentValue) {
      return new ThreadScopeAttributes(parentValue);
    }
  };

  public static ThreadScopeAttributes getThreadScopeAttributes() {
    return threadScopeAttributesHolder.get();
  }

  public static void setThreadScopeAttributes(ThreadScopeAttributes accessor) {
    ThreadScopeContextHolder.threadScopeAttributesHolder.set(accessor);
  }

  public static ThreadScopeAttributes currentThreadScopeAttributes() throws IllegalStateException {
    ThreadScopeAttributes accessor = threadScopeAttributesHolder.get();

    if (accessor == null) {
      throw new IllegalStateException("No thread scoped attributes.");
    }

    return accessor;
  }


}