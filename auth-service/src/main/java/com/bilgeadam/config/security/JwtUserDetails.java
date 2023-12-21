package com.bilgeadam.config.security;

import com.bilgeadam.repository.entity.Auth;
import com.bilgeadam.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtUserDetails implements UserDetailsService {
    private final AuthService authService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    public UserDetails loadUserByUserId(Long id) throws UsernameNotFoundException {
        Optional<Auth> auth = authService.findById(id);
        if (auth.isPresent()){
            //Bir yetkilendirme mekanizmasını ve kullanıcının rollerini temsil eder.
            List<GrantedAuthority> authorityList=new ArrayList<>();
            //SimpleGrantedAuthority --> Kullanıcının tek bir rolü olduğunu varsayar
            authorityList.add(new SimpleGrantedAuthority(auth.get().getRole().toString()));
            /**
             * Rolleri normalde liste şeklinde tutarız ama bu uygulamada tek bir rolü varmış gibi tuttuk.
             * List<UserRoles> rolesList = userRolesRepository.findAllByUserprofileid(userProfile.get().getId());
             *         rolesList.forEach(roles -> {
             *             authorities.add(new SimpleGrantedAuthority(roles.getRole()));
             *         });
             */
            return User.builder()
                    .username(auth.get().getUsername())
                    //uygulamada şifre doğrulama işlemi yapılmayacağı için boş geçilir
                    .password("")
                    //kullanıcnın hesabının süresinin dolmadığını belirtir
                    .accountExpired(false)
                    .accountLocked(false)
                    //kullanıcının her bir işlem için buradaki rol bilgisi karşılaştırılır
                    .authorities(authorityList)
                    .build();
        }
        return null;
    }
}
