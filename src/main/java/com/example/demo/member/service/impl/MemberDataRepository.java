package com.example.demo.member.service.impl;


import com.example.demo.member.entity.Member;
import com.example.demo.member.entity.MemberDto;
import com.example.demo.member.service.UsernameOnly;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberDataRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    @Query("select m from Member m where m.username = :username")
    List<Member> findUser(@Param("username") String username);

    @Query("select new com.example.demo.member.entity.MemberDto(m.id, m.username, t.teamName) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    Page<Member> findByUsername(String username, Pageable pageable);

    @Modifying
    @Query("update Member m set m.age = m.age +1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @QueryHints(value=@QueryHint(name="org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    List<UsernameOnly> findProjectionsByUsername();

    @Query(value = "select * from member where useranme = ?", nativeQuery = true)
    Member findByNativeQuery(String username);
}
