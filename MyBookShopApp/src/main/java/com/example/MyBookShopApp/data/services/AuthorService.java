package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.Author;
import com.example.MyBookShopApp.data.repositories.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Map<String, List<Author>> getAuthorsMap() {
       List<Author> listAuthors = authorRepository.findAll();
        return listAuthors.stream().collect(Collectors.groupingBy((Author a) -> {return a.getName().substring(0,1);}));
    }

    public Author getAuthorBySlug(String slug){
        return authorRepository.findBySlug(slug);
    }

}
