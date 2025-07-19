package CommunityApplication.Services.JWT;

public interface JwtServices {
    String generateToken(String username);
    String extractUsername(String token);
    boolean validateToken(String token, String username);
}
