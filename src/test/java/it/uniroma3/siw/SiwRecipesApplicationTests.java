package it.uniroma3.siw;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class SiwRecipesApplicationTests {

    @Test
    void generatePasswordHashes() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("HASH admin    = " + encoder.encode("admin"));
        System.out.println("HASH gio1     = " + encoder.encode("gio1"));
        System.out.println("HASH paolo1   = " + encoder.encode("paolo1"));
        System.out.println("HASH alessia1 = " + encoder.encode("alessia1"));
    }
}
