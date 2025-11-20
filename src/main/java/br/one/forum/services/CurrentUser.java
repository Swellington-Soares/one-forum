package br.one.forum.services;

import br.one.forum.entities.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CurrentUser {
        private final User user;
}
