package hello.jdbc.service;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 트랜잭션 - 파라미터 연동
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

	private final DataSource dataSource;
	private final MemberRepositoryV2 memberRepository;

	private static void validation(Member toMember) {
		if (toMember.getMemberId().equals("ex")) {
			throw new IllegalStateException("이체중 예외 발생");
		}
	}

	private static void release(Connection con) {
		if (con != null) {
			try {
				con.setAutoCommit(true); //커넥션 풀 고려
				con.close();
			} catch (Exception e) {
				log.info("error", e);
			}
		}
	}

	public void accountTransfer(String formId, String toId, int money) throws SQLException {
		Connection con = dataSource.getConnection();
		try {
			con.setAutoCommit(false);//트랜잭션 시작
			//비지니스 로직 시작
			bizLogic(con, formId, toId, money);
			con.commit();//성공 시 커밋
		} catch (Exception e) {
			con.rollback();
			throw new IllegalStateException(e);
		} finally {
			release(con);
		}

	}

	private void bizLogic(Connection con, String formId, String toId, int money) throws SQLException {
		Member fromMember = memberRepository.findById(con, formId);
		Member toMember = memberRepository.findById(con, toId);

		memberRepository.update(con, formId, fromMember.getMoney() - money);
		validation(toMember);
		memberRepository.update(con, toId, toMember.getMoney() + money);
	}
}
