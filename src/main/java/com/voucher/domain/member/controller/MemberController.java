package com.voucher.domain.member.controller;

import com.voucher.domain.member.dto.MemberRequest;
import com.voucher.domain.member.dto.MemberResponse;
import com.voucher.domain.member.entity.Member;
import com.voucher.domain.member.repository.MemberRepository;
import com.voucher.domain.point.core.constants.ApiPath;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {
	private final MemberRepository memberRepository;

	//멤버생성
	@PostMapping(path = ApiPath.MEMBER.SIGN_UP)
	public String signUpMember(@RequestBody @Valid MemberRequest.SignUp request) {
		memberRepository.save(Member.builder().memberName(request.memberName()).build());
		return "성공";
	}

	//멤버 이름 목록 조회
	@GetMapping(path = ApiPath.MEMBER.MEMBERS)
	public List<MemberResponse.MemberInfo> findMembersNameList() {
		return memberRepository.findAll().stream().map(MemberResponse.MemberInfo::of).toList();
	}
}
