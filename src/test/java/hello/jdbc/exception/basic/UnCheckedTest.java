package hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnCheckedTest {

	@Test
	void unchecked_catch() {
		Service service = new Service();
		service.callCatch();
	}

	@Test
	void unchecked_throw() {
		Service service = new Service();
		Assertions.assertThatThrownBy(() -> service.callThrow())
			.isInstanceOf(MyUnCheckedException.class);
	}

	/**
	 * RuntimeException을 상속받은 예외는 UnCheckedException
	 */
	static class MyUnCheckedException extends RuntimeException {
		public MyUnCheckedException(String message) {
			super(message);
		}
	}

	static class Repository {
		public void call() {
			throw new MyUnCheckedException("ex");
		}
	}

	/**
	 * UnChecked 예외는
	 * 예외를 잡거나, 던지지 않아도 된다.
	 * 예외를 잡지 않으면 자동으로 밖으로 던진다.
	 */
	static class Service {
		Repository repository = new Repository();

		/**
		 * 필요한 경우 잡으면 된다
		 */
		public void callCatch() {
			try {
				repository.call();
			} catch (MyUnCheckedException e) {
				log.info("예외 처리, message = {}", e.getMessage(), e);
			}
		}

		/**
		 * 예외를 잡지 않아도 된다.
		 * 체크 예외와 다르게 throws 예외 선언을 하지 않아도 된다.
		 */
		public void callThrow() {
			repository.call();
		}
	}

}
