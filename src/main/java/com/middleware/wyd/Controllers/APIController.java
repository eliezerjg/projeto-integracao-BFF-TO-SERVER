package com.middleware.wyd.Controllers;


import com.middleware.wyd.Services.IntegrationAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@RestController()
@RequestMapping("/api")
public class APIController {

    @Autowired
    IntegrationAPIService service;

    @PostMapping("/addaccount")
    public ResponseEntity<?> addAccount(@RequestParam String user, @RequestParam String pass) throws IOException {
        try{
            return ResponseEntity.ok(service.addAccount(user, pass));
        }
        catch (Exception ex){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/dologin")
    public ResponseEntity<?> doLogin(@RequestParam String user, @RequestParam String pass) throws IOException {
        try{
            return ResponseEntity.ok(service.dologin(user, pass));
        }catch (Exception ex){
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/accountexists")
    public ResponseEntity<?> accountExists(@RequestParam String user) {
        try{
            return ResponseEntity.ok(service.accountExists(user));
        }
        catch (Exception ex){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/alterpass")
    public ResponseEntity<?> alterPass(@RequestParam String user, @RequestParam String pass, @RequestParam String newPass) {
        try{
            return ResponseEntity.ok(service.alterPass(user, pass, newPass));
        }
        catch (Exception ex){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/getranking")
    public ResponseEntity<?> getRanking() {
        try{
            return ResponseEntity.ok(service.getRanking());
        }
        catch (Exception ex){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getdroplist")
    public ResponseEntity<?> getDroplist() {
        try{
            return ResponseEntity.ok(service.getDroplist());
        }
        catch (Exception ex){
            return ResponseEntity.notFound().build();
        }
    }

}
