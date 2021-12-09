package miguel.oliveira.demo.jpa.scope;

public class ThreadScopeRunnable implements Runnable {

  private final Runnable target;

  public ThreadScopeRunnable(Runnable target) {
    this.target = target;
  }

  public final void run() {
    try {
      target.run();
    } finally {
      ThreadScopeContextHolder.currentThreadScopeAttributes().clear();
    }
  }

}