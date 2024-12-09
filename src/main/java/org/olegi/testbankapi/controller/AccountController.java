package org.olegi.testbankapi.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.olegi.testbankapi.dto.AccountDTO;
import org.olegi.testbankapi.exceptions.UserNotFoundException;
import org.olegi.testbankapi.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@AllArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/createAccount")
    public ResponseEntity<?> createAccount(@RequestBody AccountDTO accountDTO) {
        accountService.createAccount(accountDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/updateUser/{accountId}")
    public ResponseEntity<?> updateUser(@PathVariable String accountId, AccountDTO accountDTO) {
        try {
            log.info("Trying to update user (Controller)");
            accountService.updateUser(accountId, accountDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @GetMapping("/getUserInfo/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long id) {
        try {
            log.info("Trying to retrieve user info (Controller)");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authUserName = authentication.getName();
            UserDTO userDTO = userService.getUserInfo(id, authUserName);
            return ResponseEntity.ok(userDTO);
        } catch (UserNotFoundException e) {
            log.error("User with id '{}' not found", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (AuthorizationServiceException e) {
            log.error("Unauthorized", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You do not have permission to view this user");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            log.info("Trying to delete user with id {} (Controller)", id);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authUserName = authentication.getName();
            userService.deleteUser(id, authUserName);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            log.error("User with id '{}' not found", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
        } catch (AuthorizationServiceException e) {
            log.error("Unauthorized", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You do not have permission to delete this user");
        }
    }


}
