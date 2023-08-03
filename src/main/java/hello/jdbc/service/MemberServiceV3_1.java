package hello.jdbc.service;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 트랜잭션 - 트랜잭션 매니저
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

	private final PlatformTransactionManager transactionManager;
	private final MemberRepositoryV3 memberRepository;

	private static void validation(Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("이체중 예외 발생");
		}
	}

	public void accountTransfer(String formId, String toId, int money) throws SQLException {
		//트랜잭션 시작
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

		try {
			//비지니스 로직 시작
			bizLogic(formId, toId, money);
			transactionManager.commit(status);//성공 시 커밋
		} catch (Exception e) {
			transactionManager.rollback(status);
			throw new IllegalStateException(e);
		}

	}

	private void bizLogic(String formId, String toId, int money) throws SQLException {
		Member fromMember = memberRepository.findById(formId);
		Member toMember = memberRepository.findById(toId);

		memberRepository.update(formId, fromMember.getMoney() - money);
		validation(toMember);
		memberRepository.update(toId, toMember.getMoney() + money);
	}
}
