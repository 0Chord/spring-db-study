package hello.jdbc.service;

import org.springframework.transaction.annotation.Transactional;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 예외 문제 해결
 * SQLException 해결
 * MemberRepository Interface에 의존
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV4 {
	;
	private final MemberRepository memberRepository;

	private static void validation(Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("이체중 예외 발생");
		}
	}

	@Transactional
	public void accountTransfer(String formId, String toId, int money) {
		bizLogic(formId, toId, money);

	}

	private void bizLogic(String formId, String toId, int money) {
		Member fromMember = memberRepository.findById(formId);
		Member toMember = memberRepository.findById(toId);

		memberRepository.update(formId, fromMember.getMoney() - money);
		validation(toMember);
		memberRepository.update(toId, toMember.getMoney() + money);
	}
}
