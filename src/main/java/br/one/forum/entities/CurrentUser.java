package br.one.forum.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CurrentUser {

    private final User user;

    public boolean isOwner( Integer id ) {
        return user != null && user.getId().equals(id);
    }

}
