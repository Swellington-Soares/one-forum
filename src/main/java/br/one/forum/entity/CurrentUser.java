package br.one.forum.entity;

public record CurrentUser(User user) {
    public boolean isOwner(Long id) {
        return user != null && user.getId().equals(id);
    }
}
