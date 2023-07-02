package com.eclectics.System.memberData;

import com.eclectics.System.Utils.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public EntityResponse addMemberData(Member member) {
        try {
            EntityResponse response = new EntityResponse();

            Member addData = memberRepository.save(member);
            response.setMessage("Created Successfully");
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setEntity(addData);

            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse getMemberData() {
        try {
            EntityResponse response = new EntityResponse();
            List<Member> member = memberRepository.findAll();
            if (member.size() > 0) {
                response.setMessage("Found");
                response.setStatusCode(HttpStatus.FOUND.value());
                response.setEntity(member);
            } else {
                response.setMessage("Not Found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public EntityResponse update(Member member) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<Member> member1 = memberRepository.findById(member.getId());
            if (member1.isPresent()){
                Member addData = memberRepository.save(member);
                response.setMessage("Update Successfully");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(addData);
            } else {
                response.setMessage("Member Not Found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }

            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public EntityResponse delete(Long id) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<Member> member = memberRepository.findById(id);
            if (member.isPresent()) {
                memberRepository.deleteById(id);
                response.setMessage("Deleted Successfully");
                response.setStatusCode(HttpStatus.OK.value());
            } else {
                response.setMessage("Not Found");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
