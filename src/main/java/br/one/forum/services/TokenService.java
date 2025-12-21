package br.one.forum.services;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class TokenService {
    public String extractTokenFromRequest(HttpServletRequest request) {
        var  header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer")) {
            return header.replace("Bearer", "").trim();
        }
        return null;
    }

    public DecodedJWT validateToken(String token) {
        return null;
    }
}
