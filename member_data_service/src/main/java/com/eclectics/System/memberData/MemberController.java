package com.eclectics.System.memberData;

import com.eclectics.System.Utils.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("api/v1/member-data")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
    @PostMapping("/add")
    public ResponseEntity<?> addMemberData(@RequestBody Member member) {
        try {
            return ResponseEntity.ok().body(memberService.addMemberData(member));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getMemberData() {
        try {
            return ResponseEntity.ok().body(memberService.getMemberData());
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    @PutMapping("/update")
    public ResponseEntity<?> update(Member member) {
        try {
            return ResponseEntity.ok().body(memberService.update(member));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(memberService.delete(id));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}
