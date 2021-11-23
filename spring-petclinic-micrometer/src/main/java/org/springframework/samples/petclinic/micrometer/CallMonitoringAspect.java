import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.util.StopWatch;

/**
 * Simple aspect that monitors call count and call invocation time. It uses JMX
 * annotations and therefore can be monitored using any JMX console such as the jConsole
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Michael Isvy
 * @since 2.5
 */
@ManagedResource(objectName = "petclinic:type=CallMonitor")
@Aspect
public class CallMonitoringAspect {

	private boolean enabled = true;

	private int callCount = 0;

	private long accumulatedCallTime = 0;

	@ManagedAttribute
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@ManagedAttribute
	public boolean isEnabled() {
		return enabled;
	}

	@ManagedOperation
	public void reset() {
		this.callCount = 0;
		this.accumulatedCallTime = 0;
	}

	@ManagedAttribute
	public int getCallCount() {
		return callCount;
	}

	@ManagedAttribute
	public long getCallTime() {
		if (this.callCount > 0) {
			return this.accumulatedCallTime / this.callCount;
		}
		else {
			return 0;
		}
	}

	@Around("@annotation(Monitored)")
	public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {
		if (this.enabled) {
			StopWatch sw = new StopWatch(joinPoint.toShortString());

			sw.start("invoke");
			try {
				return joinPoint.proceed();
			}
			finally {
				sw.stop();
				synchronized (this) {
					this.callCount++;
					this.accumulatedCallTime += sw.getTotalTimeMillis();
				}
			}
		}
		else {
			return joinPoint.proceed();
		}
	}

}
