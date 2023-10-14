package com.example.demo.api;

import com.example.demo.member.entity.Member;
import com.example.demo.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;


    //모든 entity가 노출된다. 필요없는 orders 까지...
    //@JsonIgnore 를 추가하면 무시된다.
    //근데 entity에 api를 위한 기능을 추가하게 됐다.
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findAll();
    }

    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> members = memberService.findAll();
        List<MemberDto> collect = members.stream()
                .map(m->new MemberDto(m.getUsername()))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setUsername(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){

        memberService.update(id, request.getName());
        Member findMember = memberService.find(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getUsername());
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private long id;

        public CreateMemberResponse(long id) {
            this.id = id;
        }
    }
}
