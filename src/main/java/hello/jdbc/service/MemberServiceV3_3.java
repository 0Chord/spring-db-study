package hello.jdbc.service;

import java.sql.SQLException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Transactional AOP
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_3 {
	;
	private final MemberRepositoryV3 memberRepository;

	private static void validation(Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("이체중 예외 발생");
		}
	}

	@Transactional
	public void accountTransfer(String formId, String toId, int money) throws SQLException {
		bizLogic(formId, toId, money);

	}

	private void bizLogic(String formId, String toId, int money) throws SQLException {
		Member fromMember = memberRepository.findById(formId);
		Member toMember = memberRepository.findById(toId);

		memberRepository.update(formId, fromMember.getMoney() - money);
		validation(toMember);
		memberRepository.update(toId, toMember.getMoney() + money);
	}
}
