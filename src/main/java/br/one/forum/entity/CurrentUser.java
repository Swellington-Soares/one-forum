package br.one.forum.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CurrentUser {
    private final User user;
    public boolean isOwner( Long id ) {
        return user != null && user.getId().equals(id);
    }
}
